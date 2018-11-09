package data;

import java.util.TimeZone;

public class Objednavka {
	
	private final long CAS = 21600000;
	private int mesto;
	private int palet;
	private String casObjednavky;
	private boolean prijato;
	private int cisloObjednavky;
	private int casDoruceni;
	private int cisloNakladaku;

	public Objednavka(int mesto, int palet, String casObjednavky) {
		this.mesto = mesto;
		this.palet = palet;
		this.casObjednavky = casObjednavky;
	}

	public int getCisloObjednavky() {
		return cisloObjednavky;
	}

	public void setCisloObjednavky(int cisloObjednavky) {
		this.cisloObjednavky = cisloObjednavky;
	}

	public int getCasDoruceni() {
		return casDoruceni;
	}

	public void setCasDoruceni(int casDoruceni) {
		this.casDoruceni = casDoruceni;
	}
	
	public void setPrijato(boolean prijato) {
		this.prijato = prijato;
	}

	public int getMesto() {
		return mesto;
	}

	public int getPalet() {
		return palet;
	}

	public String getCasObjednavky() {
		return casObjednavky;
	}
	
	public int getCisloNakladaku() {
		return this.cisloNakladaku;
	}
	
	public void setCisloNakladaku(int cislo) {
		this.cisloNakladaku = cislo;
	}
	
	public boolean isPrijato() {
		return prijato;
	}

	/** Vrati retezec casu
	 * @param millis cas, ktery se ma prevest (v milisekundach)
	 * @return cas ve formatu hh:mm:ss
	 */
	public String setTime(long millis) {
		if (millis == 0) {
			setTime(CAS);
		}
		long sec = millis/1000;
	    long second = sec % 60;
	    int minute = (int)((sec / 60) % 60);
	    int hour = (int)(sec / 3600);
	    return String.format("%tT", millis-TimeZone.getDefault().getRawOffset());
		
	}
	
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

	public String strucnyPopis() {
		return "Objednávka èíslo " + cisloObjednavky + "\nMìsto: " + mesto + " Palet: " + palet + "\nÈas objednávky: " + casObjednavky;
	}

}
