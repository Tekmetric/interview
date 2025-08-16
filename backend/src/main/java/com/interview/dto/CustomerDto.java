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
// TODO EXPLAIN: why dto, no certain fields
// TODO EXPLAIN: Jackson, serialization
public class CustomerDto implements Serializable {
    @JsonProperty("customer_id")
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    private String lastName;
    private String email;
    private Integer version;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    // TODO EXPLAIN: "2025-08-07T17:06:33.349278"
    private LocalDateTime createdAt;
    private List<AddressDto> addresses;
}
