package com.interview.service;

import com.interview.model.Account;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testAccount = new Account();
        testAccount.setId(testId);
        testAccount.setFirstName("John");
        testAccount.setLastName("Doe");
        testAccount.setEmail("john.doe@example.com");
        testAccount.setPassword("password123");
        testAccount.setPhone("555-1234");
    }

    @Test
    void getAllAccounts_ShouldReturnAllNonDeletedAccounts() {
        // Arrange
        List<Account> expectedAccounts = Arrays.asList(testAccount, new Account());
        when(accountRepository.findByDeletedAtIsNull()).thenReturn(expectedAccounts);

        // Act
        List<Account> actualAccounts = accountService.getAllAccounts();

        // Assert
        assertNotNull(actualAccounts);
        assertEquals(2, actualAccounts.size());
        verify(accountRepository, times(1)).findByDeletedAtIsNull();
    }

    @Test
    void getAllAccountsWithPageable_ShouldReturnPagedNonDeletedAccounts() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        Page<Account> expectedPage = new PageImpl<>(accounts);
        Pageable pageable = PageRequest.of(0, 10);
        when(accountRepository.findByDeletedAtIsNull(pageable)).thenReturn(expectedPage);

        // Act
        Page<Account> actualPage = accountService.getAllAccounts(pageable);

        // Assert
        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        verify(accountRepository, times(1)).findByDeletedAtIsNull(pageable);
    }

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccount() {
        // Arrange
        when(accountRepository.findByIdAndDeletedAtIsNull(testId)).thenReturn(Optional.of(testAccount));

        // Act
        Optional<Account> result = accountService.getAccountById(testId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAccount.getEmail(), result.get().getEmail());
        verify(accountRepository, times(1)).findByIdAndDeletedAtIsNull(testId);
    }

    @Test
    void getAccountById_WhenAccountDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(accountRepository.findByIdAndDeletedAtIsNull(testId)).thenReturn(Optional.empty());

        // Act
        Optional<Account> result = accountService.getAccountById(testId);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findByIdAndDeletedAtIsNull(testId);
    }

    @Test
    void getAccountByEmail_WhenAccountExists_ShouldReturnAccount() {
        // Arrange
        String email = "john.doe@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));

        // Act
        Optional<Account> result = accountService.getAccountByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(accountRepository, times(1)).findByEmail(email);
    }

    @Test
    void getAccountByEmail_WhenAccountDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<Account> result = accountService.getAccountByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findByEmail(email);
    }

    @Test
    void createAccount_WhenEmailIsUnique_ShouldEncodePasswordAndSaveAccount() {
        // Arrange
        when(accountRepository.findByEmailAndDeletedAtIsNull(testAccount.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testAccount.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account savedAccount = accountService.createAccount(testAccount);

        // Assert
        assertNotNull(savedAccount);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void createAccount_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        when(accountRepository.findByEmailAndDeletedAtIsNull(testAccount.getEmail())).thenReturn(Optional.of(testAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.createAccount(testAccount);
        });
        assertTrue(exception.getMessage().contains("already exists"));
        verify(accountRepository, times(1)).findByEmailAndDeletedAtIsNull(testAccount.getEmail());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_WhenAccountExists_ShouldUpdateAndReturnAccount() {
        // Arrange
        Account updatedDetails = new Account();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Smith");
        updatedDetails.setEmail("john.doe@example.com"); // Same email
        updatedDetails.setPassword("newPassword");
        updatedDetails.setPhone("555-5678");

        when(accountRepository.findById(testId)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account result = accountService.updateAccount(testId, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(accountRepository, times(1)).findById(testId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateAccount_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        Account updatedDetails = new Account();
        when(accountRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.updateAccount(testId, updatedDetails);
        });
        verify(accountRepository, times(1)).findById(testId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_WhenEmailChangedToExistingEmail_ShouldThrowException() {
        // Arrange
        UUID existingAccountId = UUID.randomUUID();
        Account existingAccount = new Account();
        existingAccount.setId(existingAccountId);
        existingAccount.setEmail("existing@example.com");

        Account updatedDetails = new Account();
        updatedDetails.setEmail("existing@example.com"); // Email taken by another account

        when(accountRepository.findById(testId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByEmailAndDeletedAtIsNull("existing@example.com")).thenReturn(Optional.of(existingAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.updateAccount(testId, updatedDetails);
        });
        assertTrue(exception.getMessage().contains("already taken"));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_WhenPasswordIsNull_ShouldNotEncodePassword() {
        // Arrange
        Account updatedDetails = new Account();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Smith");
        updatedDetails.setEmail("john.doe@example.com");
        updatedDetails.setPassword(null); // No password update
        updatedDetails.setPhone("555-5678");

        when(accountRepository.findById(testId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account result = accountService.updateAccount(testId, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateAccount_WhenPasswordIsEmpty_ShouldNotEncodePassword() {
        // Arrange
        Account updatedDetails = new Account();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Smith");
        updatedDetails.setEmail("john.doe@example.com");
        updatedDetails.setPassword(""); // Empty password
        updatedDetails.setPhone("555-5678");

        when(accountRepository.findById(testId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account result = accountService.updateAccount(testId, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void deleteAccount_WhenAccountExists_ShouldSoftDeleteAccount() {
        // Arrange
        when(accountRepository.findById(testId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.deleteAccount(testId);

        // Assert
        verify(accountRepository, times(1)).findById(testId);
        verify(accountRepository, times(1)).save(testAccount);
        assertNotNull(testAccount.getDeletedAt());
    }

    @Test
    void deleteAccount_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.deleteAccount(testId);
        });
        verify(accountRepository, times(1)).findById(testId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void deleteAccount_WhenAccountAlreadyDeleted_ShouldThrowException() {
        // Arrange
        testAccount.setDeletedAt(java.time.LocalDateTime.now());
        when(accountRepository.findById(testId)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.deleteAccount(testId);
        });
        assertTrue(exception.getMessage().contains("already deleted"));
        verify(accountRepository, times(1)).findById(testId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void searchAccountsByName_ShouldReturnMatchingNonDeletedAccounts() {
        // Arrange
        String searchTerm = "John";
        List<Account> expectedAccounts = Arrays.asList(testAccount);
        when(accountRepository.findByNameContainingAndNotDeleted(searchTerm, searchTerm))
                .thenReturn(expectedAccounts);

        // Act
        List<Account> result = accountService.searchAccountsByName(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAccount.getFirstName(), result.get(0).getFirstName());
        verify(accountRepository, times(1))
                .findByNameContainingAndNotDeleted(searchTerm, searchTerm);
    }

    @Test
    void searchAccountsByName_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        String searchTerm = "Nonexistent";
        when(accountRepository.findByNameContainingAndNotDeleted(searchTerm, searchTerm))
                .thenReturn(Arrays.asList());

        // Act
        List<Account> result = accountService.searchAccountsByName(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(accountRepository, times(1))
                .findByNameContainingAndNotDeleted(searchTerm, searchTerm);
    }
}
