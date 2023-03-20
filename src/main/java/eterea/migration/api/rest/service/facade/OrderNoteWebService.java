package eterea.migration.api.rest.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eterea.migration.api.rest.extern.OrderNoteWeb;
import eterea.migration.api.rest.extern.PaymentWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class OrderNoteWebService {

    public List<OrderNoteWeb> capture() {
        ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File("/data/temp/agencia/orders-2023-03-11-15.json");
        List<OrderNoteWeb> orderNotes = null;
        try {
            orderNotes = objectMapper.readValue(file, new TypeReference<List<OrderNoteWeb>>() {
            });
            for (OrderNoteWeb orderNote : orderNotes) {
                Integer inicioPago = orderNote.getOrderNotes().indexOf("PlusPago");
                Integer finPago = orderNote.getOrderNotes().indexOf("Una nueva reserva");
                String plusPagoString = "";
                if (inicioPago > -1 && finPago > -1) {
                    plusPagoString = orderNote.getOrderNotes().substring(inicioPago + 8, finPago - 1);
                    PaymentWeb payment = objectMapper.readValue(plusPagoString, PaymentWeb.class);
                    orderNote.setPayment(payment);
                }
            }
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException", e.getMessage());
        } catch (IOException e) {
            log.debug("IOException", e.getMessage());
        }
        return orderNotes;

    }

}
