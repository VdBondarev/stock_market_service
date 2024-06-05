package com.bond.dto.company;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateCompanyRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String registrationNumber;
    @NotBlank
    private String address;
}
