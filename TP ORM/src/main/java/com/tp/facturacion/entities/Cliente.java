package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente extends AuditoriaApp {
    @Column(name = "cuit_cuil", nullable = false)
    private String cuitCuil;

    @Column(nullable = false)
    private String denominacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contacto_id", nullable = false)
    private Contacto contacto;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domicilio_id", nullable = false)
    private Domicilio domicilio;
}
