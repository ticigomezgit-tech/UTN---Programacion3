package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "lista_precio")
public class ListaPrecio extends AuditoriaApp {
    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String denominacion;
}
