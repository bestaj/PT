package data;

import java.util.ArrayList;

/**
 * T��da {@code Model} p�edstavuje model aplikace,
 * ve kter�m jsou uchov�v�ny v�echny d�le�it� data.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Model {

	/** Vytvo�� instanci modelu. */
	public static final Model INSTANCE = new Model();
	/** Instance t��dy {@code Dispetcher} */
	public Dispetcher disp;
	/** Celkov� po�et m�st (v�etn� firmy) */
	public int pocetMest;
	/** Den simulace */
	public int den;
	/** Cena jedn� palety pro dan� den */
	public int cenaPalety;
	/** Celkov� po�et p�ijat�ch objedn�vek v jeden den */
	public int prijatychObjednavek = 0;
	/** Celkov� po�et odm�tnut�ch objedn�vek v jeden den */
	public int odmitnutychObjednavek = 0;
	/** Celkov� po�et rozvezen�ch palet v jeden den */
	public int rozvezenychPalet;
	/** Celkov� po�et ujet�ch kilometr� v jeden den */
	public int ujetychKm = 0;
	/** Seznam m�st, kter� si objedn�vaj� palety */
	public ArrayList<Mesto> mesta = new ArrayList<>();
	/** �asy pro ujet� dan� vzd�lenosti mezi dv�ma m�sty */
	public int[][] casy;
	/** Vzd�lenosti mezi dv�ma mesty */
	public int[][] vzdalenosti;
	/** Matice, ve kter� jsou ulo�eny nejkrat�� cesty mezi jednotliv�mi m�sty. */
	public int[][] nejkratsiCesty;
	/** Matice, ve kter� jsou ulo�eny nejrychlej�� cesty mezi jednotliv�mi m�sty. */
	public int[][] nejrychlejsiCesty;
	/** Seznam nezpracovan�ch objedn�vek */
	public ArrayList<Objednavka> nezpracovaneObjednavky;
	/** Seznam doru�ovan�ch objedn�vek */
	public ArrayList<Objednavka> dorucovaneObjednavky;
	/** Vr�t� instanci modelu */
	public static Model getInstance() {
		return INSTANCE;
	}
}
