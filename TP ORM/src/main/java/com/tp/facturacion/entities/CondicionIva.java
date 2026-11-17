package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "condicion_iva")
public class CondicionIva extends AuditoriaApp {
    @Column(name = "codigo_afip", nullable = false)
    private int codigoAfip;

    @Column(nullable = false)
    private String denominacion;
}
