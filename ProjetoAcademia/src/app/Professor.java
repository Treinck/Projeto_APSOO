package app;

public class Professor{
    private int id;
    private String nome;
    private String telefone;
    private String telefoneEmergencia;
    private Endereco e;

    public Professor(int id, String nome, String telefone, String tE, Endereco end){
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.telefoneEmergencia = tE;
        this.e = end;
    }
    
    public Professor(String nome, String telefone, String tE, Endereco end){
        this.nome = nome;
        this.telefone = telefone;
        this.telefoneEmergencia = tE;
        this.e = end;
    }
    
    public Professor(int id, String nome) {
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