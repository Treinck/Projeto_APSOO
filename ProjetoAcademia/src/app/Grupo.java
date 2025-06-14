package app;

import java.util.ArrayList;
import java.util.List;

public class Grupo {
    private Professor professor;
    private List<Aluno> alunos;
    private List<Horario> horarios;

    public Grupo(Professor professor) {
        this.professor = professor;
        this.alunos = new ArrayList<>();
        this.horarios = new ArrayList<>();
    }

    public void adicionarAluno(Aluno aluno) {
        alunos.add(aluno);
    }

    public void adicionarHorario(Horario horario) {
        horarios.add(horario);
    }

    public Professor getProfessor() {
        return professor;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Professor: ").append(professor.getNome()).append("\n");
        sb.append("Alunos: ");
        for (Aluno a : alunos) {
            sb.append(a.getNome()).append(", ");
        }
        sb.append("\nHorários: ");
        for (Horario h : horarios) {
            sb.append(h.toString()).append(", ");
        }
        return sb.toString();
    }
}
