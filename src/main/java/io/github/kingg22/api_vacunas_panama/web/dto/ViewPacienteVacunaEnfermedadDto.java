package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Paciente;
import io.github.kingg22.api_vacunas_panama.util.NumDosisEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DTO for view_pacientes_vacunas_enfermedades, details about {@link Paciente} add separate of view.
 */
public record ViewPacienteVacunaEnfermedadDto(@JsonIgnore
                                              @JsonProperty(value = "id_paciente") UUID idPaciente,
                                              @JsonProperty(value = "id_dosis") UUID idDosis,
                                              @JsonProperty(value = "id_vacuna") UUID idVacuna,
                                              @NotNull @Size(max = 100) String vacuna,
                                              @NotNull @JsonProperty(value = "numero_dosis") NumDosisEnum numeroDosis,
                                              List<IdNombreDto> enfermedades,
                                              @JsonProperty(value = "edad_min_recomendada") Short edadMinRecomendada,
                                              @NotNull @JsonProperty(value = "fecha_aplicacion") LocalDateTime fechaAplicacion,
                                              @JsonProperty(value = "intervalo_recomendado_dosis") Double intervaloRecomendadoDosis,
                                              @JsonProperty(value = "intervalo_real_dosis") Integer intervaloRealDosis,
                                              @JsonProperty(value = "id_sede") UUID idSede,
                                              @Size(max = 100) String sede,
                                              @Size(max = 13) String dependencia) implements Serializable {

    public ViewPacienteVacunaEnfermedadDto(String vacuna, String numeroDosis,
                                           String enfermedadesPrevenidas, Short edadMinima,
                                           LocalDateTime fechaAplicacion, Double intervaloRecomendado,
                                           Integer intervaloReal, String sede, String dependencia, UUID idPaciente,
                                           UUID idVacuna, UUID idSede, UUID idDosis, String idsEnfermedades) {
        this(idPaciente, idDosis, idVacuna, vacuna, NumDosisEnum.fromValue(numeroDosis.trim().toUpperCase()),
                mapEnfermedades(idsEnfermedades, enfermedadesPrevenidas), edadMinima, fechaAplicacion,
                intervaloRecomendado, intervaloReal, idSede, sede, dependencia);
    }

    private static List<IdNombreDto> mapEnfermedades(String idsEnfermedades, String enfermedadesPrevenidas) {
        List<Integer> ids = Arrays.stream(idsEnfermedades.split("\\s*,\\s*")).map(Integer::parseInt).toList();
        List<String> nombres = Arrays.asList(enfermedadesPrevenidas.split("\\s*,\\s*"));
        if (ids.isEmpty() && nombres.isEmpty()) {
            return Collections.emptyList();
        }
        if (ids.size() != nombres.size()) {
            throw new IllegalArgumentException("The ID and name lists must be the same size");
        }

        List<IdNombreDto> enfermedades = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            enfermedades.add(new IdNombreDto(ids.get(i), nombres.get(i)));
        }
        return enfermedades;
    }
}
