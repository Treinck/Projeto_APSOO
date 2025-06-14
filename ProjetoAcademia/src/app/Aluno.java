package app;

public class Aluno{
    private int id;
    private String nome;
    private String telefone;
    private String telefoneEmergencia;
    private String tipoTreino;
    private String problemas;
    private Endereco e;

    public Aluno(int id, String nome, String telefone, String tE, String tipoTreino, String problemas, Endereco end){
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.telefoneEmergencia = tE;
        this.tipoTreino = tipoTreino;
        this.problemas = problemas;
        this.e = end;
    }
    
    public Aluno(String nome, String telefone, String tE, String tipoTreino, String problemas, Endereco end){
        this.nome = nome;
        this.telefone = telefone;
        this.telefoneEmergencia = tE;
        this.tipoTreino = tipoTreino;
        this.problemas = problemas;
        this.e = end;
    }
    
    public Aluno(int id, String nome) {
    	this.id = id;
    	this.nome = nome;
    }

    public int getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }
    
    public String getProblemas(){
        return problemas;
    }

    public String getTelefone(){
        return telefone;
    }

    public String getTelefoneEmergencia(){
        return telefoneEmergencia;
    }
    
    public String getTipoTreino(){
        return tipoTreino;
    }
    
    public Endereco getEndereco(){
    	return e;
    }

    @Override
    public String toString(){
        return "ID: " + id + " | Nome: " + nome;
    }
}