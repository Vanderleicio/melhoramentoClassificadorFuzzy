package Fuzzy.ControllerNaoEstacionario;

public enum Metricas {
	AUC("AUC"),
	GMEAN("GMean"),
	F1SCORE("F1Score");
	
	private String nome;
	
	Metricas(String nome){
		this.nome = nome;
	}
	
	public String getNome() {
		return this.nome;
	}
}
