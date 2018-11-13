package data;

import java.util.ArrayList;

import javafx.collections.ObservableList;

public class Model {

	/** Vytvori instanci modelu */
	public static final Model instance = new Model();
	/** Instance tridy {@code Dispetcher} */
	public Dispetcher disp;
	/** Celkovy pocet mest (vcetne firmy) */
	public int pocetMest;
	
	public int den;
	public int cenaPalety;
	public int prijatychObjednavek = 0;
	public int odmitnutychObjednavek = 0;
	public int rozvezenychPalet = 0;
	public int ujetychKm = 0;
	public int nakladyNaDopravu = 0;
	
	
	
	/** Seznam mest, ktere si objednavaji palety */
	public ArrayList<Mesto> mesta = new ArrayList<>();
	/** Casy pro ujeti dane vzdalenosti mezi dvema mesty */
	public int[][] casy;
	/** Vzdalenosti mezi dvema mesty */
	public int[][] vzdalenosti;
	/** Matice ve ktere jsou ulozeny nejkratsi cesty mezi jednotlivymi mesty */
	public int[][] nejkratsiCesty;
	
	public int[][] nejrychlejsiCesty;
	/** Seznam nezpracovanych objednavek */
	public ArrayList<Objednavka> nezpracovaneObjednavky;
	/** Seznam dorucovanych objednavek */
	public ArrayList<Objednavka> dorucovaneObjednavky;
	/** Vrati instanci modelu */
	public static Model getInstance() {
		return instance;
	}
}
