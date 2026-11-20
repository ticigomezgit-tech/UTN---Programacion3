package clinica;

import clinica.entidades.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClinicaPU");
        EntityManager em = emf.createEntityManager();

        try {
            cargarDatos(em);
            ejecutarConsultas(em);
        } finally {
            em.close();
            emf.close();
        }
    }

    // ------------------------------------------------------------------
    // Persistencia de datos de prueba
    // ------------------------------------------------------------------
    private static void cargarDatos(EntityManager em) {
        em.getTransaction().begin();
        try {
            // 2 médicos
            Medico m1 = new Medico("Ana", "Torres", "Cardiología", "MP-1234");
            Medico m2 = new Medico("Luis", "Ramírez", "Clínica médica", "MP-5678");
            em.persist(m1);
            em.persist(m2);

            // 3 medicamentos
            Medicamento ibuprofeno = new Medicamento("Ibuprofeno", "400 mg");
            Medicamento paracetamol = new Medicamento("Paracetamol", "500 mg");
            Medicamento amoxicilina = new Medicamento("Amoxicilina", "875 mg");
            em.persist(ibuprofeno);
            em.persist(paracetamol);
            em.persist(amoxicilina);

            // 3 pacientes (2 con historial clínico, persistido en cascada)
            Paciente p1 = new Paciente("Juan", "Pérez", 45, "OSDE");
            p1.setHistorialClinico(new HistorialClinico(
                    "Hipertensión arterial controlada con medicación", LocalDate.of(2020, 3, 15)));
            p1.agregarMedicamento(ibuprofeno);
            p1.agregarMedicamento(paracetamol);

            Paciente p2 = new Paciente("María", "Gómez", 28, "Swiss Medical");
            p2.setHistorialClinico(new HistorialClinico(
                    "Alergia estacional y cuadros respiratorios recurrentes", LocalDate.of(2022, 7, 2)));
            p2.agregarMedicamento(amoxicilina);

            Paciente p3 = new Paciente("Carlos", "López", 35, "OSDE");
            p3.agregarMedicamento(paracetamol);

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            // 4 consultas
            em.persist(new Consulta(LocalDate.of(2026, 1, 10), "Hipertensión", p1, m1));
            em.persist(new Consulta(LocalDate.of(2026, 2, 5), "Faringitis aguda", p2, m2));
            em.persist(new Consulta(LocalDate.of(2026, 3, 18), "Lumbalgia", p3, m2));
            em.persist(new Consulta(LocalDate.of(2026, 4, 22), "Control cardiológico", p1, m1));

            em.getTransaction().commit();
            System.out.println("\n=== Datos persistidos correctamente ===\n");
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // ------------------------------------------------------------------
    // Consultas JPQL
    // ------------------------------------------------------------------
    private static void ejecutarConsultas(EntityManager em) {

        // 1. Pacientes mayores de 30 años
        titulo("1. Pacientes mayores de 30 años");
        List<Paciente> mayores = em.createQuery(
                "SELECT p FROM Paciente p WHERE p.edad > :edad", Paciente.class)
                .setParameter("edad", 30)
                .getResultList();
        mayores.forEach(p -> System.out.println(p));

        // 2. Consultas realizadas por un médico específico (por matrícula)
        titulo("2. Consultas del médico MP-1234");
        List<Consulta> deMedico = em.createQuery(
                "SELECT c FROM Consulta c WHERE c.medico.matricula = :matricula", Consulta.class)
                .setParameter("matricula", "MP-1234")
                .getResultList();
        deMedico.forEach(c -> System.out.println(c));

        // 3. Medicamentos asociados a un paciente
        titulo("3. Medicamentos de Juan Pérez");
        List<Medicamento> meds = em.createQuery(
                "SELECT m FROM Paciente p JOIN p.medicamentos m "
                + "WHERE p.nombre = :nombre AND p.apellido = :apellido", Medicamento.class)
                .setParameter("nombre", "Juan")
                .setParameter("apellido", "Pérez")
                .getResultList();
        meds.forEach(m -> System.out.println(m));

        // 4. Consultas con su diagnóstico y el nombre del paciente
        titulo("4. Consultas con diagnóstico y paciente");
        List<Object[]> consultas = em.createQuery(
                "SELECT c.fecha, c.diagnostico, p.nombre, p.apellido "
                + "FROM Consulta c JOIN c.paciente p ORDER BY c.fecha", Object[].class)
                .getResultList();
        for (Object[] f : consultas) {
            System.out.println(f[0] + " | " + f[1] + " | " + f[2] + " " + f[3]);
        }

        // 5. Promedio de edad de los pacientes
        titulo("5. Promedio de edad de los pacientes");
        Double promedio = em.createQuery(
                "SELECT AVG(p.edad) FROM Paciente p", Double.class)
                .getSingleResult();
        System.out.printf("Promedio: %.2f años%n", promedio);

        // 6. Pacientes con una obra social específica
        titulo("6. Pacientes con obra social OSDE");
        List<Paciente> deOs = em.createQuery(
                "SELECT p FROM Paciente p WHERE p.obraSocial = :os", Paciente.class)
                .setParameter("os", "OSDE")
                .getResultList();
        deOs.forEach(p -> System.out.println(p));

        // 7. Médicos y cantidad de consultas que atendieron
        titulo("7. Médicos y cantidad de consultas");
        List<Object[]> porMedico = em.createQuery(
                "SELECT m.nombre, m.apellido, COUNT(c) "
                + "FROM Medico m LEFT JOIN m.consultas c "
                + "GROUP BY m.id, m.nombre, m.apellido", Object[].class)
                .getResultList();
        for (Object[] f : porMedico) {
            System.out.println("Dr/a. " + f[0] + " " + f[1] + " -> " + f[2] + " consultas");
        }

        // 8. Pacientes junto con la descripción de su historia clínica
        //    (LEFT JOIN: incluye también a los pacientes sin historial)
        titulo("8. Pacientes con su historia clínica");
        List<Object[]> conHistorial = em.createQuery(
                "SELECT p.nombre, p.apellido, h.descripcion "
                + "FROM Paciente p LEFT JOIN p.historialClinico h", Object[].class)
                .getResultList();
        for (Object[] f : conHistorial) {
            String desc = f[2] != null ? (String) f[2] : "(sin historial clínico)";
            System.out.println(f[0] + " " + f[1] + " -> " + desc);
        }
    }

    private static void titulo(String t) {
        System.out.println("\n--- " + t + " ---");
    }
}
