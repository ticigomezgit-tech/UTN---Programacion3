package com.tp.facturacion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

/** Ejecuta las 22 consultas. Ejecutar primero Main para tener datos cargados. */
public class MainConsultas {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();
        ConsultasJPQL q = new ConsultasJPQL(em);

        try {
            Calendar cal = Calendar.getInstance();
            Date hasta = cal.getTime();
            cal.add(Calendar.YEAR, -1);
            Date desde = cal.getTime();

            System.out.println("1) Facturas: " + q.q01TodasLasFacturas().size());
            imprimir("2) Proyección", q.q02ProyeccionFacturas());
            System.out.println("3) Artículos rubro Informática: " + q.q03ArticulosPorRubro("Informática").size());
            System.out.println("4) Facturas último año: " + q.q04FacturasEntreFechas(desde, hasta).size());
            System.out.println("5) Emitidas > 10000 no anuladas: " + q.q05FacturasEmitidasNoAnuladas(10000).size());
            System.out.println("6) Clientes 'gom': " + q.q06ClientesPorTexto("gom").size());
            System.out.println("7) Estados: " + q.q07EstadosDistintos());
            Object[] agg = q.q08AgregacionesFacturas();
            System.out.println("8) Cantidad=" + agg[0] + " Suma=" + agg[1] + " Promedio=" + agg[2]);
            System.out.println("9) Puntos de venta 1,2,5: " + q.q09PuntosVentaPorNumeros(Arrays.asList(1, 2, 5)).size());
            System.out.println("10) Facturas de admin: " + q.q10FacturasPorUsuarioCarga("admin").size());
            System.out.println("11) Detalles punto venta 1: " + q.q11DetallesPorPuntoVenta(1).size());
            imprimir("12) Artículo / marca", q.q12ArticulosConMarca());
            System.out.println("13) Facturas marca Logitech: " + q.q13FacturasPorMarca("Logitech").size());
            System.out.println("14) Facturas sobre el promedio: " + q.q14FacturasSobrePromedio().size());
            System.out.println("15) Facturas del cliente: " + q.q15FacturasPorCliente("20-12345678-9").size());
            imprimir("16) Por punto de venta", q.q16FacturacionPorPuntoVenta());
            imprimir("17) Usuarios con más de 5 facturas", q.q17UsuariosConMasDeNFacturas(5));
            imprimir("18) Ventas por marca", q.q18VentasPorMarca());
            imprimir("19) Total por condición IVA", q.q19TotalPorCondicionIva());
            System.out.println("20) Marcas con artículos facturados: " + q.q20MarcasConArticulosFacturados().size());
            System.out.println("21) Artículos nunca facturados: " + q.q21ArticulosNuncaFacturados().size());
            imprimir("22) Número / Importe / Categoría", q.q22CategoriaFacturas());
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void imprimir(String titulo, List<Object[]> filas) {
        System.out.println(titulo);
        for (Object[] fila : filas) {
            System.out.println("   " + Arrays.toString(fila));
        }
    }
}
