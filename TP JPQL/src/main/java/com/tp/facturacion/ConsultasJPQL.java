package com.tp.facturacion;

import com.tp.facturacion.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * TP Grupal JPQL - 22 consultas.
 * Todas usan nombres de entidades/atributos Java y parámetros nombrados.
 */
public class ConsultasJPQL {

    private final EntityManager em;

    public ConsultasJPQL(EntityManager em) {
        this.em = em;
    }

    // ===================== NIVEL 1 =====================

    /** 1. Todas las facturas de venta. */
    public List<FacturaVenta> q01TodasLasFacturas() {
        String jpql = "SELECT f FROM FacturaVenta f";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    /** 2. Proyección: número, fecha de emisión e importe total. Object[] = {numero, fechaEmision, importeTotal}. */
    public List<Object[]> q02ProyeccionFacturas() {
        String jpql = "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 3. Artículos de un rubro por denominación. */
    public List<Articulo> q03ArticulosPorRubro(String denominacionRubro) {
        String jpql = "SELECT a FROM Articulo a JOIN a.rubro r WHERE r.denominacion = :rubro";
        return em.createQuery(jpql, Articulo.class)
                .setParameter("rubro", denominacionRubro)
                .getResultList();
    }

    /** 4. Facturas emitidas entre dos fechas. */
    public List<FacturaVenta> q04FacturasEntreFechas(Date desde, Date hasta) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.fechaEmision BETWEEN :desde AND :hasta";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("desde", desde, TemporalType.TIMESTAMP)
                .setParameter("hasta", hasta, TemporalType.TIMESTAMP)
                .getResultList();
    }

    // ===================== NIVEL 2 =====================

    /** 5. Estado EMITIDA, importe > mínimo y no anuladas. */
    public List<FacturaVenta> q05FacturasEmitidasNoAnuladas(double importeMinimo) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.estado = :estado AND f.importeTotal > :minimo AND f.fechaAnulacion IS NULL";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("estado", "EMITIDA")
                .setParameter("minimo", importeMinimo)
                .getResultList();
    }

    /** 6. Clientes por texto parcial en denominación (sin distinguir mayúsculas) o CUIT/CUIL que empiece con "20-". */
    public List<Cliente> q06ClientesPorTexto(String texto) {
        String jpql = "SELECT c FROM Cliente c "
                + "WHERE LOWER(c.denominacion) LIKE LOWER(CONCAT('%', :texto, '%')) "
                + "OR c.cuitCuil LIKE CONCAT(:prefijo, '%')";
        return em.createQuery(jpql, Cliente.class)
                .setParameter("texto", texto)
                .setParameter("prefijo", "20-")
                .getResultList();
    }

    /** 7. Estados distintos ordenados ascendente. */
    public List<String> q07EstadosDistintos() {
        String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
        return em.createQuery(jpql, String.class).getResultList();
    }

    /** 8. COUNT, SUM y AVG en un solo arreglo: {cantidad (Long), suma (Double), promedio (Double)}. */
    public Object[] q08AgregacionesFacturas() {
        String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getSingleResult();
    }

    /** 9. Puntos de venta cuyo número esté en una lista. */
    public List<PuntoVenta> q09PuntosVentaPorNumeros(List<Integer> numeros) {
        String jpql = "SELECT p FROM PuntoVenta p WHERE p.numero IN :numeros";
        return em.createQuery(jpql, PuntoVenta.class)
                .setParameter("numeros", numeros)
                .getResultList();
    }

    // ===================== NIVEL 3 =====================

    /** 10. Facturas cargadas por un usuario (path expression usuarioCarga.usuario). */
    public List<FacturaVenta> q10FacturasPorUsuarioCarga(String nombreUsuario) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :usuario";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("usuario", nombreUsuario)
                .getResultList();
    }

    /** 11. Detalles de facturas de un punto de venta (INNER JOIN explícito). */
    public List<FacturaVentaDetalle> q11DetallesPorPuntoVenta(int numeroPuntoVenta) {
        String jpql = "SELECT d FROM FacturaVentaDetalle d "
                + "INNER JOIN d.factura f "
                + "INNER JOIN f.puntoVenta pv "
                + "WHERE pv.numero = :numeroPv";
        return em.createQuery(jpql, FacturaVentaDetalle.class)
                .setParameter("numeroPv", numeroPuntoVenta)
                .getResultList();
    }

    /** 12. Artículo y marca, incluyendo artículos sin marca (LEFT JOIN). Object[] = {denArticulo, denMarca|null}. */
    public List<Object[]> q12ArticulosConMarca() {
        String jpql = "SELECT a.denominacion, m.denominacion FROM Articulo a LEFT JOIN a.marca m";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 13. Facturas con al menos un detalle de un artículo de una marca (JOINs multinivel). */
    public List<FacturaVenta> q13FacturasPorMarca(String denominacionMarca) {
        String jpql = "SELECT DISTINCT f FROM FacturaVenta f "
                + "JOIN f.detalles d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "WHERE m.denominacion = :marca";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("marca", denominacionMarca)
                .getResultList();
    }

    /** 14. Facturas con importeTotal mayor al promedio (subconsulta en WHERE). */
    public List<FacturaVenta> q14FacturasSobrePromedio() {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2)";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    /** 15. Facturas de un cliente por CUIT/CUIL. */
    public List<FacturaVenta> q15FacturasPorCliente(String cuitCuil) {
        String jpql = "SELECT f FROM FacturaVenta f JOIN f.cliente c WHERE c.cuitCuil = :cuit";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("cuit", cuitCuil)
                .getResultList();
    }

    // ===================== NIVEL 4 =====================

    /** 16. Por punto de venta: {descripcion, cantidad (Long), total (Double)}. */
    public List<Object[]> q16FacturacionPorPuntoVenta() {
        String jpql = "SELECT pv.descripcion, COUNT(f), SUM(f.importeTotal) "
                + "FROM FacturaVenta f JOIN f.puntoVenta pv "
                + "GROUP BY pv.descripcion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 17. Usuarios de carga con más de N facturas: {usuario, cantidad (Long)}. */
    public List<Object[]> q17UsuariosConMasDeNFacturas(long minimo) {
        String jpql = "SELECT u.usuario, COUNT(f) "
                + "FROM FacturaVenta f JOIN f.usuarioCarga u "
                + "GROUP BY u.usuario "
                + "HAVING COUNT(f) > :minimo";
        return em.createQuery(jpql, Object[].class)
                .setParameter("minimo", minimo)
                .getResultList();
    }

    /** 18. Por marca: {denominacion, unidades vendidas, subtotal acumulado}. */
    public List<Object[]> q18VentasPorMarca() {
        String jpql = "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) "
                + "FROM FacturaVentaDetalle d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "GROUP BY m.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 19. Por condición de IVA: {denominacion, total facturado}. */
    public List<Object[]> q19TotalPorCondicionIva() {
        String jpql = "SELECT ci.denominacion, SUM(f.importeTotal) "
                + "FROM FacturaVenta f JOIN f.condicionIva ci "
                + "GROUP BY ci.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // ===================== NIVEL 5 =====================

    /** 20. Marcas con al menos un artículo facturado (EXISTS correlacionado). */
    public List<Marca> q20MarcasConArticulosFacturados() {
        String jpql = "SELECT m FROM Marca m WHERE EXISTS ("
                + "SELECT 1 FROM Articulo a WHERE a.marca = m AND EXISTS ("
                + "SELECT 1 FROM FacturaVentaDetalle d JOIN d.listaPrecioArticulo lpa WHERE lpa.articulo = a))";
        return em.createQuery(jpql, Marca.class).getResultList();
    }

    /** 21. Artículos que nunca fueron facturados (NOT EXISTS correlacionado). */
    public List<Articulo> q21ArticulosNuncaFacturados() {
        String jpql = "SELECT a FROM Articulo a WHERE NOT EXISTS ("
                + "SELECT 1 FROM FacturaVentaDetalle d JOIN d.listaPrecioArticulo lpa WHERE lpa.articulo = a)";
        return em.createQuery(jpql, Articulo.class).getResultList();
    }

    /** 22. CASE WHEN: {numero, importeTotal, categoria}, de mayor a menor importe. */
    public List<Object[]> q22CategoriaFacturas() {
        String jpql = "SELECT f.numero, f.importeTotal, "
                + "CASE WHEN f.importeTotal > 50000 THEN 'ALTO VALOR' "
                + "WHEN f.importeTotal >= 10000 THEN 'MEDIO VALOR' "
                + "ELSE 'BAJO VALOR' END AS categoria "
                + "FROM FacturaVenta f ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }
}
