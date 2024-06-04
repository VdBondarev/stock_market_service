package com.bond.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDto {
    @Email
    @NotBlank
    private String email;
    @Size(min = 8, max = 35)
    private String password;
}
