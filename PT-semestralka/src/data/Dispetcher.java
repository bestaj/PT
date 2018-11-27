package data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * T��da {@code Dispetcher}, kter� se star� o hled�n� nejkrat��ch
 * a nejrychlej��ch cest. D�le tak� vyhled�n� posloupnosti m�st 
 * na dan� cest� a zji�t�n� jej� doby trv�n�.
 * T��da tak� obsahuje metodu {@link generujCasyMeziMesty}, kter� se star� 
 * o n�hodn� p�i�azen� �asu k jednotliv�m cest�m.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Dispetcher {
	/** Minim�ln� koeficient pro generov�n� �asu cesty */
	private static final int MIN_RANGE_KOEFICIENT = 36;
	/** Maxim�ln� koeficient pro generov�n� �asu cesty */
	private static final int MAX_RANGE_KOEFICIENT = 72;
	public Random r = new Random();
	/** Matice nejkrat��ch cest */
	private int [][] nejkratsicesta;
	/** Matice nejrychlej��ch cest */
	private int [][] nejrychlejsicesta;
	/** Celkov� po�et m�st */
	private final int POCET_MEST;
	
	/**
	 * Vytvo�� instanci dispetchera
	 * 
	 * @param pocetMest celkov� po�et m�st
	 */
	public Dispetcher(int pocetMest) {
		nejkratsicesta = new int [pocetMest][pocetMest];
		nejrychlejsicesta = new int[pocetMest][pocetMest];
		this.POCET_MEST = pocetMest;
	}
	
	/**
	 * Vr�t� matici s nejkrat��mi nebo nejrychlej��mi cestami
	 * 
	 * @param m 			matice, pro kterou se maj� naj�t nejkrat�� cesty
	 * @param ident 		identifikator, podle ktereho rozlisime, do ktere matice
	 *  						mame zapsat cestu (posloupnost vrcholu). 0 - nejkratsi, 1 - nejrrychlejsi cesta.
	 * @return spocten� matice
	 */
	public int[][] floydAlg(int [][] m, int ident) {

		for(int mezi_uzel = 0; mezi_uzel < POCET_MEST; mezi_uzel++) {
			for(int a = 0; a < POCET_MEST; a++) {
				for(int b = 0; b < POCET_MEST; b++) {
					if(m[a][mezi_uzel] + m[mezi_uzel][b] < m[a][b]) {
						m[a][b] = m[a][mezi_uzel] + m[mezi_uzel][b];
						if(ident == 0) {
							nejkratsicesta[a][b] = mezi_uzel;
						}else if(ident == 1) {
							nejrychlejsicesta[a][b] = mezi_uzel;
						}
					}
				}
			}
		}
		return m;
	}
	
	/**
	 * Vyp�e posloupnost vrchol� - cestu mezi zadan�mi vrcholy 'a' a 'b'. 
	 * 
	 * @param a po��te�n� m�sto
	 * @param b koncov� m�sto
	 * @param index
	 * @return seznam m�st, p�es kter� vede cesta
	 */
	public List<Integer> cesta(int a, int b, int index) {
		List<Integer> al = new ArrayList<>();
		int novyB = b;
		int mezi_vrchol;
		int pom = a;
		al.add(b);
		while (true) {
			if (index == 0) {
				if(nejkratsicesta[a][novyB] == 0) {
					al.add(pom);
					return al; 
				}
				else {
					mezi_vrchol = nejkratsicesta[a][novyB];
					al.add(mezi_vrchol);
					novyB = mezi_vrchol;
					continue;
				}
			}else if (index == 1) {
				if(nejrychlejsicesta[a][novyB] == 0) {
					al.add(pom);
					return al; 
				}
				else {
					mezi_vrchol = nejrychlejsicesta[a][novyB];
					al.add(mezi_vrchol);
					novyB = mezi_vrchol;
					continue;
				}
			}
			
		}
	}
	
	/**
	 * Prepocitame realny cas (24 hod = 1 den) na pocitacovy cas(120m = 1 den).
	 * Tim padem pokud nakladak ma jet rychlosti v rozmezi 100 km/1 az 2 hod, potom  
	 * kdyz prevedeme do pocitacovyho casu, tak ujede v rozmezi 
	 * 1 km/36 (MIN_RANGE_KOEFICIENT)  az 72 (MAX_RANGE_KOEFICIENT) sec. 
	 * 
	 * Metoda nam vygeneruje matici s casy mezi jednotlivymi mesty
	 * Cas se spocte: vzdalenost[km] * (36 - 72s)/1km
	 * Vysledne casy jsou v sekundach
	 * 
	 * @param matrix 				matice s alokovanou velikosti
	 */
	public int[][] generujCasyMeziMesty(int[][] vzdalenosti) {
		int koef = 0;
		int[][] matrix = new int[vzdalenosti.length][vzdalenosti.length];
		for(int i = 0; i < vzdalenosti.length; i++) {
			for(int j = 0; j < vzdalenosti[i].length; j++) {
				koef = r.nextInt((MAX_RANGE_KOEFICIENT - MIN_RANGE_KOEFICIENT)+ 1) + MIN_RANGE_KOEFICIENT;
				matrix[i][j] = koef * vzdalenosti[i][j];
			}
		}
		return matrix;
	}
	
	/**
	 * Zjist� trvani cesty mezi m�sty 'a' a 'b' 
	 * @param a 					po�ate�n� bod
	 * @param b 					koncov� bod 
	 * @return result 				�as, kter� pot�ebujeme, aby se dostal z {@value a} do {@value b}
	 */
	public int trvaniCesty(int a, int b) {
		List<Integer> path = cesta(a, b, 1);
		int result = 0;
		for(int i = path.size() - 1; i > 0; i--) {
			int j = i;
			--j;
			result += Model.getInstance().casy[path.get(i)][path.get(j)];
		}
		return result;
	}

}
