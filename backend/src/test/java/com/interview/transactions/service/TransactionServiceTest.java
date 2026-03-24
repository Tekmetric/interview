package com.interview.transactions.service;

import com.interview.transactions.common.exceptions.TransactionNotFound;
import com.interview.transactions.repo.TransactionRepo;
import com.interview.transactions.repo.entities.TransactionEntity;
import com.interview.transactions.service.dto.Currency;
import com.interview.transactions.service.dto.Status;
import com.interview.transactions.service.dto.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepo repo;

    @InjectMocks
    private TransactionService service;

    private final Instant now = Instant.now();

    private TransactionEntity givenEntity(Long id, BigDecimal amount, String currency, String status) {
        return TransactionEntity.builder()
                .id(id)
                .amount(amount)
                .currency(currency)
                .status(status)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Transaction givenDto(Long id, BigDecimal amount, Currency currency, Status status) {
        return Transaction.builder()
                .id(id)
                .amount(amount)
                .currency(currency)
                .status(status)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void getTransactionById_existing_ReturnsDto() {
        Long id = 1L;
        TransactionEntity entity = givenEntity(id, BigDecimal.valueOf(100.00), "EUR", "PLACED");

        when(repo.findById(id)).thenReturn(Optional.of(entity));

        Transaction actual = service.getTransactionById(id);

        assertThat(actual.id()).isEqualTo(id);
        assertThat(actual.amount()).isEqualTo(BigDecimal.valueOf(100.00));
        assertThat(actual.currency()).isEqualTo(Currency.EUR);
        assertThat(actual.status()).isEqualTo(Status.PLACED);
    }

    @Test
    void getTransactionById_notFound_ThrowsTransactionNotFound() {
        Long id = 999L;

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTransactionById(id))
                .isInstanceOf(TransactionNotFound.class);
    }

    @Test
    void getAllTransactions_emptyRepo_ReturnsEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<Transaction> result = service.getAllTransactions();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllTransactions_nonEmptyRepo_ReturnsMappedDtos() {
        List<TransactionEntity> entities = List.of(
                givenEntity(1L, BigDecimal.valueOf(10.00), "EUR", "PLACED"),
                givenEntity(2L, BigDecimal.valueOf(20.00), "USD", "PROCESSING")
        );

        when(repo.findAll()).thenReturn(entities);

        List<Transaction> result = service.getAllTransactions();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).amount()).isEqualTo(BigDecimal.valueOf(10.00));
        assertThat(result.get(0).currency()).isEqualTo(Currency.EUR);
        assertThat(result.get(0).status()).isEqualTo(Status.PLACED);

        assertThat(result.get(1).amount()).isEqualTo(BigDecimal.valueOf(20.00));
        assertThat(result.get(1).currency()).isEqualTo(Currency.USD);
        assertThat(result.get(1).status()).isEqualTo(Status.PROCESSING);
    }

    @Test
    void createTransaction_newSave_ReturnsDtoWithId() {
        // given
        Transaction dto = givenDto(null, BigDecimal.valueOf(100.00), Currency.EUR, null);
        TransactionEntity entity = givenEntity(1L, BigDecimal.valueOf(100.00), "EUR", "PLACED");

        when(repo.save(any(TransactionEntity.class))).thenReturn(entity);

        // when
        Transaction saved = service.createTransaction(dto);

        // then
        assertThat(saved.id()).isEqualTo(1L);
        assertThat(saved.amount()).isEqualTo(BigDecimal.valueOf(100.00));
        assertThat(saved.currency()).isEqualTo(Currency.EUR);
        assertThat(saved.status()).isEqualTo(Status.PLACED);

        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(repo).save(captor.capture());
        TransactionEntity captured = captor.getValue();
        assertThat(captured.getAmount()).isEqualTo(BigDecimal.valueOf(100.00));
        assertThat(captured.getCurrency()).isEqualTo("EUR");
        assertThat(captured.getStatus()).isEqualTo("PLACED");
    }

    @Test
    void delete_existingId_CallsDelete() {
        Long id = 1L;

        when(repo.existsById(id)).thenReturn(true);
        doNothing().when(repo).deleteById(id);

        service.delete(id);

        verify(repo).existsById(id);
        verify(repo).deleteById(id);
    }

    @Test
    void delete_notFound_ThrowsTransactionNotFound() {
        Long id = 999L;

        when(repo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(TransactionNotFound.class);

        verify(repo).existsById(id);
        verify(repo, never()).deleteById(any());
    }

    @Test
    void update_existingTransaction_ReturnsUpdatedDto() {
        Long id = 1L;
        TransactionEntity entity = givenEntity(id, BigDecimal.valueOf(10.00), "EUR", "PLACED");

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);

        Transaction dto = givenDto(id, BigDecimal.valueOf(150.00), Currency.USD, Status.FINALIZED);
        Transaction updated = service.update(dto);

        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.amount()).isEqualTo(BigDecimal.valueOf(150.00));
        assertThat(updated.currency()).isEqualTo(Currency.USD);
        assertThat(updated.status()).isEqualTo(Status.FINALIZED);

        assertThat(entity.getAmount()).isEqualTo(BigDecimal.valueOf(150.00));
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getStatus()).isEqualTo("FINALIZED");
    }

    @Test
    void update_notFound_ThrowsTransactionNotFound() {
        Long id = 999L;

        when(repo.findById(id)).thenReturn(Optional.empty());

        Transaction dto = givenDto(id, BigDecimal.valueOf(150.00), Currency.USD, Status.FINALIZED);

        assertThatThrownBy(() -> service.update(dto))
                .isInstanceOf(TransactionNotFound.class);
    }

    @Test
    void entityToDto_mapsAllFieldsCorrectly() {
        TransactionEntity entity = givenEntity(
                1L, BigDecimal.valueOf(100.00), "EUR", "PROCESSING"
        );

        entity.setCreatedAt(now.minusSeconds(100));
        entity.setUpdatedAt(now);

        Transaction dto = service.entityToDto(entity);

        assertThat(dto).isEqualTo(
                Transaction.builder()
                        .id(1L)
                        .amount(BigDecimal.valueOf(100.00))
                        .currency(Currency.EUR)
                        .status(Status.PROCESSING)
                        .createdAt(now.minusSeconds(100))
                        .updatedAt(now)
                        .build()
        );
    }
}
