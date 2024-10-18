package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.persistence.entity.Direccion;
import com.kingg.api_vacunas_panama.persistence.entity.Distrito;
import com.kingg.api_vacunas_panama.persistence.entity.Provincia;
import com.kingg.api_vacunas_panama.persistence.repository.DireccionRepository;
import com.kingg.api_vacunas_panama.persistence.repository.DistritoRepository;
import com.kingg.api_vacunas_panama.persistence.repository.ProvinciaRepository;
import com.kingg.api_vacunas_panama.util.mapper.DireccionMapper;
import com.kingg.api_vacunas_panama.web.dto.DireccionDto;
import com.kingg.api_vacunas_panama.web.dto.DistritoDto;
import com.kingg.api_vacunas_panama.web.dto.ProvinciaDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Service for {@link Direccion}, {@link Distrito} and {@link Provincia}.
 */
@Service
@RequiredArgsConstructor
public class DireccionService {
    private final DireccionMapper mapper;
    private final DireccionRepository direccionRepository;
    private final DistritoRepository distritoRepository;
    private final ProvinciaRepository provinciaRepository;

    List<Distrito> getDistritos() {
        return distritoRepository.findAll();
    }

    @Cacheable(cacheNames = "massive", key = "'distritosDto'")
    public List<DistritoDto> getDistritosDto() {
        return mapper.distritoListToDto(getDistritos());
    }

    List<Provincia> getProvincias() {
        return provinciaRepository.findAll();
    }

    @Cacheable(cacheNames = "massive", key = "'provinciasDto'")
    public List<ProvinciaDto> getProvinciasDto() {
        return mapper.provinciaListToDto(getProvincias());
    }

    Direccion getDireccionDefault() {
        return direccionRepository.findDireccionByDireccionAndDistrito_Id("Por registrar", 0).orElseThrow();
    }

    @Cacheable(cacheNames = "massive", key = "'direccionDefault'")
    public DireccionDto getDireccionDtoDefault() {
        return mapper.direccionToDto(getDireccionDefault());
    }

    Distrito getDistritoDefault() {
        return distritoRepository.findById((short) 0).orElseThrow();
    }

    @Cacheable(cacheNames = "massive", key = "'distritoDefault'")
    public DistritoDto getDistritoDtoDefault() {
        return mapper.distritoToDto(getDistritoDefault());
    }

    Distrito getDistritoById(Short id) {
        return distritoRepository.findById(id).orElseThrow();
    }

    Optional<Direccion> getDireccionByDireccionAndDistritoId(String direccion, Integer distrito) {
        return direccionRepository.findDireccionByDireccionAndDistrito_Id(direccion, distrito);
    }

    public Optional<Direccion> getDireccionByDto(@NotNull @Valid DireccionDto direccionDto) {
        Optional<Direccion> direccion = direccionRepository.findById(direccionDto.id());
        if (direccion.isEmpty() && direccionDto.distrito().id() != null) {
            direccion = direccionRepository.findDireccionByDireccionAndDistrito_Id(direccionDto.direccion(), direccionDto.distrito().id());
        } else if (direccion.isEmpty() && direccionDto.distrito().nombre() != null) {
            direccion = direccionRepository.findDireccionByDireccionAndDistrito_Nombre(direccionDto.direccion(), direccionDto.distrito().nombre());
        } else if (direccion.isEmpty()) {
            direccion = direccionRepository.findDireccionByDireccionContainingIgnoreCase(direccionDto.direccion());
        }
        return direccion;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Direccion createDireccion(@NotNull @Valid DireccionDto direccionDto) {
        Distrito distrito;
        if (direccionDto.distrito() != null) {
            distrito = this.getDistritoById(direccionDto.distrito().id());
        } else {
            distrito = this.getDistritoDefault();
        }

        Direccion direccion = new Direccion();
        direccion.setDireccion(direccionDto.direccion());
        direccion.setDistrito(distrito);
        direccion.setCreatedAt(direccionDto.createdAt() != null ? direccionDto.createdAt() : LocalDateTime.now(ZoneOffset.UTC));

        direccion = direccionRepository.save(direccion);
        return direccion;
    }

}
