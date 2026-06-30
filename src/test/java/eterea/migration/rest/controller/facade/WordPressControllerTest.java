package eterea.migration.rest.controller.facade;

import eterea.migration.rest.service.facade.OrderNoteWebService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WordPressController.class)
class WordPressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderNoteWebService orderNoteWebService;

    @Test
    @WithMockUser
    void whenCaptureEndpointIsCalled_thenServiceIsInvokedAndReturnsOk() throws Exception {
        // given
        // The capture method returns a List, so we mock it to return an empty list.
        when(orderNoteWebService.capture()).thenReturn(Collections.emptyList());

        // when / then
        mockMvc.perform(get("/api/wordpress/capture"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // Expect an empty JSON array

        // verify that the service method was called exactly once
        verify(orderNoteWebService, times(1)).capture();
    }

    @Test
    @WithMockUser
    void whenExportFeedEndpointIsCalled_thenServiceIsInvokedAndReturnsOk() throws Exception {
        // given
        when(orderNoteWebService.captureFromExportFeed()).thenReturn(Collections.emptyList());

        // when / then
        mockMvc.perform(get("/api/wordpress/capture/from-apis/export-feed"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        // verify that the service method was called exactly once
        verify(orderNoteWebService, times(1)).captureFromExportFeed();
    }
}
