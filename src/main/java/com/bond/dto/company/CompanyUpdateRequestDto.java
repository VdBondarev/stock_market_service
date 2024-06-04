package com.bond.dto.company;

import lombok.Getter;
import lombok.Setter;

/**
 * You are allowed to update company's name or address only
 */
@Getter
@Setter
public class CompanyUpdateRequestDto {
    private String name;
    private String address;
}
