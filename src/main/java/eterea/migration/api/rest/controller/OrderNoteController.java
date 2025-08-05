package eterea.migration.api.rest.controller;

import eterea.migration.api.rest.exception.OrderNoteException;
import eterea.migration.api.rest.model.OrderNote;
import eterea.migration.api.rest.service.OrderNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping({"/orderNote", "/api/import/orderNote"})
public class OrderNoteController {

    private final OrderNoteService service;

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

    @GetMapping("/documento/last/{numeroDocumento}")
    public ResponseEntity<OrderNote> findLastByNumeroDocumento(@PathVariable Long numeroDocumento) {
        try {
            return ResponseEntity.ok(service.findLastByNumeroDocumento(numeroDocumento));
        } catch (OrderNoteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/documento/last/{numeroDocumento}/importe/{importe}")
    public ResponseEntity<OrderNote> findLastByNumeroDocumentoAndImporte(@PathVariable Long numeroDocumento, @PathVariable BigDecimal importe) {
        try {
            return ResponseEntity.ok(service.findLastByNumeroDocumentoAndImporte(numeroDocumento, importe));
        } catch (OrderNoteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/lastTwoDays")
    public ResponseEntity<List<OrderNote>> findAllCompletedByLastTwoDays() {
        return new ResponseEntity<>(service.findAllCompletedByLastTwoDays(), HttpStatus.OK);
    }

}
