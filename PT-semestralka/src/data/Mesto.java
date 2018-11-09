package data;

public class Mesto {

	private int id;
	
	private int maxPalet;
	
	private boolean dnesObjednano;
	
	public Mesto(int id, int maxPalet) {
		this.id = id;
		this.maxPalet = maxPalet;
		this.dnesObjednano = false;
	}
	
	public int getMaxPalet() {
		return this.maxPalet;
	}
	
	public boolean isDnesObjednano() {
		return this.dnesObjednano;
	}
	
	public void setDnesObjednano(boolean dnesObjednano) {
		this.dnesObjednano = dnesObjednano;
	}
	
	@Override
	public String toString() {
		return "Mesto_" + id;
	}
}
