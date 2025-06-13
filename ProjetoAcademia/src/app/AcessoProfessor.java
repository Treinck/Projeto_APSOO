package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcessoProfessor{

	public List<Professor> buscarPorNome(String termo){
        List<Professor> profs = new ArrayList<>();
        String sql = "SELECT id, nome FROM professor WHERE nome ILIKE ? ORDER BY nome";

        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement psProf = conn.prepareStatement(sql)){

            psProf.setString(1, termo + "%"); // Começa com o termo (case-insensitive)
            ResultSet rs = psProf.executeQuery();

            while (rs.next()){
            	String nome = rs.getString("nome");
            	int id = rs.getInt("id");
                profs.add(new Professor(id, nome));
            }

        } catch (SQLException e){
            System.err.println("Erro na busca: " + e.getMessage());
        }

        return profs;
    }
    
	public List<Professor> listarProfessoresComEndereco() {
	    List<Professor> profs = new ArrayList<>();
	    String sql = """
	        SELECT p.id AS professor_id, p.nome, p.telefone, p.telefone_emergencia, e.rua, e.numero, e.bairro, e.cep, e.complemento
	        FROM professor p
	        JOIN endereco e ON p.endereco_id = e.id
	        ORDER BY p.nome
	    """;

	    try (Connection conn = DatabaseConn.conectar();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Endereco endereco = new Endereco(
	                rs.getString("rua"),
	                rs.getString("numero"),
	                rs.getString("bairro"),
	                rs.getString("cep"),
	                rs.getString("complemento")
	            );

	            Professor professor = new Professor(
	                rs.getInt("professor_id"),
	                rs.getString("nome"),
	                rs.getString("telefone"),
	                rs.getString("telefone_emergencia"),
	                endereco
	            );

	            profs.add(professor);
	        }

	    } catch (SQLException e) {
	        System.err.println("Erro ao listar professores com endereço: " + e.getMessage());
	    }

	    return profs;
	}
	
    public void inserirProfessor(Professor professor, int endereco_id){
    	String sqlProfessor = "INSERT INTO professor (nome, telefone, telefone_emergencia, endereco_id) VALUES (?, ?, ?, ?)";
        
    	try (Connection conn = DatabaseConn.conectar();
             PreparedStatement psProfessor = conn.prepareStatement(sqlProfessor)){

            psProfessor.setString(1, professor.getNome());
            psProfessor.setString(2, professor.getTelefone());
            psProfessor.setString(3, professor.getTelefoneEmergencia());
            psProfessor.setInt(4, endereco_id);
            psProfessor.executeUpdate();

            } catch (SQLException e){
                System.err.println("Erro ao inserir professor: " + e.getMessage());
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
            stmt.setString(5, endereco.getComplemento());//seta os valores do endereco

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar endereço: " + e.getMessage());
        }

        return -1; // não encontrado
    }
   
    public void inserirProfessorComEndereco(Professor professor, Endereco endereco) throws SQLException{
        String sqlEndereco = "INSERT INTO endereco (rua, numero, bairro, cep, complemento) VALUES (?, ?, ?, ?, ?) RETURNING id";
        String sqlProfessor = "INSERT INTO professor (nome, telefone, telefone_emergencia, endereco_id) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psEndereco = null;
        PreparedStatement psProfessor = null;
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

                //Insere professor com o endereco_id
                psProfessor = conn.prepareStatement(sqlProfessor);
                psProfessor.setString(1, professor.getNome());
                psProfessor.setString(2, professor.getTelefone());
                psProfessor.setString(3, professor.getTelefoneEmergencia());
                psProfessor.setInt(4, enderecoId);
                psProfessor.executeUpdate();

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
            if (psProfessor != null) psProfessor.close();
            if (conn != null) conn.close();
        }
    }
    
    public void removerProfessor(int x) {
    	String sql = "DELETE FROM professor WHERE id = ?";

        try (Connection conn = DatabaseConn.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, x);
            ps.executeUpdate();

            } catch (SQLException e){
                System.err.println("Erro ao remover o professor: " + e.getMessage());
            }
    }
}
