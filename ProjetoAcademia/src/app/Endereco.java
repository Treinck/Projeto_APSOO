package app;

public class Endereco {
	private String rua;
	private String numero; 
    private String bairro;
    private String cep;
    private String complemento;
	private int id;
	
	public Endereco(String rua, String numero, String bairro, String cep, String complemento, int id){
		
    	this.bairro = bairro;
    	this.cep = cep;
    	this.complemento = complemento;
    	this.id = id;
    }
	
	public Endereco(String rua, String numero, String bairro, String cep, String complemento){
		this.rua = rua;
		this.numero = numero;
    	this.bairro = bairro;
    	this.cep = cep;
    	this.complemento = complemento;
    }
	
	public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
	
	public String getNumero(){
	    return numero;
	}
	
	public void setNumero(String numero){
	    this.numero = numero;
	}

	public String getRua(){
	    return rua;
	}
	
	public void setRua(String rua){
	    this.rua = rua;
	}

	public String getBairro(){
	    return bairro;
	}
	
	public void setBairro(String bairro){
	    this.bairro = bairro;
	}

	public String getCep(){
	    return cep;
	}
	
	public void setCep(String cep){
	    this.cep = cep;
	}

	public String getComplemento(){
	    return complemento;
	}
	
	public void setComplemento(String complemento){
	    this.complemento = complemento;
	}

    
    @Override
    public String toString(){
        return "Rua: " + rua + " | Bairro: " + bairro + " | Nº: " + numero + "\nCEP: " + cep + " | Complemento: " + complemento;
    }
}
