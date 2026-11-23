package com.zimmerh0.medicalclinic.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangePasswordCommand(@NotNull String currentPassword, @NotBlank String newPassword) {
}
