package data;

import java.util.ArrayList;

/**
 * Tøída {@code Model} pøedstavuje model aplikace,
 * ve kterém jsou uchovávány všechny dùležitá data.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class Model {

	/** Vytvoøí instanci modelu. */
	public static final Model INSTANCE = new Model();
	/** Instance tøídy {@code Dispetcher} */
	public Dispetcher disp;
	/** Celkový poèet mìst (vèetnì firmy) */
	public int pocetMest;
	/** Den simulace */
	public int den;
	/** Cena jedné palety pro daný den */
	public int cenaPalety;
	/** Celkový poèet pøijatých objednávek v jeden den */
	public int prijatychObjednavek = 0;
	/** Celkový poèet odmítnutých objednávek v jeden den */
	public int odmitnutychObjednavek = 0;
	/** Celkový poèet rozvezených palet v jeden den */
	public int rozvezenychPalet;
	/** Celkový poèet ujetých kilometrù v jeden den */
	public int ujetychKm = 0;
	/** Seznam mìst, které si objednávají palety */
	public ArrayList<Mesto> mesta = new ArrayList<>();
	/** Èasy pro ujetí dané vzdálenosti mezi dvìma mìsty */
	public int[][] casy;
	/** Vzdálenosti mezi dvìma mesty */
	public int[][] vzdalenosti;
	/** Matice, ve které jsou uloženy nejkratší cesty mezi jednotlivými mìsty. */
	public int[][] nejkratsiCesty;
	/** Matice, ve které jsou uloženy nejrychlejší cesty mezi jednotlivými mìsty. */
	public int[][] nejrychlejsiCesty;
	/** Seznam nezpracovaných objednávek */
	public ArrayList<Objednavka> nezpracovaneObjednavky;
	/** Seznam doruèovaných objednávek */
	public ArrayList<Objednavka> dorucovaneObjednavky;
	/** Vrátí instanci modelu */
	public static Model getInstance() {
		return INSTANCE;
	}
}
