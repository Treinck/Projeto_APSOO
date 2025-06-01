package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcessoAluno{

    public List<Aluno> buscarPorNome(String termo){
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT id, nome FROM aluno WHERE nome ILIKE ? ORDER BY nome";

        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, termo + "%"); // Começa com o termo (case-insensitive)
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
            	String nome = rs.getString("nome");
            	int id = rs.getInt("id");
                alunos.add(new Aluno(id, nome));
            }

        } catch (SQLException e){
            System.err.println("Erro na busca: " + e.getMessage());
        }

        return alunos;
    }

    public List<Aluno> listarTodos(){
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT nome, id FROM aluno ORDER BY nome";

        try (Connection conn = DatabaseConn.conectar();
             Statement ps = conn.createStatement();
             ResultSet rs = ps.executeQuery(sql)){

            while (rs.next()){
            	String nome = rs.getString("nome");
            	int id = rs.getInt("id");
                alunos.add(new Aluno(id, nome));
            }

        } catch (SQLException e){
            System.err.println("Erro ao listar alunos: " + e.getMessage());
        }

        return alunos;
    }
    
    public void inserirAluno(Aluno aluno, int endereco_id){
    	String sqlAluno = "INSERT INTO aluno (nome, telefone, telefone_emergencia, tipo_treino, endereco_id) VALUES (?, ?, ?, ?, ?)";
        
    	try (Connection conn = DatabaseConn.conectar();
             PreparedStatement psAluno = conn.prepareStatement(sqlAluno)){

            psAluno.setString(1, aluno.getNome());
            psAluno.setString(2, aluno.getTelefone());
            psAluno.setString(3, aluno.getTelefoneEmergencia());
            psAluno.setString(4, aluno.getTipoTreino());
            psAluno.setInt(5, endereco_id);
            psAluno.executeUpdate();

            } catch (SQLException e){
                System.err.println("Erro ao inserir aluno: " + e.getMessage());
            }
    }
    
    public int buscarEndereco(Endereco endereco) {
        String sql = "SELECT id FROM endereco WHERE rua = ? AND numero = ? AND bairro = ? AND cep = ? AND complemento = ?";
        
        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, endereco.getRua());
            stmt.setString(2, endereco.getNumero());
            stmt.setString(3, endereco.getBairro());
            stmt.setString(4, endereco.getCep());
            stmt.setString(5, endereco.getComplemento());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar endereço: " + e.getMessage());
        }

        return -1; // não encontrado
    }
   
    public void removerAluno(int x) {
    	String sql = "DELETE FROM aluno WHERE id = ?";

        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, x);
            ps.executeUpdate();

            } catch (SQLException e){
                System.err.println("Erro ao remover o aluno: " + e.getMessage());
            }
    }
    
    public void inserirAlunoComEndereco(Aluno aluno, Endereco endereco) throws SQLException{
        String sqlEndereco = "INSERT INTO endereco (rua, numero, bairro, cep, complemento) VALUES (?, ?, ?, ?, ?) RETURNING id";
        String sqlAluno = "INSERT INTO aluno (nome, telefone, telefone_emergencia, tipo_treino, endereco_id) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psEndereco = null;
        PreparedStatement psAluno = null;
        ResultSet rs = null;

        try{
        	conn = DatabaseConn.conectar();
            conn.setAutoCommit(false); //transação para garantir integridade

            //Insere endereço
            psEndereco = conn.prepareStatement(sqlEndereco);
            psEndereco.setString(1, endereco.getRua());
            psEndereco.setString(2, endereco.getNumero());
            psEndereco.setString(3, endereco.getBairro());
            psEndereco.setString(4, endereco.getCep());
            psEndereco.setString(5, endereco.getComplemento());

            rs = psEndereco.executeQuery();
            if (rs.next()) {
                int enderecoId = rs.getInt(1);

                //Insere aluno com o endereco_id
                psAluno = conn.prepareStatement(sqlAluno);
                psAluno.setString(1, aluno.getNome());
                psAluno.setString(2, aluno.getTelefone());
                psAluno.setString(3, aluno.getTelefoneEmergencia());
                psAluno.setString(4, aluno.getTipoTreino());
                psAluno.setInt(5, enderecoId);
                psAluno.executeUpdate();

                conn.commit(); //confirma a transação
            } else{
                conn.rollback();
                throw new SQLException("Falha ao obter ID do endereço");
            }

        }catch (SQLException e){
            if (conn != null) conn.rollback();
            throw e;

        }finally{
            if (rs != null) rs.close();
            if (psEndereco != null) psEndereco.close();
            if (psAluno != null) psAluno.close();
            if (conn != null) conn.close();
        }
    }
}