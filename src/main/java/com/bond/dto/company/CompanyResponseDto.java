package com.bond.dto.company;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponseDto {
    private String id;
    private String name;
    private String registrationNumber;
    private String address;
    private LocalDate createdAt;
    private Long ownerId;
}
