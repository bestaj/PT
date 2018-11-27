package data;

import java.util.ArrayList;
import java.util.List;

/**
 * T��da {@code Nakladak} reprezentuj�c� n�kladn� auto.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Nakladak {
	/** Po�et palet kolik veze dan� n�kladn� auto */
	private int rozvezPalet;
	/** Celkov� ujet� vzd�lenost n�kladn�ho auta */
	private int kolikUjelKm;
	/** Celkov� �as str�ven� na cest�ch */
	private int kolikUjelCasu;
	/** ��slo n�kladn�ho auta */
	private final int CISLO_NAKLADAKU;
	/** N�klady na dopravu dan�ho n�kladn�ho auta */
	private int naklady;
	/** Seznam objedn�vek, kter� dan� n�kladn�ho auto rozv�� */
	private List<Integer> objednavkyCoVeze;

	/** Vytvo�� nov� n�kladn� auto s p�id�len�m ��slem.
	 * 
	 * @param cisloNakladaku ozna�en� n�kladn�ho auta
	 */
	public Nakladak(int cisloNakladaku) {
		this.CISLO_NAKLADAKU = cisloNakladaku;
		objednavkyCoVeze = new ArrayList<>();
	}

	/** Vr�t� �as, jak dlouho n�kladn� auto rozv��.
	 * @return kolikUjelCasu �as str�ven� na cest�ch
	 */
	public int getKolikUjelCasu() {
		return kolikUjelCasu;
	}

	/** Nastav� �as, jak dlouho bude trvat doru�en� v�ech doru�ovan�ch objedn�vek
	 * s vr�cen�m se zp�t do domovsk�ho m�sta.
	 * @param kolikUjelCasu trv�n� doru�ov�n� objedn�vek
	 */
	public void setKolikUjelCasu(int kolikUjelCasu) {
		this.kolikUjelCasu = kolikUjelCasu;
	}
	
//	public void setCisloNakladaku(int cisloNakladaku) {
//		this.cisloNakladaku = cisloNakladaku;
//	}

	/** Vr�t� n�klady na dan� n�kladn� auto.
	 * @return naklady n�klady na dopravu dan�ho n�kladn�ho auta
	 */
	public int getNaklad() {
		return naklady;
	}

	/** Nastav� n�klady pro dan� n�kladn� auto.
	 * @param naklady n�klady na dopravu dan�ho n�kladn�ho auta
	 */
	public void setNaklad(int naklady) {
		this.naklady = naklady;
	}

	/** Vr�t� po�et doru�ovan�ch palet .
	 * @return rozvezPalet po�et rozv�en�ch palet
	 */
	public int getRozvezPalet() {
		return rozvezPalet;
	}

	/** Nastav� po�et rozv�en�ch palet.
	 * @param rozvezPalet po�et rozv�en�ch palet
	 */
	public void setRozvezPalet(int rozvezPalet) {
		int pocetPalet = 6 - rozvezPalet;
		this.rozvezPalet = pocetPalet;
	}

	/** Vr�t� po�et najet�ch kilometr�.
	 * @return kolikUjelKm mno�stv� ujet�ch kilometr�
	 */
	public int getKolikUjel() {
		return kolikUjelKm;
	}

	/** Nastav� po�et najet�ch kilometr�.
	 * @param kolikUjel mno�stv� ujet�ch kilometr�
	 */
	public void setKolikUjel(int kolikUjel) {
		this.kolikUjelKm = kolikUjel;
	}

	/** Vr�t� ��slo n�kladn�ho auta.
	 * @return cisloNakladaku ��slo n�kladn�ho auta
	 */
	public int getCisloNakladaku() {
		return CISLO_NAKLADAKU;
	}

	/** Vr�t� seznam rozv�en�ch objedn�vek.
	 * @return objednavkyCoVeze seznam doru�ovan�ch objedn�vek
	 */
	public List<Integer> getObjednavkyCoVeze() {
		return objednavkyCoVeze;
	}

	/** P�id� do seznamu doru�ovan�ch objedn�vek dal�� objedn�vku.
	 * @param cisloObjednavky p�id�van� objedn�vka
	 */
	public void setObjednavkyCoVeze(int cisloObjednavky) {
		this.objednavkyCoVeze.add(cisloObjednavky);
	}

	/** Textov� reprezentace n�kladn�ho auta */
	@Override
	public String toString() {
		return "N�kladn� auto ��slo:\t\t\t" + CISLO_NAKLADAKU + "\nPo�et palet:\t\t\t\t" + rozvezPalet + "\nUjet� vzd�lenost:\t\t\t" + kolikUjelKm + " Km" 
				+ "\nN�klady na dopravu:\t\t" + naklady + " K�. \nDoru�ovan� objedn�vky:\t\t" + objednavkyCoVeze.toString() + "\n";
	}
}
