package data;

import java.util.ArrayList;

/**
 * Tøída {@code Nakladak} reprezentující nákladní auto.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class Nakladak {
	/** Poèet palet kolik veze dané nákladní auto */
	private int rozvezPalet;
	/** Celková ujetá vzdálenost nákladního auta */
	private int kolikUjelKm;
	/** Celkový èas strávený na cestách */
	private int kolikUjelCasu;
	/** Èíslo nákladního auta */
	private int cisloNakladaku;
	/** Náklady na dopravu daného nákladního auta */
	private int naklady;
	/** Seznam objednávek, které dané nákladního auto rozváží */
	private ArrayList<Integer> objednavkyCoVeze;

	/** Vytvoøí nové nákladní auto s pøidìleným èíslem.
	 * 
	 * @param cisloNakladaku oznaèení nákladního auta
	 */
	public Nakladak(int cisloNakladaku) {
		this.cisloNakladaku = cisloNakladaku;
		objednavkyCoVeze = new ArrayList<>();
	}

	/** Vrátí èas, jak dlouho nákladní auto rozváží.
	 * @return kolikUjelCasu èas strávený na cestách
	 */
	public int getKolikUjelCasu() {
		return kolikUjelCasu;
	}

	/** Nastaví èas, jak dlouho bude trvat doruèení všech doruèovaných objednávek
	 * s vrácením se zpìt do domovského mìsta.
	 * @param kolikUjelCasu trvání doruèování objednávek
	 */
	public void setKolikUjelCasu(int kolikUjelCasu) {
		this.kolikUjelCasu = kolikUjelCasu;
	}
	
//	public void setCisloNakladaku(int cisloNakladaku) {
//		this.cisloNakladaku = cisloNakladaku;
//	}

	/** Vrátí náklady na dané nákladní auto.
	 * @return naklady náklady na dopravu daného nákladního auta
	 */
	public int getNaklad() {
		return naklady;
	}

	/** Nastaví náklady pro dané nákladní auto.
	 * @param naklady náklady na dopravu daného nákladního auta
	 */
	public void setNaklad(int naklady) {
		this.naklady = naklady;
	}

	/** Vrátí poèet doruèovaných palet .
	 * @return rozvezPalet poèet rozvážených palet
	 */
	public int getRozvezPalet() {
		return rozvezPalet;
	}

	/** Nastaví poèet rozvážených palet.
	 * @param rozvezPalet poèet rozvážených palet
	 */
	public void setRozvezPalet(int rozvezPalet) {
		rozvezPalet = 6 - rozvezPalet;
		this.rozvezPalet = rozvezPalet;
	}

	/** Vrátí poèet najetých kilometrù.
	 * @return kolikUjelKm množství ujetých kilometrù
	 */
	public int getKolikUjel() {
		return kolikUjelKm;
	}

	/** Nastaví poèet najetých kilometrù.
	 * @param kolikUjel množství ujetých kilometrù
	 */
	public void setKolikUjel(int kolikUjel) {
		this.kolikUjelKm += kolikUjel;
	}

	/** Vrátí èíslo nákladního auta.
	 * @return cisloNakladaku èíslo nákladního auta
	 */
	public int getCisloNakladaku() {
		return cisloNakladaku;
	}

	/** Vrátí seznam rozvážených objednávek.
	 * @return objednavkyCoVeze seznam doruèovaných objednávek
	 */
	public ArrayList<Integer> getObjednavkyCoVeze() {
		return objednavkyCoVeze;
	}

	/** Pøidá do seznamu doruèovaných objednávek další objednávku.
	 * @param cisloObjednavky pøidávaná objednávka
	 */
	public void setObjednavkyCoVeze(int cisloObjednavky) {
		this.objednavkyCoVeze.add(cisloObjednavky);
	}

	/** Textová reprezentace nákladního auta */
	@Override
	public String toString() {
		return "Nákladní auto èíslo:\t\t\t" + cisloNakladaku + "\nPoèet palet:\t\t\t\t" + rozvezPalet + "\nUjetá vzdálenost:\t\t\t" + kolikUjelKm + " Km" 
				+ "\nNáklady na dopravu:\t\t" + naklady + " Kè. \nDoruèované objednávky:\t\t" + objednavkyCoVeze.toString() + "\n";
	}
}
