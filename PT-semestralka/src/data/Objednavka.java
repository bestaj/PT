package data;

import java.util.TimeZone;

/**
 * T��da {@code Objednavka} reprezentuj�c� objedn�vku.
 *
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Objednavka {
	/** �as, kdy za��n� simulace. (8:00) */
	private final long CAS = 28800000;
	/** M�sto, kter� si objednalo palety */
	private final int MESTO;
	/** Po�et palet, kolik si dan� m�sto objednalo */
	private final int PALET;
	/** �as, kdy p�i�la objedn�vka */
	private final String CAS_OBJ;
	/** Zda je objedn�vka p�ijata nebo nen� */
	private boolean prijato;
	/** ��slo objedn�vky */
	private int cisloObjednavky;
	/** �as v kolik bude objedn�vka doru�ena */
	private int casDoruceni;
	/** ��slo n�kla��ku, kter� bude objedn�vku doru�ovat */
	private int cisloNakladaku;

	/**
	 * Vytvo�� novou objedn�vku pro dan� m�sto, s dan�m po�tem palet 
	 * a s �asem, kdy p�i�la objedn�vka.
	 * 
	 * @param mesto Mesto, kter� objedn�v�.
	 * @param palet Po�et palet, kter� si m�sto objednalo.
	 * @param casObjednavky �as, kdy p�i�la objedn�vka
	 */
	public Objednavka(int mesto, int palet, String casObjednavky) {
		this.MESTO = mesto;
		this.PALET = palet;
		this.CAS_OBJ = casObjednavky;
	}

	/** Vr�t� ��slo objedn�vky.
	 * @return ��slo objedn�vky
	 */
	public int getCisloObjednavky() {
		return cisloObjednavky;
	}

	/** Nastav� ��slo objedn�vky.
	 * @param cisloObjednavky ��slo p�i�azen� objedn�vce
	 */
	public void setCisloObjednavky(int cisloObjednavky) {
		this.cisloObjednavky = cisloObjednavky;
	}

	/** Vr�t� �as doru�en� objedn�vky.
	 * @return �as doru�en�
	 */
	public int getCasDoruceni() {
		return casDoruceni;
	}

	/** Nastav� �as doru�en� objedn�vky. 
	 * @param casDoruceni �as doru�en� objedn�vky
	 */
	public void setCasDoruceni(int casDoruceni) {
		this.casDoruceni = casDoruceni;
	}
	
	/** Vr�t�, zda je objedn�vka p�ijata nebo zam�tnuta.
	 * @return prijato true-objedn�vka je p�ijata, false-objedn�vka je zam�tnuta
	 */
	public boolean isPrijato() {
		return prijato;
	}
	
	/** P�ijme nebo zam�tne objedn�vku. 
	 * @param prijato true-objedn�vka je p�ijata, false-objedn�vka je zam�tnuta
	 */
	public void setPrijato(boolean prijato) {
		this.prijato = prijato;
	}

	/** Vr�t� ��slo m�sto.
	* @return mesto m�sto, kter� si objedn�valo
	*/
	public int getMesto() {
		return MESTO;
	}

	/** Vr�t� po�et palet. 
	 * @return palet po�et palet, kter� si dan� m�sto objednalo
	 */
	public int getPalet() {
		return PALET;
	}

	/** Vr�t� �as, kdy p�i�la objedn�vka.	
	 * @return casObjednavky �as, kdy p�i�la objedn�vka
	 */
	public String getCasObjednavky() {
		return CAS_OBJ;
	}
	
	/** Vr�t� ��slo n�kladn�ho auta, kter� doru�uje danou objedn�vku.
	 * @return cisloNakladaku ��slo n�kladn�ho auta
	 */
	public int getCisloNakladaku() {
		return cisloNakladaku;
	}
	
	/** P�i�ad� dan� objedn�vce n�kladn� auto.
	 * @param cislo ��slo n�kladn�ho auta
	 */
	public void setCisloNakladaku(int cislo) {
		this.cisloNakladaku = cislo;
	}
	
	/** Vr�t� �as ve form�tu hh:mm:ss
	 * @param millis �as, kter� se m� p�ev�st (v milisekund�ch)
	 * @return �as ve form�tu hh:mm:ss
	 */
	public String setTime(long millis) {
		if (millis == 0) {
			setTime(CAS);
		}
	    return String.format("%tT", millis-TimeZone.getDefault().getRawOffset());
	}
	
	/** Vr�t� textovou reprezentaci objedn�vky */
	@Override
	public String toString() {
		String vypis = "";
		vypis += "Objedn�vka �islo " + cisloObjednavky + " je";
		if (isPrijato()) {
			vypis += " p�ijata. �as doru�en�: " + setTime(casDoruceni*1000);
		} else {
			vypis += " odm�tnuta.";
		}
		return vypis;
	}

	/** Vr�t� stru�n� popis objedn�vky
	 * @return stru�n� popis objedn�vky
	 */
	public String strucnyPopis() {
		return "Objedn�vka ��slo " + cisloObjednavky + "\nM�sto: " + MESTO + " Palet: " + PALET + "\n�as objedn�vky: " + CAS_OBJ;
	}
}
