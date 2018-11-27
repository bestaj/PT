package data;

/**
 * T��da {@code Mesto} reprezentuje m�sto.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Mesto {
	/** ��slo m�sta */
	private final int ID;
	/** Maxim�ln� po�et palet, kter� m��e dan� m�sto objednat */
	private final int MAX_PALET;
	/** Indikuje, zda dan� m�sto u� v jeden den objedn�valo. */
	private boolean dnesObjednano;
	
	/** Vytvo�� nov� m�sto s p�i�azen�m ��slem 
	 * a maxim�ln�m po�tem palet. 
	 * 
	 * @param id ��slo m�sta
	 * @param maxPalet maxim�ln� po�et palet
	 */
	public Mesto(int id, int maxPalet) {
		this.ID = id;
		this.MAX_PALET = maxPalet;
		this.dnesObjednano = false;
	}
	
	/** Vr�t� maxim�ln� po�et palet, kter� m��e dan� m�sto objednat. 
	 * @return maxPalet maxim�ln� po�et palet
	 */
	public int getMaxPalet() {
		return this.MAX_PALET;
	}
	
	/** Vr�t�, zda dan� m�sto ji� dnes objedn�valo.
	 * @return true-pokud m�sto ji� dnes objedn�valo, false-pokud je�t� dnes neobjedn�valo
	 */
	public boolean isDnesObjednano() {
		return this.dnesObjednano;
	}
	
	/** Nastav�, jestli m�sto dnes ji� objedn�valo nebo ne
	 * @param dnesObjednano true-pokud m�sto ji� dnes objedn�valo, false-pokud je�t� dnes neobjedn�valo
	 */
	public void setDnesObjednano(boolean dnesObjednano) {
		this.dnesObjednano = dnesObjednano;
	}
	
	/** Vr�t� textovou reprezentaci m�sta */
	@Override
	public String toString() {
		return "Mesto_" + ID;
	}
}
