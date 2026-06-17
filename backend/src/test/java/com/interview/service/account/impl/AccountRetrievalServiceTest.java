package com.interview.service.account.impl;

import com.interview.dto.account.request.AccountListRequestDTO;
import com.interview.dto.account.response.AccountDetailsResponseDTO;
import com.interview.dto.account.response.AccountListResponseDTO;
import com.interview.exception.AccountNotFoundException;
import com.interview.mapper.AccountMapper;
import com.interview.model.account.AccountEntity;
import com.interview.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountRetrievalService.
 */
@ExtendWith(MockitoExtension.class)
class AccountRetrievalServiceTest {

    private static final String TEST_ACCOUNT_ID = "ACC-000001";

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountRetrievalService accountRetrievalService;

    @Test
    void testGetAccount_Success() {
        // Arrange
        AccountEntity accountEntity = AccountEntity.builder()
                .accountId(TEST_ACCOUNT_ID)
                .accountName("Test Account")
                .email("test@example.com")
                .build();
        accountEntity.setId(1L);

        when(accountRepository.findByAccountId(TEST_ACCOUNT_ID))
                .thenReturn(Optional.of(accountEntity));

        // Act
        AccountDetailsResponseDTO result = accountRetrievalService.getAccount(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getAccountId());
        assertEquals("Test Account", result.getAccountName());
        verify(accountRepository).findByAccountId(TEST_ACCOUNT_ID);
    }

    @Test
    void testGetAccount_NotFound() {
        // Arrange
        when(accountRepository.findByAccountId(TEST_ACCOUNT_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> 
                accountRetrievalService.getAccount(TEST_ACCOUNT_ID));

        verify(accountRepository).findByAccountId(TEST_ACCOUNT_ID);
    }

    @Test
    void testGetAccounts_WithFilters() {
        // Arrange
        AccountListRequestDTO request = AccountListRequestDTO.builder()
                .accountName("Test")
                .pageNumber(1)
                .pageSize(25)
                .build();

        AccountEntity accountEntity = AccountEntity.builder()
                .accountId(TEST_ACCOUNT_ID)
                .accountName("Test Account")
                .email("test@example.com")
                .build();
        accountEntity.setId(1L);

        Page<AccountEntity> accountPage = new PageImpl<>(
                Collections.singletonList(accountEntity),
                PageRequest.of(0, 25),
                1L
        );

        when(accountRepository.findAccountsWithFilters(
                eq("Test"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(accountPage);

        // Act
        AccountListResponseDTO result = accountRetrievalService.getAccounts(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAccounts().size());
        assertEquals(1, result.getPageNumber());
        assertEquals(25, result.getPageSize());
        assertEquals(1L, result.getTotalItems());
        verify(accountRepository).findAccountsWithFilters(anyString(), any(), any(), any(), 
                any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void testGetAccounts_EmptyResult() {
        // Arrange
        AccountListRequestDTO request = AccountListRequestDTO.builder()
                .pageNumber(1)
                .pageSize(25)
                .build();

        Page<AccountEntity> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, 25),
                0L
        );

        when(accountRepository.findAccountsWithFilters(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        AccountListResponseDTO result = accountRetrievalService.getAccounts(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.getAccounts().isEmpty());
        assertEquals(0L, result.getTotalItems());
    }

    @Test
    void testGetAccounts_DefaultPagination() {
        // Arrange
        AccountListRequestDTO request = AccountListRequestDTO.builder()
                .build(); // No pagination specified

        Page<AccountEntity> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, 25),
                0L
        );

        when(accountRepository.findAccountsWithFilters(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        AccountListResponseDTO result = accountRetrievalService.getAccounts(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(25, result.getPageSize());
    }
}

