package eterea.migration.api.rest.controller;

import eterea.migration.api.rest.model.OrderNote;
import eterea.migration.api.rest.service.OrderNoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderNoteController.class)
class OrderNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderNoteService orderNoteService;

    private OrderNote orderNote;

    @BeforeEach
    void setUp() {
        orderNote = new OrderNote();
        orderNote.setOrderNumberId(1L);
        orderNote.setBillingEmail("test@example.com");
    }

    @Test
    @WithMockUser
    void givenOrderNoteId_whenGetByOrderNumberId_thenReturnJson() throws Exception {
        // given
        given(orderNoteService.findByOrderNumberId(1L)).willReturn(orderNote);

        // when / then
        mockMvc.perform(get("/orderNote/1") // Corrected path
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumberId", is(1)))
                .andExpect(jsonPath("$.billingEmail", is("test@example.com")));
    }

    @Test
    @WithMockUser
    void givenDocumentNumber_whenGetLastByNumeroDocumento_thenReturnJson() throws Exception {
        // given
        given(orderNoteService.findLastByNumeroDocumento(12345678L)).willReturn(orderNote);

        // when / then
        mockMvc.perform(get("/orderNote/documento/last/12345678") // Corrected path
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumberId", is(1)));
    }
}
