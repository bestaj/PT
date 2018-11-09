package data;
import java.util.ArrayList;
import java.util.Random;

public class Dispetcher {
	
	private static final int MIN_RANGE_KOEFICIENT = 36;
	private static final int MAX_RANGE_KOEFICIENT =72;
	public Random r = new Random();
	private int [][] nejkratsicesta;
	private int [][] nejrychlejsicesta;
	private int pocetMest;
	
	public Dispetcher(int pocetMest) {
		nejkratsicesta = new int [pocetMest][pocetMest];
		nejrychlejsicesta = new int[pocetMest][pocetMest];
		this.pocetMest = pocetMest;
	}
/**
 * 
 * @param m
 * @param ident 				identifikator, podle ktereho rozlisime, do ktere matice
 * 								 mame zapsat cestu (posloupnost vrcholu). 0 - nejkratsi, 1 - nejrrychlejsi cesta.
 * @return
 */
	// Vrati matici s nejkratsimi cestami
	public int[][] floydAlg(int [][] m, int ident) {
	//	int[][] m2 = new int[m.length][m.length];
		
		for(int mezi_uzel = 0; mezi_uzel < pocetMest; mezi_uzel++) {
			for(int a = 0; a < pocetMest; a++) {
				for(int b = 0; b < pocetMest; b++) {
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
	
	// Vypise posloupnost vrcholu - cestu mezi zadanym vrcholem 'a' a 'b'. 
	public ArrayList<Integer> cesta(int a, int b, int index) {
		ArrayList<Integer> al = new ArrayList<>();
		//String result = "";
		int mezi_vrchol;
		int pom = a;
		al.add(b);
		while (true) {
			if (index == 0) {
				if(nejkratsicesta[a][b] == 0) {
					al.add(pom);
					return al; 
				}
				else {
					mezi_vrchol = nejkratsicesta[a][b];
					al.add(mezi_vrchol);
					//result = mezi_vrchol + " "+ result;
					b = mezi_vrchol;
					continue;
				}
			}else if (index == 1) {
				if(nejrychlejsicesta[a][b] == 0) {
					al.add(pom);
					return al; 
				}
				else {
					mezi_vrchol = nejrychlejsicesta[a][b];
					al.add(mezi_vrchol);
					//result = mezi_vrchol + " "+ result;
					b = mezi_vrchol;
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
	 * @param a 					pocatecni bod
	 * @param b 					koncovy bod 
	 * @return result 				cas, ktery potrebujeme aby se dostat z {@value a} do {@value b}
	 */
	public int trvaniCesty(int a, int b) {
		ArrayList<Integer> path = cesta(a, b, 1);
		int result = 0;
		for(int i = path.size() - 1; i > 0; i--) {
			int j = i;
			--j;
			result += Model.getInstance().casy[path.get(i)][path.get(j)];
		}
		return result;
	}

}
