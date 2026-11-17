package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "domicilio")
public class Domicilio extends EntityId {
    private String nombreCalle;
    private String numeroCalle;
}
