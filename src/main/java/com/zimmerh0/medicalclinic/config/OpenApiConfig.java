package com.zimmerh0.medicalclinic.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MedicalClinic API",
                version = "1.0",
                description = "API przychodni do zarzadzania uzytkownikami, "
                        + "pacjentami, doktorami, placowkami i wizytami"
        )
)
public class OpenApiConfig {
}
