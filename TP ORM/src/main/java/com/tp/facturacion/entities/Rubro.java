package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "rubro")
public class Rubro extends AuditoriaApp {
    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private Integer codigo;
}
