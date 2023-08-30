package com.oemspring.bookz.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Set;

@Getter
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;


    private String businessadress;


    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public void setBusinessadress(String businessadress) {
        this.businessadress = businessadress;
    }


}
