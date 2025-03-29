package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto;
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto;
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto;
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion;
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.DireccionKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito;
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.DistritoKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia;
import io.github.kingg22.api.vacunas.panama.modules.direccion.extensions.DistritoProvinciaExtKt;
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DireccionRepository;
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DistritoRepository;
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.ProvinciaRepository;
import io.github.kingg22.api.vacunas.panama.modules.direccion.service.IDireccionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Service for {@link Direccion}, {@link Distrito} and {@link Provincia}. */
@Service
@RequiredArgsConstructor
public class DireccionService implements IDireccionService {
    private final DireccionRepository direccionRepository;
    private final DistritoRepository distritoRepository;
    private final ProvinciaRepository provinciaRepository;

    List<Distrito> getDistritos() {
        return distritoRepository.findAll();
    }

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "massive", key = "'distritosDto'")
    public List<DistritoDto> getDistritosDto() {
        return DistritoProvinciaExtKt.toListDistritoDto(getDistritos());
    }

    List<Provincia> getProvincias() {
        return provinciaRepository.findAll();
    }

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "massive", key = "'provinciasDto'")
    public List<ProvinciaDto> getProvinciasDto() {
        return DistritoProvinciaExtKt.toListProvinciaDto(getProvincias());
    }

    Direccion getDireccionDefault() {
        var result = direccionRepository
                .findDireccionByDireccionAndDistrito_Id("Por registrar", 0)
                .orElseThrow();
        if (result.isEmpty()) throw new NoSuchElementException("La dirección por defecto no fue encontrada");
        return result.getFirst();
    }

    @Cacheable(cacheNames = "massive", key = "'direccionDefault'")
    public DireccionDto getDireccionDtoDefault() {
        return DireccionKonverterKt.toDireccionDto(getDireccionDefault());
    }

    Distrito getDistritoDefault() {
        return distritoRepository.findById((short) 0).orElseThrow();
    }

    @Cacheable(cacheNames = "massive", key = "'distritoDefault'")
    public DistritoDto getDistritoDtoDefault() {
        return DistritoKonverterKt.toDistritoDto(getDistritoDefault());
    }

    Distrito getDistritoById(Short id) {
        return distritoRepository.findById(id).orElseThrow();
    }

    public Optional<Direccion> getDireccionByDto(
            @org.jetbrains.annotations.NotNull @NotNull @Valid DireccionDto direccionDto) {
        Optional<Direccion> direccion = Optional.empty();
        if (direccionDto.id() != null) {
            direccion = direccionRepository.findById(direccionDto.id());
        }
        if (direccion.isEmpty()
                && direccionDto.distrito() != null
                && direccionDto.distrito().id() != null) {
            direccion = this.findUniqueDireccion(direccionRepository.findDireccionByDireccionAndDistrito_Id(
                    direccionDto.direccion(), direccionDto.distrito().id()));
        }
        if (direccion.isEmpty()
                && direccionDto.distrito() != null
                && direccionDto.distrito().nombre() != null) {
            direccion = this.findUniqueDireccion(direccionRepository.findDireccionByDireccionAndDistrito_Nombre(
                    direccionDto.direccion(), direccionDto.distrito().nombre()));
        }
        if (direccion.isEmpty()) {
            direccion = this.findUniqueDireccion(direccionRepository.findDireccionByDireccionStartingWith(
                    direccionDto.direccion().toLowerCase()));
        }
        return direccion;
    }

    @org.jetbrains.annotations.NotNull
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Direccion createDireccion(@org.jetbrains.annotations.NotNull @NotNull @Valid DireccionDto direccionDto) {
        Distrito distrito;
        if (direccionDto.distrito() != null && direccionDto.distrito().id() != null) {
            distrito = this.getDistritoById(direccionDto.distrito().id());
        } else {
            distrito = this.getDistritoDefault();
        }

        Direccion direccion = new Direccion();
        direccion.setDireccion(direccionDto.direccion());
        direccion.setDistrito(distrito);
        if (direccionDto.createdAt() != null) {
            direccion.setCreatedAt(direccionDto.createdAt());
        }
        return direccionRepository.save(direccion);
    }

    private Optional<Direccion> findUniqueDireccion(
            @org.jetbrains.annotations.NotNull Optional<List<Direccion>> result) {
        return result.filter(resp -> resp.size() == 1).map(List::getFirst);
    }
}
