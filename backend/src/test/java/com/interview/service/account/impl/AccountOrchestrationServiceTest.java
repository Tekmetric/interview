package com.interview.service.account.impl;

import com.interview.dto.account.request.AccountCreateRequestDTO;
import com.interview.dto.account.request.AccountListRequestDTO;
import com.interview.dto.account.request.AccountUpdateRequestDTO;
import com.interview.dto.account.response.*;
import com.interview.service.account.IAccountDeleteService;
import com.interview.service.account.IAccountRetrievalService;
import com.interview.service.account.IAccountUpdateService;
import com.interview.service.account.ICreateAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountOrchestrationService.
 */
@ExtendWith(MockitoExtension.class)
class AccountOrchestrationServiceTest {

    private static final String TEST_ACCOUNT_ID = "ACC-000001";

    @Mock
    private ICreateAccountService createAccountService;

    @Mock
    private IAccountRetrievalService accountRetrievalService;

    @Mock
    private IAccountUpdateService accountUpdateService;

    @Mock
    private IAccountDeleteService accountDeleteService;

    @InjectMocks
    private AccountOrchestrationService accountOrchestrationService;

    @Test
    void testCreateAccount() {
        // Arrange
        AccountCreateRequestDTO request = AccountCreateRequestDTO.builder()
                .accountName("Test Account")
                .email("test@example.com")
                .build();

        AccountCreateResponseDTO response = AccountCreateResponseDTO.builder()
                .accountReferenceId(TEST_ACCOUNT_ID)
                .accountName("Test Account")
                .status("PENDING")
                .build();

        when(createAccountService.createAccount(any(AccountCreateRequestDTO.class))).thenReturn(response);

        // Act
        AccountCreateResponseDTO result = accountOrchestrationService.createAccount(request);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getAccountReferenceId());
        verify(createAccountService).createAccount(request);
    }

    @Test
    void testUpdateAccount() {
        // Arrange
        AccountUpdateRequestDTO request = AccountUpdateRequestDTO.builder()
                .accountName("Updated Account")
                .build();

        AccountUpdateResponseDTO response = AccountUpdateResponseDTO.builder()
                .accountReferenceId(TEST_ACCOUNT_ID)
                .accountName("Updated Account")
                .status("ACTIVE")
                .build();

        when(accountUpdateService.updateAccount(eq(TEST_ACCOUNT_ID), any(AccountUpdateRequestDTO.class)))
                .thenReturn(response);

        // Act
        AccountUpdateResponseDTO result = accountOrchestrationService.updateAccount(TEST_ACCOUNT_ID, request);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getAccountReferenceId());
        verify(accountUpdateService).updateAccount(TEST_ACCOUNT_ID, request);
    }

    @Test
    void testGetAccount() {
        // Arrange
        AccountDetailsResponseDTO response = AccountDetailsResponseDTO.builder()
                .accountReferenceId(TEST_ACCOUNT_ID)
                .accountName("Test Account")
                .build();

        when(accountRetrievalService.getAccount(TEST_ACCOUNT_ID)).thenReturn(response);

        // Act
        AccountDetailsResponseDTO result = accountOrchestrationService.getAccount(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getAccountReferenceId());
        verify(accountRetrievalService).getAccount(TEST_ACCOUNT_ID);
    }

    @Test
    void testGetAccounts() {
        // Arrange
        AccountListRequestDTO request = AccountListRequestDTO.builder()
                .pageNumber(1)
                .pageSize(25)
                .build();

        AccountListResponseDTO response = AccountListResponseDTO.builder()
                .accounts(java.util.Collections.emptyList())
                .pageNumber(1)
                .pageSize(25)
                .totalItems(0L)
                .totalPages(0)
                .build();

        when(accountRetrievalService.getAccounts(any(AccountListRequestDTO.class))).thenReturn(response);

        // Act
        AccountListResponseDTO result = accountOrchestrationService.getAccounts(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        verify(accountRetrievalService).getAccounts(request);
    }

    @Test
    void testPatchAccount() {
        // Arrange
        AccountUpdateRequestDTO request = AccountUpdateRequestDTO.builder()
                .accountName("Patched Account")
                .build();

        AccountUpdateResponseDTO response = AccountUpdateResponseDTO.builder()
                .accountReferenceId(TEST_ACCOUNT_ID)
                .accountName("Patched Account")
                .status("ACTIVE")
                .build();

        when(accountUpdateService.patchAccount(eq(TEST_ACCOUNT_ID), any(AccountUpdateRequestDTO.class)))
                .thenReturn(response);

        // Act
        AccountUpdateResponseDTO result = accountOrchestrationService.patchAccount(TEST_ACCOUNT_ID, request);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getAccountReferenceId());
        verify(accountUpdateService).patchAccount(TEST_ACCOUNT_ID, request);
    }

    @Test
    void testDeleteAccount() {
        // Arrange
        AccountDeleteResponseDTO response = AccountDeleteResponseDTO.builder()
                .accountReferenceId(TEST_ACCOUNT_ID)
                .message("Account deleted successfully")
                .deleted(true)
                .build();

        when(accountDeleteService.deleteAccount(TEST_ACCOUNT_ID)).thenReturn(response);

        // Act
        AccountDeleteResponseDTO result = accountOrchestrationService.deleteAccount(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getAccountReferenceId());
        assertTrue(result.isDeleted());
        verify(accountDeleteService).deleteAccount(TEST_ACCOUNT_ID);
    }
}




