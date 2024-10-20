package com.kingg.api_vacunas_panama.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class RolesPermisosId implements Serializable {
    @Serial
    private static final long serialVersionUID = -5300599546506914322L;
    @NotNull
    @Column(name = "rol", nullable = false)
    private Short idRol;

    @NotNull
    @Column(name = "permiso", nullable = false)
    private Short idPermiso;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RolesPermisosId entity = (RolesPermisosId) o;
        return Objects.equals(this.idRol, entity.idRol) &&
                Objects.equals(this.idPermiso, entity.idPermiso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol, idPermiso);
    }

}