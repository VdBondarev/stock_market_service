package com.bond.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequestDto {
    private String email;
    private String password;
    private String repeatPassword;
    private String firstName;
    private String lastName;
}
