package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "tipo_moneda")
public class TipoMoneda extends AuditoriaApp {
    @Column(name = "codigo_afip", nullable = false)
    private String codigoAfip;

    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private String simbolo;
}
