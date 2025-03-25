package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Sede;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for {@link Sede}
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SedeDto extends EntidadDto {
    String region;
    @Nullable
    @PastOrPresent
    @JsonProperty(value = "created_at")
    LocalDateTime createdAt;
    @PastOrPresent
    @JsonProperty(value = "updated_at")
    LocalDateTime updatedAt;
}
