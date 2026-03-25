package com.interview.service;

import com.interview.Application;
import com.interview.exception.DuplicateResourceException;
import com.interview.model.Mechanic;
import com.interview.repository.MechanicRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
public class MechanicServiceTest {

    private static final Long MECHANIC_ID = 1L;
    private static final String EMAIL = "123@gmail.com";
    private static final String EMAIL_ALREADY_EXISTS_IN_DB_MESSAGE = "Email already exists in DB";

    @Mock
    private MechanicRepository mechanicRepository;
    @InjectMocks
    private MechanicService mechanicService;

    @Test
    public void GIVEN_mechanic_WHEN_createMechanic_THEN_throwException() {
        Mechanic mechanic = Mechanic.builder().id(MECHANIC_ID).email(EMAIL).build();
        when(mechanicRepository.existsByEmail(mechanic.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> mechanicService.createMechanic(mechanic));

        assertEquals(EMAIL_ALREADY_EXISTS_IN_DB_MESSAGE, exception.getMessage());
    }

}

