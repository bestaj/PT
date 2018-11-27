package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.controller.MainController;

/**
 * Tøída {@code ZpracujObjednavky} se starí o zpracovávání pøíchozích objednávek
 * a vıpis jejich stavu.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class ZpracujObjednavky {
	/**Seznam objednávek pro zpracování*/
	private List<Objednavka> objednavky;
	/**Konstanta reprezentuje nekoneèno*/
	private final int INF = 9999;
	/**Èas pro vykládání jedné palety*/
	private final int CAS_V_JEDNEM_MESTE = 1800;
	/**Konec dne = 20:00*/
	private final int KONEC_DNE = 72000;
	/**Aktualní poèet palet*/
	private int pocetPalet;
	/**Kolik potøebujeme palet pro pravì zpracovávanou objednávku*/
	private int potrebujemePalet;
	/**Aktualní index zpracovávané objednávky*/
	private int aktualniIndex;
	/**Èíslo objednávky*/
	private int cisloObjednavky = 1;
	/**Aktuální èas*/
	public int cas;
	/**Celkové náklady na dopravu*/
	public int naklady;
	/***/
	private MainController mc;
	/**Seznam zpracovanıch objednávek*/
	Map<Integer, Objednavka> zpracovaneObjednavky;
	/**Seznam pouitıch nákladních aut*/
	Map<Integer, Nakladak> pouziteNakladaky;
	/**Èíslo nákladního auta*/
	private int cisloNakladaku;

	/**
	 * V konstruktoru se nastaví seznam objednavek pro zpracování, aktuální èas.
	 * @param cas 				aktuální èas
	 * @param objednavky		seznam objednávek pro zpracování
	 */
	public ZpracujObjednavky(int cas, List<Objednavka> objednavky) {
		this.cas = cas;
		this.objednavky = objednavky;
		this.zpracovaneObjednavky = new HashMap<>();
		this.pouziteNakladaky = new HashMap<>();
	}

	/**
	 * Nastaví instanci hlavního kontroléru
	 * @param mainController hlavní kontrolér
	 */
	public void setMainController(MainController mainController) {
		this.mc = mainController;
	}

	/**
	 * 	Nastaví se index mìsta do kterého pojede nákladní auto.  
	 * @param a 			index aktuálního mìsta ve kterém se nachází auto
	 * @param objednavky	seznam objednavek
	 * @return	b			index mìsta do kterého pojede auto			
	 */
	public int rozhodniKam(int a, List<Objednavka> objednavky) {
		int b = 0;
		int vzdalenost = INF;
		for (int i = 0; i < objednavky.size(); i++) {
			if (vzdalenost > Model.getInstance().nejkratsiCesty[a][objednavky.get(i).getMesto()]) {
				vzdalenost = Model.getInstance().nejkratsiCesty[a][objednavky.get(i).getMesto()];
				b = objednavky.get(i).getMesto();
				this.aktualniIndex = i;

			}
		}
		potrebujemePalet = objednavky.get(this.aktualniIndex).getPalet();
		return b;
	}

	/**
	 * Nastaví atributy objednávky na aktuální
	 * @param ob					instance aktulní zpracovávané objednávky
	 * @param cisloObjednavky		èíslo aktuálnì zpracovávané objednávky
	 * @param casDoruceni			pøedpokládanı èas doruèení objednávky
	 * @param cisloNakladaku		èislo nákladního auta, které vyøizuje danou objednávku
	 * @param jePrijata				true objednávka je pøijata, false je odmítnuta
	 */
	public void nastavObj(Objednavka ob, int cisloObjednavky, int casDoruceni, int cisloNakladaku, boolean jePrijata) {
		ob.setCisloObjednavky(cisloObjednavky);
		ob.setCasDoruceni(casDoruceni);
		ob.setCisloNakladaku(cisloNakladaku);
		ob.setPrijato(jePrijata);
	}

	/**
	 * Rozhodne, zda pøijmout objednávku nebo ne podle toho, jestli nákladní auto stihne doruèit objednávku do konce pracovního dne
	 * @param kolikCasuPotreba		kolik èasu potøebuje auto pro doruèení a vyloení objednávky
	 * @param kolikCasuZbyva		kolik èasu zbıvá do konce pracovního dne
	 * @return						true objednávka je pøijata, false je odmítnuta
	 */
	public boolean prijmout(int kolikCasuPotreba, int kolikCasuZbyva) {
		if (kolikCasuPotreba > kolikCasuZbyva) return false;
		return true;
	}

	/**
	 *  Metoda zjišuje, jestli na cestì, kterou pojede auto, není nìjaká objednávka ze seznamu
	 * @param mestaNaCeste		seznam mìst na cestì
	 * @param ob				seznam zbylıch objednávek
	 * @return					index mìsta, které je na cestì, pokud takové mìsto se nachází v seznamu zbylıch objednávek, -1 - pokud ne.
	 */
	public int dorucPoCeste(List<Integer> mestaNaCeste, List<Objednavka> ob) {
		int indexMesta = -1;
		for (int i = 1; i < mestaNaCeste.size() - 1; i++) {
			for (int j = 0; j < ob.size(); j++) {
				if (mestaNaCeste.get(i) == ob.get(j).getMesto()) {
					indexMesta = mestaNaCeste.get(i);
					return indexMesta;
				}
			}
		}
		return indexMesta;

	}
	/**
	 * Nastaví stav objednávky pro vıpis
	 * @param stav				øetìzec pro vıpis
	 * @param casUjedeAuto		èas co ujede jedno nákladní auto
	 * @return	stav			øetìzec pro vıpis	
	 */
	public String nastavStav(String stav, int casUjedeAuto) {
		String stav2 = stav;
		stav2 += "Objednávka èíslo " + cisloObjednavky + " je pøiijata.\n";
		stav2 += "Objednávku bude vyøizovat nákladní auto èíslo " + cisloNakladaku + ".\n";
		stav2 += "Pøedpokládanı èas doruèení je " + mc.setTime((casUjedeAuto + cas) * 1000) + ".\n\n";
		return stav2;
	}

	/**
	 * Nákladní auto se vrátí do domovského mìsta, které je reprezentované indexem 0
	 * @param n				instance nákladního auta
	 * @param vzdalenost	vzdálenost mezi mìstem, kde se nachází, a firmou
	 * @param kmNajednoAuto kolik ji auto ujelo Km
	 * @param naklad		kolik utratí za cestu z mìsta, kde se nachází, do firmy
	 * @param nakladNaAuto  kolik firma dosud utratila na cestu 
	 * @param odkud			mìsto, kde se právì nachází nákladní auto
	 * @return				true
	 */
	public boolean jedDomu(Nakladak n, int kmNajednoAuto, int nakladNaAuto, int odkud) {
		
		int naklad = (Model.getInstance().nejkratsiCesty[odkud][0]) * 25;
		int vzdalenost = Model.getInstance().nejkratsiCesty[odkud][0];
		int kmNaJednoAutoPom = kmNajednoAuto;
		int nakladNaAutoPom = nakladNaAuto;
		kmNaJednoAutoPom += vzdalenost;
		n.setKolikUjel(kmNaJednoAutoPom);
		nakladNaAutoPom += naklad;
		n.setNaklad(nakladNaAutoPom);
		this.naklady += naklad;
		n.setRozvezPalet(pocetPalet);
		pouziteNakladaky.put(cisloNakladaku, n);
		return true;
	}

	/**
	 * Zpracuje vdycky 6 objednávek. Jakoby reprezentuje jedno auto, které mùe rozvézt maximálnì 6 objednávek.
	 * V metodì se rozhoduje, jestli dané auto stíhá odvézt objednávku do konce pracovní doby, podle toho se nastaví stav objednávky.
	 * 
	 * 
	 * @param casCoZbyva  			èas co zbıvá do konce pracovního dne 	
	 * @return	stavObjednavky 		øetìzec pro vıpis- informace o stavu objednávky
	 */
	public String zpracujObJednoAuto(int casCoZbyva) {

		int casCoZbyvaPom = casCoZbyva;
		int cenaZaPaletu = 3000;
		List<Integer> mestaNaCeste = new ArrayList<>();
		Nakladak nakladak = new Nakladak(++cisloNakladaku);
		int casUjedeJednoAuto = 0;
		int pocatek = 0;
		pocetPalet = 6;
		int indexMesta = 0;
		int nakladNaJednoAuto = 0;
		int kmNaJednoAuto = 0;
		int naklad = 0;
		int vzdalenost = 0;
		boolean jdiDomu = false;
		String stavObjednavky = "";
		int poradiObjedn = 1;
		int kolikVydelame = 0;
		int kolikUtratime = 0;

		HashMap<Integer, Integer> kandidatiNaObjednavku = new HashMap<>();

		while (true) {
			if (jdiDomu) {
				break;
			}

			indexMesta = rozhodniKam(pocatek, objednavky);
			mestaNaCeste = Model.getInstance().disp.cesta(pocatek, indexMesta, 0);
			int kolikCasuPotreba = 0;
			for (int i = 0; i < mestaNaCeste.size() - 1; i++) {
				kolikCasuPotreba += (Model.getInstance().casy[mestaNaCeste.get(i)][i + 1]) / 1000;
			}
			kolikCasuPotreba += (CAS_V_JEDNEM_MESTE * potrebujemePalet);
			kolikVydelame = potrebujemePalet * cenaZaPaletu;
			kolikUtratime = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;

			if (!prijmout(kolikCasuPotreba, casCoZbyvaPom)) {
				if (poradiObjedn == 1) {
					stavObjednavky += "Objednávka èíslo " + cisloObjednavky + " je zamítnuta.\n\n";
					Model.getInstance().odmitnutychObjednavek++;
					objednavky.get(aktualniIndex).setCisloObjednavky(cisloObjednavky);
					objednavky.get(aktualniIndex).setPrijato(false);
					mc.pridejObjednavku(objednavky.get(aktualniIndex), true, false);
					zpracovaneObjednavky.put(cisloObjednavky, objednavky.get(aktualniIndex));
					objednavky.remove(aktualniIndex);
					cisloObjednavky++;
					if (objednavky.isEmpty()) {
						break;
					}

					jdiDomu = true;

				} else {
					jdiDomu = jedDomu(nakladak, kmNaJednoAuto, nakladNaJednoAuto, pocatek);
					break;
				}
			} else {
				if (pocetPalet < potrebujemePalet) {
					if (pocetPalet == 0) {
						jdiDomu = jedDomu(nakladak, kmNaJednoAuto, nakladNaJednoAuto, pocatek);
						break;
					}
					ArrayList<Objednavka> pomocne = new ArrayList<>();
					for (int k = 0; k < objednavky.size(); k++) {
						if (objednavky.get(k).getPalet() <= pocetPalet) {
							pomocne.add(objednavky.get(k));
							kandidatiNaObjednavku.put(pomocne.size() - 1, k);
						}
					}

					if (pomocne.isEmpty()) {
						jdiDomu = jedDomu(nakladak, kmNaJednoAuto, nakladNaJednoAuto, pocatek);

					} else {
						indexMesta = rozhodniKam(pocatek, pomocne);
						if ((dorucPoCeste(mestaNaCeste, objednavky)) > 0) {
							indexMesta = dorucPoCeste(mestaNaCeste, objednavky);
						}
						kolikVydelame = potrebujemePalet * cenaZaPaletu;
						kolikUtratime = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;

						// Pokud je to posledni mesto kam pojedeme
						if (((kolikUtratime + (Model.getInstance().nejkratsiCesty[indexMesta][0]) * 25)
								- kolikVydelame) < (Model.getInstance().nejkratsiCesty[pocatek][0]) * 25) {
							
							naklad = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;
							vzdalenost = Model.getInstance().nejkratsiCesty[pocatek][indexMesta];
							kmNaJednoAuto += vzdalenost;
							nakladNaJednoAuto += naklad;
							this.naklady += naklad;
							casCoZbyvaPom -= kolikCasuPotreba;
							casUjedeJednoAuto += kolikCasuPotreba;
							nakladak.setKolikUjelCasu(casUjedeJednoAuto + cas);
							pocatek = indexMesta;
							pocetPalet -= pomocne.get(this.aktualniIndex).getPalet();
							pomocne.remove(this.aktualniIndex);
							stavObjednavky = nastavStav(stavObjednavky, casUjedeJednoAuto);
							Model.getInstance().prijatychObjednavek++;
							nastavObj(objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)), cisloObjednavky,
									(casUjedeJednoAuto + cas), cisloNakladaku, true);
							nakladak.setObjednavkyCoVeze(cisloObjednavky);
							zpracovaneObjednavky.put(cisloObjednavky,
									objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)));

							mc.pridejObjednavku(objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)), true,
									true);
							Model.getInstance().dorucovaneObjednavky.add(objednavky.get(aktualniIndex));
							Model.getInstance().rozvezenychPalet += objednavky.get(aktualniIndex).getPalet();

							objednavky.remove((int) kandidatiNaObjednavku.get(aktualniIndex));
							poradiObjedn++;
							cisloObjednavky++;
							if (objednavky.isEmpty()) {
								jdiDomu = jedDomu(nakladak, kmNaJednoAuto, nakladNaJednoAuto, pocatek);
								break;
							}

						} else {
							jdiDomu = jedDomu(nakladak, kmNaJednoAuto, nakladNaJednoAuto, pocatek);
						}
					}

				} else {
					if ((dorucPoCeste(mestaNaCeste, objednavky)) > 0) {
						indexMesta = dorucPoCeste(mestaNaCeste, objednavky);
					}
					naklad = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;
					nakladNaJednoAuto += naklad;
					this.naklady += naklad;
					vzdalenost = Model.getInstance().nejkratsiCesty[pocatek][indexMesta];
					kmNaJednoAuto += vzdalenost;
					casUjedeJednoAuto += kolikCasuPotreba;
					nakladak.setKolikUjelCasu(casUjedeJednoAuto + cas);
					pocatek = indexMesta;
					casCoZbyvaPom -= kolikCasuPotreba;
					pocetPalet -= objednavky.get(this.aktualniIndex).getPalet();
					stavObjednavky = nastavStav(stavObjednavky, casUjedeJednoAuto);				
					Model.getInstance().prijatychObjednavek++;
					nastavObj(objednavky.get(aktualniIndex), cisloObjednavky, (casUjedeJednoAuto + cas), cisloNakladaku,
							true);

					mc.pridejObjednavku(objednavky.get(aktualniIndex), true, true);
					Model.getInstance().dorucovaneObjednavky.add(objednavky.get(aktualniIndex));
					Model.getInstance().rozvezenychPalet += objednavky.get(aktualniIndex).getPalet();

					nakladak.setObjednavkyCoVeze(cisloObjednavky);
					zpracovaneObjednavky.put(cisloObjednavky, objednavky.get(aktualniIndex));
					objednavky.remove(this.aktualniIndex);
					poradiObjedn++;
					cisloObjednavky++;
					if (objednavky.isEmpty()) {
						jdiDomu = jedDomu(nakladak, kmNaJednoAuto, nakladNaJednoAuto, pocatek);
						break;
					}

				}
			}

		}
		Model.getInstance().ujetychKm += nakladak.getKolikUjel();
		return stavObjednavky;
	}

	/**
	 * Nastaví øetìzec pro vıpis. Informace o tom, kde se v danou chvíli nachází objednávka.
	 * 
	 * 
	 * @param cisloObjednavky		èislo objednávky
	 * @param aktualniCas			aktuální èas
	 * @return						øetìzec s informacemi pro vıpis
	 */
	public String stavObjednavky(int cisloObjednavky, long aktualniCas) {
		String vypis = "---------------------------------------\n";
		if (zpracovaneObjednavky.containsKey(cisloObjednavky)) {
			vypis += zpracovaneObjednavky.get(cisloObjednavky).toString() + ". \n";
			if (aktualniCas < zpracovaneObjednavky.get(cisloObjednavky).getCasDoruceni()) {
				vypis += "Objednávka ještì není doruèena. (Probíhá doruèení)\n---------------------------------------\n";
			} else {
				vypis += "Objednávka ji byla doruèena.\n---------------------------------------\n";
			}
		} else {
			vypis += "Objednávka èíslo " + cisloObjednavky + " není v seznamu.\n---------------------------------------\n";
		}

		return vypis;

	}

	/**
	 * Nastaví øetìzec pro vıpis. Informace o tom, kde se v danou chvili nachází nákladní auto.
	 * @param aktualniCas		aktuální èas
	 * @param cisloNakladaku	èíslo nákladního auta
	 * @return					øetìzec s informacemi pro vıpis
	 */
	public String stavNakladaku(int aktualniCas, int cisloNakladaku) {
		String vypis = "\n---------------------------------------\n";

		vypis += pouziteNakladaky.get(cisloNakladaku).toString();
		if (pouziteNakladaky.get(cisloNakladaku).getKolikUjelCasu() > aktualniCas) {
			vypis += "V dané chvíli nákladní auto ještì rozváí.\n";
		} else {
			vypis += "Nákladní auto ji rozvezlo všechny palety.";
		}
		return vypis;

	}

	/**
	 * Nastaví øetìzec pro vıpis. Statistiky celého dne.
	 * @return		øetìzec s informacemi pro vıpis
	 */
	public String statistikySimulace() {
		String vypis = "\n---------------------------------------\n";
		vypis += "Pøijatıch objednávek: " + Model.getInstance().prijatychObjednavek + "\nOdmítnutıch objednávek: "
				+ Model.getInstance().odmitnutychObjednavek + "\nCelkem bylo pouito " + pouziteNakladaky.size()
				+ " nákladních aut.\nUjetá vzdálenost: " + Model.getInstance().ujetychKm + "Km\nNáklady na dopravu: "
				+ (Model.getInstance().ujetychKm * 25) + "Kè\nRozvezeno " + Model.getInstance().rozvezenychPalet
				+ " palet." + "\nCena za palety: "
				+ Model.getInstance().rozvezenychPalet * Model.getInstance().cenaPalety + "Kè\nZisk: "
				+ ((Model.getInstance().cenaPalety * Model.getInstance().rozvezenychPalet)
						- (Model.getInstance().ujetychKm * 25))
				+ "Kè";
		return vypis;
	}

	/**
	 * Nastaví aktuální èas
	 * @param cas		aktuální èas
	 */
	public void setCas(int cas) {
		this.cas = cas;
	}

	/**
	 * Metoda zpracovává objednávky dokola, dokud se nevyprázdní seznam nezpracovanıch objednávek.
	 */
	public void zpracujObjednavky() {

		while (!objednavky.isEmpty()) {
			int casCoZbyva = KONEC_DNE - cas;
			mc.vypisTA.appendText(zpracujObJednoAuto(casCoZbyva));
		}
	}
}
