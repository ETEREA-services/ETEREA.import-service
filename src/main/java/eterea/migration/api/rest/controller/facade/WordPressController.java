package eterea.migration.api.rest.controller.facade;

import eterea.migration.api.rest.extern.OrderNoteWeb;
import eterea.migration.api.rest.service.facade.OrderNoteWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wordpress")
public class WordPressController {

    private final OrderNoteWebService service;

    @Autowired
    public WordPressController(OrderNoteWebService service) {
        this.service = service;
    }

    @GetMapping("/capture")
    @Scheduled(cron = "0 0 * * * *")
    public ResponseEntity<List<OrderNoteWeb>> capture() {
        return new ResponseEntity<>(service.capture(), HttpStatus.OK);
    }

}
