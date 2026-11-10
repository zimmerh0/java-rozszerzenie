package com.zimmerh0.medicalclinic.dto;

public record ChangePasswordCommand(String currentPassword, String newPassword) {
}
