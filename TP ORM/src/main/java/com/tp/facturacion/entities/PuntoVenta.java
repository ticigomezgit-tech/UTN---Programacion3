package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "punto_venta")
public class PuntoVenta extends AuditoriaApp {
    @Column(nullable = false)
    private int numero;

    private String descripcion;
    private String tipoEmision;
    private String domicilioComercial;
}
