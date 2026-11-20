package com.zimmerh0.medicalclinic.dto.patient;

public record ChangePasswordCommand(String currentPassword, String newPassword) {
}
