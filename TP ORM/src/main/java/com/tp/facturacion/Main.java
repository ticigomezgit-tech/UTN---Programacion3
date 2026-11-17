package com.tp.facturacion;

import com.tp.facturacion.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // ---------- Usuario (no es AuditoriaApp, se persiste primero) ----------
            Usuario admin = new Usuario();
            admin.setUsuario("admin");
            admin.setClave("1234");
            admin.setNombre("Juan");
            admin.setApellido("Pérez");
            em.persist(admin);

            // ---------- Entidades de soporte (cada una con su propio persist) ----------
            PuntoVenta pv = new PuntoVenta();
            pv.setNumero(1);
            pv.setDescripcion("Casa central");
            pv.setTipoEmision("CAE");
            pv.setDomicilioComercial("San Martín 123, Mendoza");
            auditar(pv, admin);
            em.persist(pv);

            TipoMoneda moneda = new TipoMoneda();
            moneda.setCodigoAfip("PES");
            moneda.setDenominacion("Pesos Argentinos");
            moneda.setSimbolo("$");
            auditar(moneda, admin);
            em.persist(moneda);

            CondicionIva condIva = new CondicionIva();
            condIva.setCodigoAfip(5);
            condIva.setDenominacion("Consumidor Final");
            auditar(condIva, admin);
            em.persist(condIva);

            Contacto contacto = new Contacto();
            contacto.setEmail("cliente@mail.com");
            contacto.setTelefono("2614000000");
            contacto.setCelular("2615000000");
            em.persist(contacto);

            Domicilio domicilio = new Domicilio();
            domicilio.setNombreCalle("Av. Las Heras");
            domicilio.setNumeroCalle("456");
            em.persist(domicilio);

            Cliente cliente = new Cliente();
            cliente.setCuitCuil("20-12345678-9");
            cliente.setDenominacion("María Gómez");
            cliente.setContacto(contacto);
            cliente.setDomicilio(domicilio);
            auditar(cliente, admin);
            em.persist(cliente);

            Rubro rubro = new Rubro();
            rubro.setDenominacion("Informática");
            rubro.setCodigo(10);
            auditar(rubro, admin);
            em.persist(rubro);

            Marca marca = new Marca();
            marca.setDenominacion("Logitech");
            marca.setCodigo(100);
            auditar(marca, admin);
            em.persist(marca);

            Articulo mouse = new Articulo();
            mouse.setCodigo("ART-001");
            mouse.setDenominacion("Mouse inalámbrico");
            mouse.setRubro(rubro);
            mouse.setMarca(marca);
            auditar(mouse, admin);
            em.persist(mouse);

            Articulo teclado = new Articulo();
            teclado.setCodigo("ART-002");
            teclado.setDenominacion("Teclado mecánico");
            teclado.setRubro(rubro);
            teclado.setMarca(marca);
            auditar(teclado, admin);
            em.persist(teclado);

            ListaPrecio lista = new ListaPrecio();
            lista.setCodigo("LP-01");
            lista.setDenominacion("Lista general");
            auditar(lista, admin);
            em.persist(lista);

            ListaPrecioArticulo lpaMouse = new ListaPrecioArticulo();
            lpaMouse.setListaPrecio(lista);
            lpaMouse.setArticulo(mouse);
            lpaMouse.setPrecioVenta(15000.0);
            auditar(lpaMouse, admin);
            em.persist(lpaMouse);

            ListaPrecioArticulo lpaTeclado = new ListaPrecioArticulo();
            lpaTeclado.setListaPrecio(lista);
            lpaTeclado.setArticulo(teclado);
            lpaTeclado.setPrecioVenta(45000.0);
            auditar(lpaTeclado, admin);
            em.persist(lpaTeclado);

            // ---------- Cabecera de la factura ----------
            FacturaVenta factura = new FacturaVenta();
            factura.setNumero(1L);
            factura.setFechaEmision(new Date());
            factura.setCliente(cliente);
            factura.setCondicionIva(condIva);
            factura.setTipoMoneda(moneda);
            factura.setPuntoVenta(pv);
            factura.setEstado("EMITIDA");
            factura.setObservaciones("Factura de prueba con persistencia en cascada");
            auditar(factura, admin);

            // ---------- Detalles (asociación bidireccional con agregarDetalle) ----------
            factura.agregarDetalle(crearDetalle(lpaMouse, 2, 0));
            factura.agregarDetalle(crearDetalle(lpaTeclado, 1, 10));

            double total = factura.getDetalles().stream()
                    .mapToDouble(FacturaVentaDetalle::getImporteSubtotal).sum();
            factura.setImporteTotal(total);
            factura.setImporteCobrado(0);
            factura.setImporteSaldo(total);

            // ---------- REQUISITO CLAVE: un único persist sobre la cabecera ----------
            em.persist(factura);

            em.getTransaction().commit();
            System.out.println("Factura id=" + factura.getId() + " guardada con "
                    + factura.getDetalles().size() + " detalles (ids: "
                    + factura.getDetalles().get(0).getId() + ", "
                    + factura.getDetalles().get(1).getId() + ")");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    private static FacturaVentaDetalle crearDetalle(ListaPrecioArticulo lpa, double cantidad, double bonif) {
        FacturaVentaDetalle d = new FacturaVentaDetalle();
        d.setListaPrecioArticulo(lpa);
        d.setDescripcion(lpa.getArticulo().getDenominacion());
        d.setCantidad(cantidad);
        d.setPrecioUnitario(lpa.getPrecioVenta());
        d.setPorcentajeBonificacion(bonif);
        double neto = cantidad * lpa.getPrecioVenta() * (1 - bonif / 100.0);
        double iva = neto * 0.21;
        d.setImporteNeto(neto);
        d.setImporteIva(iva);
        d.setImporteSubtotal(neto + iva);
        return d;
    }

    /** Completa los campos obligatorios de auditoría. */
    private static void auditar(AuditoriaApp e, Usuario u) {
        Date ahora = new Date();
        e.setFechaAlta(ahora);
        e.setFechaModificacion(ahora);
        e.setUsuarioCarga(u);
        e.setUsuarioModificacion(u);
    }
}
