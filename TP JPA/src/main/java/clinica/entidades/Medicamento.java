package clinica.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Medicamento extends Base {

    private String nombre;
    private String dosis;

    // Lado inverso de la relación (el dueño es Paciente)
    @ManyToMany(mappedBy = "medicamentos")
    private Set<Paciente> pacientes = new HashSet<>();

    public Medicamento() {}

    public Medicamento(String nombre, String dosis) {
        this.nombre = nombre;
        this.dosis = dosis;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public Set<Paciente> getPacientes() { return pacientes; }
    public void setPacientes(Set<Paciente> pacientes) { this.pacientes = pacientes; }

    @Override
    public String toString() {
        return nombre + " (" + dosis + ")";
    }
}
