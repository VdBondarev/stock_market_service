package com.bond.dto.company;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull
    private LocalDateTime createdAt;
}
