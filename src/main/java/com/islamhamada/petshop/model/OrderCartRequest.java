package com.islamhamada.petshop.model;

import com.islamhamada.petshop.contracts.validator.PhoneNumberOrEmpty;
import com.islamhamada.petshop.contracts.validator.PostalCodeOrEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCartRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank @PhoneNumberOrEmpty
    private String phoneNumber;
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank @PostalCodeOrEmpty
    private String postalCode;
    @NotBlank
    private String houseNumber;
    @NotBlank
    private String street;
}
