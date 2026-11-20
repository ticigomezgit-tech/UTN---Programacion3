package clinica.entidades;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class HistorialClinico extends Base {

    private String descripcion;
    private LocalDate fechaApertura;

    public HistorialClinico() {}

    public HistorialClinico(String descripcion, LocalDate fechaApertura) {
        this.descripcion = descripcion;
        this.fechaApertura = fechaApertura;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDate fechaApertura) { this.fechaApertura = fechaApertura; }
}
