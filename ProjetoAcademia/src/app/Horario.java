package app;

public class Horario {
    private int id;
    private String diaSemana;
    private String horaInicio;

    public Horario(int id, String diaSemana, String horaInicio) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
    }

    public int getId() {
        return id;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    @Override
    public String toString() {
        return diaSemana + " " + horaInicio;
    }
}