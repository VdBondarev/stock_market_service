package com.bond.dto.company;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCompanyRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String registrationNumber;
    @NotBlank
    private String address;
}
