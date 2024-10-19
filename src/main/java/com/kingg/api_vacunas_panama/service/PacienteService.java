package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.persistence.entity.Direccion;
import com.kingg.api_vacunas_panama.persistence.entity.Paciente;
import com.kingg.api_vacunas_panama.persistence.repository.PacienteRepository;
import com.kingg.api_vacunas_panama.util.FormatterUtil;
import com.kingg.api_vacunas_panama.util.mapper.DireccionMapper;
import com.kingg.api_vacunas_panama.util.mapper.PacienteMapper;
import com.kingg.api_vacunas_panama.web.dto.PacienteDto;
import com.kingg.api_vacunas_panama.web.dto.ViewPacienteVacunaEnfermedadDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for {@link Paciente}.
 * Extends {@link PersonaService} to inherit methods for directly modifying all details related to a {@link Paciente}.
 * This service allows for comprehensive management of patient information and integrates with
 * the underlying personal data structure.
 */
@Service
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteMapper mapper;
    private final DireccionMapper direccionMapper;
    private final PacienteRepository pacienteRepository;
    private final DireccionService direccionService;

    @Cacheable(cacheNames = "cache", key = "'view_vacuna_enfermedad'.concat(#idPaciente)")
    public List<ViewPacienteVacunaEnfermedadDto> getViewVacunaEnfermedad(UUID idPaciente) {
        List<ViewPacienteVacunaEnfermedadDto> view = this.pacienteRepository.findAllFromViewVacunaEnfermedad(idPaciente);
        if (view.isEmpty()) {
            return List.of();
        } else {
            return view;
        }
    }

    private void validateCreatePaciente(PacienteDto pacienteDto) throws IllegalArgumentException {
        if ((pacienteDto.getNombre() == null || pacienteDto.getNombre().isBlank()) && (pacienteDto.getNombre2() == null || pacienteDto.getNombre2().isBlank())) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio");
        }
        if ((pacienteDto.getApellido1() == null || pacienteDto.getApellido2().isBlank()) && (pacienteDto.getApellido2() == null || pacienteDto.getApellido2().isBlank())) {
            throw new IllegalArgumentException("El apellido del paciente es obligatorio");
        }
        if ((pacienteDto.getCedula() == null || pacienteDto.getCedula().isBlank()) &&
            (pacienteDto.getPasaporte() == null || pacienteDto.getPasaporte().isBlank()) &&
            (pacienteDto.getIdentificacionTemporal() == null || pacienteDto.getIdentificacionTemporal().isBlank())) {
            throw new IllegalArgumentException("Una identificación personal del paciente es obligatorio");
        }
        if (pacienteDto.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha del paciente es obligatorio");
        }
    }

    @Transactional
    public PacienteDto createPaciente(@NotNull PacienteDto pacienteDto) throws IllegalArgumentException {
        validateCreatePaciente(pacienteDto);
        if (pacienteDto.getCedula() != null) {
            pacienteDto.setCedula(FormatterUtil.formatCedula(pacienteDto.getCedula()));
        }
        Direccion direccion;
        if (pacienteDto.getDireccion() != null) {
            direccion = direccionService.getDireccionByDto(pacienteDto.getDireccion()).orElseGet(() -> direccionService.createDireccion(pacienteDto.getDireccion()));
        } else {
            direccion = direccionMapper.direccionDtoToEntity(direccionService.getDireccionDtoDefault());
        }

        Paciente paciente = Paciente.builder()
                .nombre(pacienteDto.getNombre())
                .nombre2(pacienteDto.getNombre2())
                .apellido1(pacienteDto.getApellido1())
                .apellido2(pacienteDto.getApellido2())
                .fechaNacimiento(pacienteDto.getFechaNacimiento())
                .cedula(pacienteDto.getCedula())
                .pasaporte(pacienteDto.getPasaporte())
                .identificacionTemporal(pacienteDto.getIdentificacionTemporal())
                .telefono(pacienteDto.getTelefono())
                .correo(pacienteDto.getCorreo())
                .sexo(pacienteDto.getSexo())
                .direccion(direccion)
                .estado(pacienteDto.getEstado())
                .disabled(pacienteDto.getDisabled())
                .createdAt(pacienteDto.getCreatedAt() != null ? pacienteDto.getCreatedAt() : LocalDateTime.now(ZoneOffset.UTC))
                .build();
        paciente =  pacienteRepository.save(paciente);
        return mapper.toDto(paciente);
    }

    Optional<Paciente> getPacienteByUserID(@NotNull UUID idUser) {
        return this.pacienteRepository.findByUsuario_Id(idUser);
    }

    Optional<Paciente> getPacienteById(@NotNull UUID idPaciente) {
        return this.pacienteRepository.findById(idPaciente);
    }

    public PacienteDto getPacienteDtoById(@NotNull UUID idPaciente) {
        return mapper.toDto(this.getPacienteById(idPaciente).orElseThrow());
    }

    public List<ViewPacienteVacunaEnfermedadDto> getVacunaPaciente(UUID idPaciente, UUID idVacuna) {
        List<ViewPacienteVacunaEnfermedadDto> view = this.pacienteRepository.findAllFromViewVacunaEnfermedad(idPaciente, idVacuna);
        if (view.isEmpty()) {
            return List.of();
        } else {
            return view;
        }
    }

}
