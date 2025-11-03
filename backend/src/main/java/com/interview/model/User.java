package com.interview.model;

import com.interview.model.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private List<ShoppingList> shoppingLists;

    public User(UserRequest userRequest) {
        this.firstName = userRequest.getFirstName();
        this.lastName = userRequest.getLastName();
        shoppingLists = List.of();
    }

    public boolean isValid() {
        return firstName != null && !firstName.isBlank()
                && lastName != null && !lastName.isBlank();
    }
}
