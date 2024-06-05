package com.bond.dto.user;

import com.bond.annotation.FieldMatch;
import com.bond.annotation.StartsWithCapital;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@FieldMatch(
        firstField = "password",
        secondField = "repeatPassword",
        message = "Passwords do not match"
)
@Accessors(chain = true)
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 35)
    private String password;
    @NotBlank
    @Size(min = 8, max = 35)
    private String repeatPassword;
    @NotBlank
    @StartsWithCapital
    private String firstName;
    @NotBlank
    @StartsWithCapital
    private String lastName;
}
