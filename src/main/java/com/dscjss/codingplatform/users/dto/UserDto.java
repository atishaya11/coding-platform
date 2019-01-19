package com.dscjss.codingplatform.users.dto;



import com.dscjss.codingplatform.validation.annotation.PasswordMatches;
import com.dscjss.codingplatform.validation.annotation.ValidEmail;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@PasswordMatches
public class UserDto {

        @NotNull(message = "{password.error.invalid}")
        @Size(min = 6, max = 18, message = "{password.error.size}")
        private String password;

        @NotNull
        @NotEmpty(message = "{field.error.empty}")
        private String matchingPassword;

        @NotNull
        @NotEmpty(message = "{field.error.empty}")
        @ValidEmail
        private String email;

        @NotNull
        @NotEmpty(message = "{field.error.empty}")
        private String username;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
