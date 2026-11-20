package clinica.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Paciente extends Base {

    private String nombre;
    private String apellido;
    private int edad;
    private String obraSocial;

    // 1:1 - al persistir el paciente se persiste también su historial
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "historial_clinico_id")
    private HistorialClinico historialClinico;

    // 1:N - una consulta pertenece a un solo paciente
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    // N:M - lado dueño de la relación
    @ManyToMany
    @JoinTable(name = "paciente_medicamento",
            joinColumns = @JoinColumn(name = "paciente_id"),
            inverseJoinColumns = @JoinColumn(name = "medicamento_id"))
    private Set<Medicamento> medicamentos = new HashSet<>();

    public Paciente() {}

    public Paciente(String nombre, String apellido, int edad, String obraSocial) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.obraSocial = obraSocial;
    }

    public void agregarMedicamento(Medicamento m) {
        medicamentos.add(m);
        m.getPacientes().add(this);
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getObraSocial() { return obraSocial; }
    public void setObraSocial(String obraSocial) { this.obraSocial = obraSocial; }

    public HistorialClinico getHistorialClinico() { return historialClinico; }
    public void setHistorialClinico(HistorialClinico historialClinico) { this.historialClinico = historialClinico; }

    public List<Consulta> getConsultas() { return consultas; }
    public void setConsultas(List<Consulta> consultas) { this.consultas = consultas; }

    public Set<Medicamento> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(Set<Medicamento> medicamentos) { this.medicamentos = medicamentos; }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + edad + " años, " + obraSocial + ")";
    }
}
