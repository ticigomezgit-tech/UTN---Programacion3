package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "contacto")
public class Contacto extends EntityId {
    private String email;
    private String telefono;
    private String celular;
}
