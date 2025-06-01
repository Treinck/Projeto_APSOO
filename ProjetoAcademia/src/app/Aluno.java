package app;

public class Aluno{
    private int id;
    private String nome;
    private String telefone;
    private String telefoneEmergencia;
    private String tipoTreino;
    private Endereco e;

    public Aluno(int id, String nome, String telefone, String tE, String tipoTreino, Endereco end){
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.telefoneEmergencia = tE;
        this.tipoTreino = tipoTreino;
        this.e = end;
    }
    
    public Aluno(String nome, String telefone, String tE, String tipoTreino, Endereco end){
        this.nome = nome;
        this.telefone = telefone;
        this.telefoneEmergencia = tE;
        this.tipoTreino = tipoTreino;
        this.e = end;
    }
    
    public Aluno(int id, String nome) {
    	this.id = id;
    	this.nome = nome;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getTelefone(){
        return telefone;
    }

    public void setTelefone(String telefone){
        this.telefone = telefone;
    }

    public String getTelefoneEmergencia(){
        return telefoneEmergencia;
    }

    public void setTelefoneEmergencia(String telefoneEmergencia){
        this.telefoneEmergencia = telefoneEmergencia;
    }

    public String getTipoTreino(){
        return tipoTreino;
    }

    public void setTipoTreino(String tipoTreino){
        this.tipoTreino = tipoTreino;
    }
    
    public Endereco getEndereco(){
    	return e;
    }
    public void setEndereco(Endereco endereco){
    	this.e = endereco;
    }

    @Override
    public String toString(){
        return "ID: " + id + " | Nome: " + nome;
    }
}