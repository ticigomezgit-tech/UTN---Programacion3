package com.tp.facturacion.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "factura_venta_detalle")
public class FacturaVentaDetalle extends EntityId {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaVenta factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_precio_articulo_id", nullable = false)
    private ListaPrecioArticulo listaPrecioArticulo;

    private String descripcion;

    @Column(nullable = false)
    private double cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    private double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;

    @Column(name = "importe_subtotal", nullable = false)
    private double importeSubtotal;
}
