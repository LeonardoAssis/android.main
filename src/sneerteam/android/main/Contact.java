package sneerteam.android.main;

public class Contact {
	
	private String nome;
	
	public Contact(String nome) {
		this.nome = nome;
	}
	
	public String getName() {
		return nome;
	}
	
	@Override public String toString() {
		return nome;
	}
	
}

