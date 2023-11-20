package eterea.migration.api.rest.controller;

import eterea.migration.api.rest.exception.OrderNoteException;
import eterea.migration.api.rest.kotlin.model.OrderNote;
import eterea.migration.api.rest.service.OrderNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orderNote")
public class OrderNoteController {

    private final OrderNoteService service;

    @Autowired
    public OrderNoteController(OrderNoteService service) {
        this.service = service;
    }

    @GetMapping("/{orderNumberId}")
    public ResponseEntity<OrderNote> findByOrderNumberId(@PathVariable Long orderNumberId) {
        try {
            return new ResponseEntity<>(service.findByOrderNumberId(orderNumberId), HttpStatus.OK);
        } catch (OrderNoteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
