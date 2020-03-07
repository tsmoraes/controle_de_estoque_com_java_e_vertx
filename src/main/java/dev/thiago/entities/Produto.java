package dev.thiago.entities;

public class Produto {

	private int id;
	
    private String nome;

    private String barra;
    
    public Produto() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getBarra() {
		return barra;
	}

	public void setBarra(String barra) {
		this.barra = barra;
	}
}
