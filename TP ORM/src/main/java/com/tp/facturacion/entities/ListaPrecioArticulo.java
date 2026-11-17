package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "lista_precio_articulo")
public class ListaPrecioArticulo extends AuditoriaApp {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_precio_id", nullable = false)
    private ListaPrecio listaPrecio;

    @Column(name = "precio_venta", nullable = false)
    private double precioVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;
}
