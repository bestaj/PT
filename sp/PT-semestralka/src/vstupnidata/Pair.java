package vstupnidata;
/**
 * Trida {@code Pair} predstavuje generickou tridu
 * pro ulozeni paru hodnot danych datovych typu
 * 
 * @author Jiri Besta
 */
public class Pair {
	
	private int first;
	private int second;
	
	/**
	 * Vytvori novy par
	 * @param first prvni prvek
	 * @param second druhy prvek
	 */
	public Pair(int first, int second) {
		this.first = first;
		this.second = second;
	}

	
	public int getFirst() {
		return this.first;
	}
	
	public int getSecond() {
		return this.second;
	}
	
	public boolean equals(Pair o) {
		if (o.first == this.first && o.second == this.second) {
			return true;
		}
		else 
			return false;
	}
	
	@Override
	public String toString() {
		return first + " " + second;
	}
}
