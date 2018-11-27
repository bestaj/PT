package data;

/**
 * Tøída {@code Mesto} reprezentuje mìsto.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class Mesto {
	/** Èíslo mìsta */
	private final int ID;
	/** Maximální poèet palet, které mùže dané mìsto objednat */
	private final int MAX_PALET;
	/** Indikuje, zda dané mìsto už v jeden den objednávalo. */
	private boolean dnesObjednano;
	
	/** Vytvoøí nové mìsto s pøiøazeným èíslem 
	 * a maximálním poètem palet. 
	 * 
	 * @param id èíslo mìsta
	 * @param maxPalet maximální poèet palet
	 */
	public Mesto(int id, int maxPalet) {
		this.ID = id;
		this.MAX_PALET = maxPalet;
		this.dnesObjednano = false;
	}
	
	/** Vrátí maximální poèet palet, které mùže dané mìsto objednat. 
	 * @return maxPalet maximální poèet palet
	 */
	public int getMaxPalet() {
		return this.MAX_PALET;
	}
	
	/** Vrátí, zda dané mìsto již dnes objednávalo.
	 * @return true-pokud mìsto již dnes objednávalo, false-pokud ještì dnes neobjednávalo
	 */
	public boolean isDnesObjednano() {
		return this.dnesObjednano;
	}
	
	/** Nastaví, jestli mìsto dnes již objednávalo nebo ne
	 * @param dnesObjednano true-pokud mìsto již dnes objednávalo, false-pokud ještì dnes neobjednávalo
	 */
	public void setDnesObjednano(boolean dnesObjednano) {
		this.dnesObjednano = dnesObjednano;
	}
	
	/** Vrátí textovou reprezentaci mìsta */
	@Override
	public String toString() {
		return "Mesto_" + ID;
	}
}
