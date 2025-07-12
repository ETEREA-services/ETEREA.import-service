package eterea.migration.api.rest.service;

import eterea.migration.api.rest.exception.OrderNoteException;
import eterea.migration.api.rest.model.OrderNote;
import eterea.migration.api.rest.repository.OrderNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderNoteServiceTest {

    @Mock
    private OrderNoteRepository orderNoteRepository;

    @InjectMocks
    private OrderNoteService orderNoteService;

    private OrderNote orderNote;

    @BeforeEach
    void setUp() {
        orderNote = new OrderNote();
        orderNote.setOrderNumberId(1L);
        orderNote.setBillingEmail("test@example.com");
    }

    @Test
    void whenAddOrderNote_thenRepositorySaveIsCalled() {
        // given
        when(orderNoteRepository.save(any(OrderNote.class))).thenReturn(orderNote);

        // when
        OrderNote saved = orderNoteService.add(orderNote);

        // then
        assertThat(saved).isNotNull();
        verify(orderNoteRepository, times(1)).save(orderNote);
    }

    @Test
    void whenFindByOrderNumberId_andExists_thenReturnOrderNote() {
        // given
        when(orderNoteRepository.findByOrderNumberId(1L)).thenReturn(Optional.of(orderNote));

        // when
        OrderNote found = orderNoteService.findByOrderNumberId(1L);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getOrderNumberId()).isEqualTo(1L);
    }

    @Test
    void whenFindByOrderNumberId_andNotExists_thenThrowOrderNoteException() {
        // given
        when(orderNoteRepository.findByOrderNumberId(anyLong())).thenReturn(Optional.empty());

        // when / then
        assertThrows(OrderNoteException.class, () -> {
            orderNoteService.findByOrderNumberId(1L);
        });
    }

    @Test
    void whenUpdate_andExists_thenSaveAndReturnUpdatedOrderNote() {
        // given
        OrderNote newDetails = new OrderNote();
        newDetails.setBillingEmail("updated@example.com");

        when(orderNoteRepository.findByOrderNumberId(1L)).thenReturn(Optional.of(orderNote));
        when(orderNoteRepository.save(any(OrderNote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderNote updated = orderNoteService.update(newDetails, 1L);

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getBillingEmail()).isEqualTo("updated@example.com");
        verify(orderNoteRepository, times(1)).save(any(OrderNote.class));
    }

    @Test
    void whenUpdate_andNotExists_thenThrowOrderNoteException() {
        // given
        OrderNote newDetails = new OrderNote();
        when(orderNoteRepository.findByOrderNumberId(anyLong())).thenReturn(Optional.empty());

        // when / then
        assertThrows(OrderNoteException.class, () -> {
            orderNoteService.update(newDetails, 1L);
        });
        verify(orderNoteRepository, never()).save(any(OrderNote.class));
    }

    @Test
    void whenFindAllCompletedByLastTwoDays_thenCallRepository() {
        // given
        when(orderNoteRepository.findAllByOrderStatusInAndCompletedDateGreaterThanEqual(anyList(), any())).thenReturn(Collections.singletonList(orderNote));

        // when
        List<OrderNote> result = orderNoteService.findAllCompletedByLastTwoDays();

        // then
        assertThat(result).hasSize(1);
        verify(orderNoteRepository, times(1)).findAllByOrderStatusInAndCompletedDateGreaterThanEqual(anyList(), any());
    }
}