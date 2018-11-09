package vstupnidata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Trida {@code GenData} vygeneruje nahodny neorientovany ohodnoceny graf 
 * 
 * @author Jiri Besta
 * @version 2.00
 */
public class GenData {

	/** Maximalni pocet mest */
	private static final int MAX_POCET_MEST = 2000;
	/** Minimalni pocet mest */
	private static final int MIN_POCET_MEST = 500;
	/** Nazev vystupniho souboru, do ktereho se zapisi mesta a cesty mezi nimi */
	private static final String VYSTUP = "src/vstupnidata/VstupniData.txt";
	  
	// Pocet vrcholu grafu
	public static int pocetMest; 
	// Pocet hran v grafu
	public static int pocetHran;
	private static Random rng = new Random();;  
	  
	// Maximalni vzdalenost mezi mesty (ohodnoceni hrany)
	public static final int MAX_VZDALENOST = 150;
	// Minimalni vzdalenost mezi mesty (ohodnoceni hrany)
	public static final int MIN_VZDALENOST = 20;
	
	// Pripavi seznam paru 
	public static ArrayList<Pair> seznam = new ArrayList<Pair>();
	
	private static int palet1;
	private static int palet2;
	private static int palet3;
	private static int palet4;
	private static int palet5;
	private static int palet6;
	
	  
	/**
	 * Vstupni bod programu 
	 * 
	 * @param args parametry prikazove radky,
	 * parametrem muze byt pocet sidel, neboli pocet vrcholu grafu
	 * pokud neni parametr zadan, pocet mest se nastavi na defaultni 
	 * hodnotu 2000
	 */
	public static void main(String[] args) {
		
		// Pokud neni zadan parametr poctu mest, nastavi se na 2000
		if (args.length != 1) 
			pocetMest = MAX_POCET_MEST;
		else {
			int parametr = Integer.parseInt(args[0]);
			if (parametr > 2000) {
				pocetMest = MAX_POCET_MEST;
			}
			else if (parametr < 500) {
				pocetMest = MIN_POCET_MEST;
			}
			else {
				pocetMest = parametr;
			}
		}
		inicializace();
		do {
		generovaniDat();
		} while (!spravnaData());
		System.out.println("Data byla uspesne vygenerovana.");
	}
	
	/** 
	 * Inicializace poctu hran
	 * Inicializace poctu mest, ktere maji odebirat dany pocet palet
	 * Procentualni rozdeleni:
	 * 		1 paleta -> 25%
	 * 		2 palety -> 25%
	 * 		3 palety -> 20%
	 * 		4 palety -> 15%
	 * 		5 palet  -> 10%
	 * 		6 palet  -> 5%
	 */
	public static void inicializace() {
		// Maximalni hustota cest grafu je n*(n-1)/2
		int maxHran = pocetMest * (pocetMest - 1) / 2;
		// Napr. pro 500 mest je prumerne 8 cest na 1 mesto
		if (pocetMest < 1000)
			pocetHran = maxHran / 30;
		// Napr. pro 1000 mest je prumerne 9 cest na 1 mesto
		else if (pocetMest >= 1000 && pocetMest <=1500)
			pocetHran = maxHran / 50;
		// Napr. pro 1501 mest je prumerne  10 cest na 1 mesto
		else 
			pocetHran = maxHran / 70;
				
		palet1 = (int)Math.ceil(0.25 * pocetMest);
		palet2 = (int)Math.ceil(0.25 * pocetMest);
		palet3 = (int)Math.ceil(0.20 * pocetMest);
		palet4 = (int)Math.ceil(0.15 * pocetMest);
		palet5 = (int)Math.ceil(0.10 * pocetMest);
		palet6 = (int)Math.ceil(0.05 * pocetMest);
	}
	
	public static void generovaniDat() {
		Pair cesta; 
        Pair opacnaCesta; 
        boolean exist;
       
		// Vytvori cesty mezi nahodnymi mesty
		for (int i = 1; i <= pocetHran; i++) {
			int a = rng.nextInt(pocetMest + 1); 
            int b = rng.nextInt(pocetMest + 1);
            cesta = new Pair(a, b); 
            opacnaCesta = new Pair(b, a); 
            
            /* Otestuje zda nova cesta jiz existuje
            * pokud ano, vygeneruje novou cestu 
            * a znova ji otestuje
            * pokud ne, ulozi ji do sady cest
            */
            if (!seznam.isEmpty()) {
            	do {
            		exist = false;
            		for (Iterator<Pair> it = seznam.iterator(); it.hasNext();) {
            			Pair next = it.next();
            			if (next.equals(cesta) || next.equals(opacnaCesta)) {
            				a = rng.nextInt(pocetMest + 1); 
	    		            b = rng.nextInt(pocetMest + 1);
	    		            cesta = new Pair(a, b); 
	    		            opacnaCesta = new Pair(b, a); 
            				exist = true;
            			}
            		}
            	} while (exist);
            }
            seznam.add(cesta);   
        }
		
	  
		/* Zapise do souboru pocet mest
		 * dale ke kazdemu mestu vygenerujeme maximalni pocet palet
		 * kolik jich muze dane mesto odebirat a zapiseme do souboru
		 * ke kazde ceste vygenerujeme nahodnou vzdalenost a zapiseme do souboru
		 */
		try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(VYSTUP))))) {
			writer.println(pocetMest);
			boolean jiny;
			int pocet;
			
			for (int i = 1; i <= pocetMest; i++) {
				do {
					jiny = false;
					pocet = 1 + rng.nextInt(6);
					switch (pocet) {
					case '1':
						if (palet1 == 0) {
							jiny = true;
							break;
						}
						else break;
					case '2':
						if (palet2 == 0) {
							jiny = true;
							break;
						}
						else break;
					case '3':
						if (palet3 == 0) {
							jiny = true;
							break;
						}
						else break;
					case '4':
						if (palet4 == 0) {
							jiny = true;
							break;
						}
						else break;
					case '5':
						if (palet5 == 0) {
							jiny = true;
							break;
						}
						else break;
					case '6':
						if (palet6 == 0) {
							jiny = true;
							break;
						}
						else break;
					}
				
				} while (jiny);
				writer.println(i + " " + pocet);
			}
			
			for (Iterator<Pair> it = seznam.iterator(); it.hasNext();) {
				int vzdalenost = MIN_VZDALENOST + rng.nextInt(MAX_VZDALENOST - MIN_VZDALENOST);
				writer.println(it.next().toString() + " " + vzdalenost);
				
			}
		} catch (Exception e) {
			System.out.println("Chyba pri zapisu do souboru.");
			e.printStackTrace();
		}
        seznam.clear();   
	}	
	
	public static boolean spravnaData() {
		ArrayList<Integer> list1 = new ArrayList<>();
		ArrayList<Integer> list2 = new ArrayList<>();
		boolean[][] pole = new boolean[2001][2001];
		for (int i = 0; i < pole.length; i++) {
				for (int j = 0; j < pole.length; j++) {
					pole[i][j] = false;
				}
		}
		try(Scanner sc = new Scanner(new File(VYSTUP))) {
			int pocet = sc.nextInt();
			for (int i = 0; i < pocet; i++) {
				String mesta = sc.nextLine();
				String[] prvky = mesta.split(" ");
			}
			while(sc.hasNextLine()) {
				String radka = sc.nextLine();
				String[] prvky = radka.split(" ");
				if (prvky[0] == prvky[1]) {
					return false;
				}
				list1.add(Integer.parseInt(prvky[0]));
				list2.add(Integer.parseInt(prvky[1]));
			}
		} catch(IOException e) {
			System.out.println("Chyba");
		}
		
		for (int i = 0; i < list1.size(); i++) {
			int m = list1.get(i);
			int n = list2.get(i);
			if (pole[m][n] == true) {
				System.out.println("Duplicate indicated " + m + " " + n );
				return false;
			}
			else {
				pole[m][n] = true;
			}
		}
		
		Collections.sort(list1);
			
		int count = 0;
		int pom = 0;
		for (Iterator<Integer> iterator = list1.iterator();iterator.hasNext();) {
			if (iterator.next() == pom) {
				count++;
			}
			else {
			//	System.out.println(pom + " " + count);
				if (count == 0) {
					System.out.println("Z mesta " + pom + " nevede zadna cesta.");
					return false;
				}
				count = 0;
				pom++;
			}
		}
		return true;
	}
}
