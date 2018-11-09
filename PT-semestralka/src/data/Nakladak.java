package data;

import java.util.ArrayList;
import java.util.HashMap;

public class Nakladak {

	private int rozvezPalet;
	private int kolikUjelKm;
	private int kolikUjelCasu;
	private int cisloNakladaku;
	private int naklady;

	private ArrayList<Integer> objednavkyCoVeze;

	public Nakladak(int cisloNakladaku) {
		this.cisloNakladaku = cisloNakladaku;
		objednavkyCoVeze = new ArrayList<>();
	}

	public int getKolikUjelCasu() {
		return kolikUjelCasu;
	}

	public void setKolikUjelCasu(int kolikUjelCasu) {
		this.kolikUjelCasu = kolikUjelCasu;
	}
//	public void setCisloNakladaku(int cisloNakladaku) {
//		this.cisloNakladaku = cisloNakladaku;
//	}

	public int getNaklad() {
		return naklady;
	}

	public void setNaklad(int naklady) {
		this.naklady = naklady;
	}

	public int getRozvezPalet() {
		return rozvezPalet;
	}

	public void setRozvezPalet(int rozvezPalet) {

		rozvezPalet = 6 - rozvezPalet;
		this.rozvezPalet = rozvezPalet;
	}

	public int getKolikUjel() {
		return kolikUjelKm;
	}

	public void setKolikUjel(int kolikUjel) {
		this.kolikUjelKm += kolikUjel;
	}

	public int getCisloNakladaku() {
		return cisloNakladaku;
	}

	public ArrayList<Integer> getObjednavkyCoVeze() {
		return objednavkyCoVeze;
	}

	public void setObjednavkyCoVeze(int cisloObjednavky) {
		this.objednavkyCoVeze.add(cisloObjednavky);
	}

	@Override
	public String toString() {
		return "Nákladní auto èíslo:\t\t" + cisloNakladaku + "\nDosud rozvezeno palet:\t" + rozvezPalet + "\nCelkem ujel:\t\t\t" + kolikUjelKm + " Km" 
				+ "\nNáklady na dopravu:\t" + naklady + " Kè. \nRozvezené objednávky:\t" + objednavkyCoVeze.toString() + "\n";
	}

}
