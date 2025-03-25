package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Doctor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Doctor}
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorDto extends PersonaDto implements Serializable {
    @NotNull
    @Size(max = 20)
    String idoneidad;
    @Size(max = 100)
    String categoria;
    @PastOrPresent
    @JsonProperty(value = "created_at")
    LocalDateTime createdAt;
    @PastOrPresent
    @JsonProperty(value = "updated_at")
    LocalDateTime updatedAt;
}
