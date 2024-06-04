package com.bond.dto.company;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponseDto {
    private String id;
    private String name;
    private String registrationNumber;
    private String address;
    private LocalDateTime createdAt;
}
