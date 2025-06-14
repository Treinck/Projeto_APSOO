package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcessoGrupo {

	//cria um grupo de alunos
    public void inserirGrupo(Grupo grupo) throws SQLException {
        String sqlGrupo = "INSERT INTO grupo (professor_id) VALUES (?) RETURNING id";
        String sqlGrupoAluno = "INSERT INTO grupo_aluno (grupo_id, aluno_id) VALUES (?, ?)";
        String sqlGrupoHorario = "INSERT INTO grupo_horario (grupo_id, horario_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement psGrupo = null;
        PreparedStatement psAluno = null;
        PreparedStatement psHorario = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConn.conectar();
            conn.setAutoCommit(false);

            // Inserir grupo
            psGrupo = conn.prepareStatement(sqlGrupo);
            psGrupo.setInt(1, grupo.getProfessor().getId());
            rs = psGrupo.executeQuery();

            int grupoId = -1;
            if (rs.next()) {
                grupoId = rs.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Falha ao criar grupo");
            }

            // Inserir alunos do grupo
            psAluno = conn.prepareStatement(sqlGrupoAluno);
            List<Aluno> alunos = grupo.getAlunos();
            for (int i = 0; i < alunos.size(); i++) {
                psAluno.setInt(1, grupoId);
                psAluno.setInt(2, alunos.get(i).getId());
                psAluno.addBatch();
            }
            psAluno.executeBatch();

            // Inserir horários do grupo
            psHorario = conn.prepareStatement(sqlGrupoHorario);
            List<Horario> horarios = grupo.getHorarios();
            for (int i = 0; i < horarios.size(); i++) {
                psHorario.setInt(1, grupoId);
                psHorario.setInt(2, horarios.get(i).getId());
                psHorario.addBatch();
            }
            psHorario.executeBatch();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (psGrupo != null) psGrupo.close();
            if (psAluno != null) psAluno.close();
            if (psHorario != null) psHorario.close();
            if (conn != null) conn.close();
        }
    }

    public List<Grupo> listarGruposComAlunosEHorarios() {
        List<Grupo> grupos = new ArrayList<>();
        String sql = """
            SELECT g.id AS grupo_id, p.id AS professor_id, p.nome AS professor_nome
            FROM grupo g
            JOIN professor p ON g.professor_id = p.id
            ORDER BY g.id
        """;

        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Professor professor = new Professor(rs.getInt("professor_id"), rs.getString("professor_nome"));
                Grupo grupo = new Grupo(professor);
                carregarAlunosDoGrupo(conn, grupo, rs.getInt("grupo_id"));
                carregarHorariosDoGrupo(conn, grupo, rs.getInt("grupo_id"));
                grupos.add(grupo);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar grupos: " + e.getMessage());
        }

        return grupos;
    }

    private void carregarAlunosDoGrupo(Connection conn, Grupo grupo, int grupoId) throws SQLException {
        String sql = """
            SELECT a.id, a.nome
            FROM grupo_aluno ga
            JOIN aluno a ON ga.aluno_id = a.id
            WHERE ga.grupo_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                grupo.adicionarAluno(new Aluno(rs.getInt("id"), rs.getString("nome")));
            }
        }
    }

    private void carregarHorariosDoGrupo(Connection conn, Grupo grupo, int grupoId) throws SQLException {
        String sql = """
            SELECT h.id, h.dia_semana, h.hora_inicio
            FROM grupo_horario gh
            JOIN horario h ON gh.horario_id = h.id
            WHERE gh.grupo_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Horario horario = new Horario(
                    rs.getInt("id"),
                    rs.getString("dia_semana"),
                    rs.getString("hora_inicio")
                );
                grupo.adicionarHorario(horario);
            }
        }
    }

    public boolean alunoTemConflitoDeHorario(int alunoId, int horarioId) {
        String sql = """
            SELECT 1
            FROM grupo_aluno ga
            JOIN grupo_horario gh ON ga.grupo_id = gh.grupo_id
            WHERE ga.aluno_id = ?
            AND gh.horario_id = ?
        """;

        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, alunoId);
            ps.setInt(2, horarioId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Erro ao verificar conflito de horário: " + e.getMessage());
            return true;
        }
    }

    public void transferirAlunoDeGrupo(int alunoId, int novoGrupoId) {
        String sqlHorariosDoNovoGrupo = """
            SELECT horario_id
            FROM grupo_horario
            WHERE grupo_id = ?
        """;

        String sqlVerificarConflito = """
            SELECT 1
            FROM grupo_aluno ga
            JOIN grupo_horario gh ON ga.grupo_id = gh.grupo_id
            WHERE ga.aluno_id = ?
            AND gh.horario_id = ?
        """;

        String sqlDelete = "DELETE FROM grupo_aluno WHERE aluno_id = ?";
        String sqlInsert = "INSERT INTO grupo_aluno (grupo_id, aluno_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConn.conectar()) {
            conn.setAutoCommit(false);

            // Pega os horários do novo grupo
            List<Integer> horariosDoNovoGrupo = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlHorariosDoNovoGrupo)) {
                ps.setInt(1, novoGrupoId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    horariosDoNovoGrupo.add(rs.getInt("horario_id"));
                }
            }

            if (horariosDoNovoGrupo.isEmpty()) {
                conn.rollback();
                throw new SQLException("O novo grupo não tem horários definidos!");
            }

            // Verificar conflitos
            for (int i = 0; i < horariosDoNovoGrupo.size(); i++) {
                int horarioId = horariosDoNovoGrupo.get(i);
                try (PreparedStatement ps = conn.prepareStatement(sqlVerificarConflito)) {
                    ps.setInt(1, alunoId);
                    ps.setInt(2, horarioId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        conn.rollback();
                        throw new SQLException("Conflito de horário: o aluno já está em outro grupo nesse horário!");
                    }
                }
            }

            // Remover de todos os grupos antigos
            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                ps.setInt(1, alunoId);
                ps.executeUpdate();
            }

            // Inserir no novo grupo
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, novoGrupoId);
                ps.setInt(2, alunoId);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Aluno transferido com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao transferir aluno: " + e.getMessage());
        }
    }
}
