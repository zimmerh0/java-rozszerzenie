package com.zimmerh0.medicalclinic.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank @Email String email, @NotBlank String password) {
}
