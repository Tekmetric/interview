package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerResponse {
    @JsonProperty("customer_id")
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    private String lastName;
    private String email;
    private Integer version;
    private LocalDateTime createdAt;
    private List<AddressResponse> addresses;
}
