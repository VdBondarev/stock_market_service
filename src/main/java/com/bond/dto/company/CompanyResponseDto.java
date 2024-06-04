package com.bond.dto.company;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponseDto {
    private UUID id;
    private String name;
    private String registrationNumber;
    private String address;
    private LocalDateTime createdAt;
    private Long ownerId;
}
