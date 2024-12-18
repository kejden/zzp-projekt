package io.ndk.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSignUp {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min=8, max = 15)
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
}
