package com.interview.dto.request;

import com.interview.model.Mechanic;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MechanicUpdateRequest {
        @Size(max = 50)
        private String firstName;
        @Size(max = 50)
        private String lastName;
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number")
        private String phoneNumber;
        @Email(message = "Invalid email format")
        private String email;
        private Long mechanicShopId;
        private Mechanic.Role role;
}
