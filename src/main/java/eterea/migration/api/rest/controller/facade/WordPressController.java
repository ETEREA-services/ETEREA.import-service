package eterea.migration.api.rest.controller.facade;

import eterea.migration.api.rest.extern.OrderNoteWeb;
import eterea.migration.api.rest.service.facade.OrderNoteWebService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/wordpress")
public class WordPressController {

   private final OrderNoteWebService service;

   public WordPressController(OrderNoteWebService service) {
      this.service = service;
   }

   @GetMapping("/capture")
   @Scheduled(cron = "0 0 * * * *")
   public ResponseEntity<List<OrderNoteWeb>> capture() {
      return new ResponseEntity<>(service.capture(), HttpStatus.OK);
   }

   @GetMapping("/capture/from-apis")
   public ResponseEntity<List<OrderNoteWeb>> captureFromApis(
         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) {

      ZoneId utcMinus3 = ZoneId.of("America/Argentina/Buenos_Aires");
      ZonedDateTime fromDateTime = (from != null)
            ? from
            : ZonedDateTime.now(utcMinus3).toLocalDate().atStartOfDay(utcMinus3);
      ZonedDateTime toDateTime = (to != null)
            ? to
            : ZonedDateTime.now(utcMinus3).toLocalDate().atTime(23, 59, 59).atZone(utcMinus3);

      return new ResponseEntity<>(service.captureFromApi(fromDateTime, toDateTime), HttpStatus.OK);
   }

   @GetMapping("/capture/from-apis/last-twelve-hours")
   @Scheduled(cron = "0 */10 * * * *")
   public ResponseEntity<List<OrderNoteWeb>> captureFromApisLastTwelveHours() {

      ZoneId utcMinus3 = ZoneId.of("America/Argentina/Buenos_Aires");
      ZonedDateTime toDateTime = ZonedDateTime.now(utcMinus3);
      ZonedDateTime fromDateTime = toDateTime.minusHours(12);

      return new ResponseEntity<>(service.captureFromApi(fromDateTime, toDateTime), HttpStatus.OK);
   }

}
