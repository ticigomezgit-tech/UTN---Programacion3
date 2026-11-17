package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditoriaApp extends EntityId {

    @Column(name = "fecha_alta", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaAlta;

    @Column(name = "fecha_baja")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaBaja;

    @Column(name = "fecha_modificacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_carga_id", nullable = false)
    protected Usuario usuarioCarga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_baja_id")
    protected Usuario usuarioBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_modificacion_id", nullable = false)
    protected Usuario usuarioModificacion;
}
