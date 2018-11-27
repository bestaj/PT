package data;

import java.util.TimeZone;

/**
 * Tøída {@code Objednavka} reprezentující objednávku.
 *
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class Objednavka {
	/** Èas, kdy zaèíná simulace. (8:00) */
	private final long CAS = 28800000;
	/** Mìsto, které si objednalo palety */
	private final int MESTO;
	/** Poèet palet, kolik si dané mìsto objednalo */
	private final int PALET;
	/** Èas, kdy pøišla objednávka */
	private final String CAS_OBJ;
	/** Zda je objednávka pøijata nebo není */
	private boolean prijato;
	/** Èíslo objednávky */
	private int cisloObjednavky;
	/** Èas v kolik bude objednávka doruèena */
	private int casDoruceni;
	/** Èíslo náklaïáku, který bude objednávku doruèovat */
	private int cisloNakladaku;

	/**
	 * Vytvoøí novou objednávku pro dané mìsto, s daným poètem palet 
	 * a s èasem, kdy pøišla objednávka.
	 * 
	 * @param mesto Mesto, které objednává.
	 * @param palet Poèet palet, které si mìsto objednalo.
	 * @param casObjednavky èas, kdy pøišla objednávka
	 */
	public Objednavka(int mesto, int palet, String casObjednavky) {
		this.MESTO = mesto;
		this.PALET = palet;
		this.CAS_OBJ = casObjednavky;
	}

	/** Vrátí èíslo objednávky.
	 * @return èíslo objednávky
	 */
	public int getCisloObjednavky() {
		return cisloObjednavky;
	}

	/** Nastaví èíslo objednávky.
	 * @param cisloObjednavky èíslo pøiøazené objednávce
	 */
	public void setCisloObjednavky(int cisloObjednavky) {
		this.cisloObjednavky = cisloObjednavky;
	}

	/** Vrátí èas doruèení objednávky.
	 * @return èas doruèení
	 */
	public int getCasDoruceni() {
		return casDoruceni;
	}

	/** Nastaví èas doruèení objednávky. 
	 * @param casDoruceni èas doruèení objednávky
	 */
	public void setCasDoruceni(int casDoruceni) {
		this.casDoruceni = casDoruceni;
	}
	
	/** Vrátí, zda je objednávka pøijata nebo zamítnuta.
	 * @return prijato true-objednávka je pøijata, false-objednávka je zamítnuta
	 */
	public boolean isPrijato() {
		return prijato;
	}
	
	/** Pøijme nebo zamítne objednávku. 
	 * @param prijato true-objednávka je pøijata, false-objednávka je zamítnuta
	 */
	public void setPrijato(boolean prijato) {
		this.prijato = prijato;
	}

	/** Vrátí èíslo mìsto.
	* @return mesto mìsto, které si objednávalo
	*/
	public int getMesto() {
		return MESTO;
	}

	/** Vrátí poèet palet. 
	 * @return palet poèet palet, které si dané mìsto objednalo
	 */
	public int getPalet() {
		return PALET;
	}

	/** Vrátí èas, kdy pøišla objednávka.	
	 * @return casObjednavky èas, kdy pøišla objednávka
	 */
	public String getCasObjednavky() {
		return CAS_OBJ;
	}
	
	/** Vrátí èíslo nákladního auta, které doruèuje danou objednávku.
	 * @return cisloNakladaku èíslo nákladního auta
	 */
	public int getCisloNakladaku() {
		return cisloNakladaku;
	}
	
	/** Pøiøadí dané objednávce nákladní auto.
	 * @param cislo èíslo nákladního auta
	 */
	public void setCisloNakladaku(int cislo) {
		this.cisloNakladaku = cislo;
	}
	
	/** Vrátí èas ve formátu hh:mm:ss
	 * @param millis èas, který se má pøevést (v milisekundách)
	 * @return èas ve formátu hh:mm:ss
	 */
	public String setTime(long millis) {
		if (millis == 0) {
			setTime(CAS);
		}
	    return String.format("%tT", millis-TimeZone.getDefault().getRawOffset());
	}
	
	/** Vrátí textovou reprezentaci objednávky */
	@Override
	public String toString() {
		String vypis = "";
		vypis += "Objednávka èislo " + cisloObjednavky + " je";
		if (isPrijato()) {
			vypis += " pøijata. Èas doruèení: " + setTime(casDoruceni*1000);
		} else {
			vypis += " odmítnuta.";
		}
		return vypis;
	}

	/** Vrátí struèný popis objednávky
	 * @return struèný popis objednávky
	 */
	public String strucnyPopis() {
		return "Objednávka èíslo " + cisloObjednavky + "\nMìsto: " + MESTO + " Palet: " + PALET + "\nÈas objednávky: " + CAS_OBJ;
	}
}
