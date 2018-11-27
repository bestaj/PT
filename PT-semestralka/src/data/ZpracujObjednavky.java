package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.controller.MainController;

/**
 * T��da {@code ZpracujObjednavky} se star� o zpracov�v�n� p��choz�ch objedn�vek
 * a v�pis jejich stavu.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class ZpracujObjednavky {
	/**Seznam objedn�vek pro zpracov�n�*/
	private List<Objednavka> objednavky;
	/**Konstanta reprezentuje nekone�no*/
	private final int INF = 9999;
	/**�as pro vykl�d�n� jedn� palety*/
	private final int CAS_V_JEDNEM_MESTE = 1800;
	/**Konec dne = 20:00*/
	private final int KONEC_DNE = 72000;
	/**Aktualn� po�et palet*/
	private int pocetPalet;
	/**Kolik pot�ebujeme palet pro prav� zpracov�vanou objedn�vku*/
	private int potrebujemePalet;
	/**Aktualn� index zpracov�van� objedn�vky*/
	private int aktualniIndex;
	/**��slo objedn�vky*/
	private int cisloObjednavky = 1;
	/**Aktu�ln� �as*/
	public int cas;
	/**Celkov� n�klady na dopravu*/
	public int naklady;
	/***/
	private MainController mc;
	/**Seznam zpracovan�ch objedn�vek*/
	Map<Integer, Objednavka> zpracovaneObjednavky;
	/**Seznam pou�it�ch n�kladn�ch aut*/
	Map<Integer, Nakladak> pouziteNakladaky;
	/**��slo n�kladn�ho auta*/
	private int cisloNakladaku;

	/**
	 * V konstruktoru se nastav� seznam objednavek pro zpracov�n�, aktu�ln� �as.
	 * @param cas 				aktu�ln� �as
	 * @param objednavky		seznam objedn�vek pro zpracov�n�
	 */
	public ZpracujObjednavky(int cas, List<Objednavka> objednavky) {
		this.cas = cas;
		this.objednavky = objednavky;
		this.zpracovaneObjednavky = new HashMap<>();
		this.pouziteNakladaky = new HashMap<>();
	}

	/**
	 * Nastav� instanci hlavn�ho kontrol�ru
	 * @param mainController hlavn� kontrol�r
	 */
	public void setMainController(MainController mainController) {
		this.mc = mainController;
	}

	/**
	 * 	Nastav� se index m�sta do kter�ho pojede n�kladn� auto.  
	 * @param a 			index aktu�ln�ho m�sta ve kter�m se nach�z� auto
	 * @param objednavky	seznam objednavek
	 * @return	b			index m�sta do kter�ho pojede auto			
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
	 * Nastav� atributy objedn�vky na aktu�ln�
	 * @param ob					instance aktuln� zpracov�van� objedn�vky
	 * @param cisloObjednavky		��slo aktu�ln� zpracov�van� objedn�vky
	 * @param casDoruceni			p�edpokl�dan� �as doru�en� objedn�vky
	 * @param cisloNakladaku		�islo n�kladn�ho auta, kter� vy�izuje danou objedn�vku
	 * @param jePrijata				true objedn�vka je p�ijata, false je odm�tnuta
	 */
	public void nastavObj(Objednavka ob, int cisloObjednavky, int casDoruceni, int cisloNakladaku, boolean jePrijata) {
		ob.setCisloObjednavky(cisloObjednavky);
		ob.setCasDoruceni(casDoruceni);
		ob.setCisloNakladaku(cisloNakladaku);
		ob.setPrijato(jePrijata);
	}

	/**
	 * Rozhodne, zda p�ijmout objedn�vku nebo ne podle toho, jestli n�kladn� auto stihne doru�it objedn�vku do konce pracovn�ho dne
	 * @param kolikCasuPotreba		kolik �asu pot�ebuje auto pro doru�en� a vylo�en� objedn�vky
	 * @param kolikCasuZbyva		kolik �asu zb�v� do konce pracovn�ho dne
	 * @return						true objedn�vka je p�ijata, false je odm�tnuta
	 */
	public boolean prijmout(int kolikCasuPotreba, int kolikCasuZbyva) {
		if (kolikCasuPotreba > kolikCasuZbyva) return false;
		return true;
	}

	/**
	 *  Metoda zji��uje, jestli na cest�, kterou pojede auto, nen� n�jak� objedn�vka ze seznamu
	 * @param mestaNaCeste		seznam m�st na cest�
	 * @param ob				seznam zbyl�ch objedn�vek
	 * @return					index m�sta, kter� je na cest�, pokud takov� m�sto se nach�z� v seznamu zbyl�ch objedn�vek, -1 - pokud ne.
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
	 * Nastav� stav objedn�vky pro v�pis
	 * @param stav				�et�zec pro v�pis
	 * @param casUjedeAuto		�as co ujede jedno n�kladn� auto
	 * @return	stav			�et�zec pro v�pis	
	 */
	public String nastavStav(String stav, int casUjedeAuto) {
		String stav2 = stav;
		stav2 += "Objedn�vka ��slo " + cisloObjednavky + " je p�iijata.\n";
		stav2 += "Objedn�vku bude vy�izovat n�kladn� auto ��slo " + cisloNakladaku + ".\n";
		stav2 += "P�edpokl�dan� �as doru�en� je " + mc.setTime((casUjedeAuto + cas) * 1000) + ".\n\n";
		return stav2;
	}

	/**
	 * N�kladn� auto se vr�t� do domovsk�ho m�sta, kter� je reprezentovan� indexem 0
	 * @param n				instance n�kladn�ho auta
	 * @param vzdalenost	vzd�lenost mezi m�stem, kde se nach�z�, a firmou
	 * @param kmNajednoAuto kolik ji� auto ujelo Km
	 * @param naklad		kolik utrat� za cestu z m�sta, kde se nach�z�, do firmy
	 * @param nakladNaAuto  kolik firma dosud utratila na cestu 
	 * @param odkud			m�sto, kde se pr�v� nach�z� n�kladn� auto
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
	 * Zpracuje v�dycky 6 objedn�vek. Jakoby reprezentuje jedno auto, kter� m��e rozv�zt maxim�ln� 6 objedn�vek.
	 * V metod� se rozhoduje, jestli dan� auto st�h� odv�zt objedn�vku do konce pracovn� doby, podle toho se nastav� stav objedn�vky.
	 * 
	 * 
	 * @param casCoZbyva  			�as co zb�v� do konce pracovn�ho dne 	
	 * @return	stavObjednavky 		�et�zec pro v�pis- informace o stavu objedn�vky
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
					stavObjednavky += "Objedn�vka ��slo " + cisloObjednavky + " je zam�tnuta.\n\n";
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
	 * Nastav� �et�zec pro v�pis. Informace o tom, kde se v danou chv�li nach�z� objedn�vka.
	 * 
	 * 
	 * @param cisloObjednavky		�islo objedn�vky
	 * @param aktualniCas			aktu�ln� �as
	 * @return						�et�zec s informacemi pro v�pis
	 */
	public String stavObjednavky(int cisloObjednavky, long aktualniCas) {
		String vypis = "---------------------------------------\n";
		if (zpracovaneObjednavky.containsKey(cisloObjednavky)) {
			vypis += zpracovaneObjednavky.get(cisloObjednavky).toString() + ". \n";
			if (aktualniCas < zpracovaneObjednavky.get(cisloObjednavky).getCasDoruceni()) {
				vypis += "Objedn�vka je�t� nen� doru�ena. (Prob�h� doru�en�)\n---------------------------------------\n";
			} else {
				vypis += "Objedn�vka ji� byla doru�ena.\n---------------------------------------\n";
			}
		} else {
			vypis += "Objedn�vka ��slo " + cisloObjednavky + " nen� v seznamu.\n---------------------------------------\n";
		}

		return vypis;

	}

	/**
	 * Nastav� �et�zec pro v�pis. Informace o tom, kde se v danou chvili nach�z� n�kladn� auto.
	 * @param aktualniCas		aktu�ln� �as
	 * @param cisloNakladaku	��slo n�kladn�ho auta
	 * @return					�et�zec s informacemi pro v�pis
	 */
	public String stavNakladaku(int aktualniCas, int cisloNakladaku) {
		String vypis = "\n---------------------------------------\n";

		vypis += pouziteNakladaky.get(cisloNakladaku).toString();
		if (pouziteNakladaky.get(cisloNakladaku).getKolikUjelCasu() > aktualniCas) {
			vypis += "V dan� chv�li n�kladn� auto je�t� rozv��.\n";
		} else {
			vypis += "N�kladn� auto ji� rozvezlo v�echny palety.";
		}
		return vypis;

	}

	/**
	 * Nastav� �et�zec pro v�pis. Statistiky cel�ho dne.
	 * @return		�et�zec s informacemi pro v�pis
	 */
	public String statistikySimulace() {
		String vypis = "\n---------------------------------------\n";
		vypis += "P�ijat�ch objedn�vek: " + Model.getInstance().prijatychObjednavek + "\nOdm�tnut�ch objedn�vek: "
				+ Model.getInstance().odmitnutychObjednavek + "\nCelkem bylo pou�ito " + pouziteNakladaky.size()
				+ " n�kladn�ch aut.\nUjet� vzd�lenost: " + Model.getInstance().ujetychKm + "Km\nN�klady na dopravu: "
				+ (Model.getInstance().ujetychKm * 25) + "K�\nRozvezeno " + Model.getInstance().rozvezenychPalet
				+ " palet." + "\nCena za palety: "
				+ Model.getInstance().rozvezenychPalet * Model.getInstance().cenaPalety + "K�\nZisk: "
				+ ((Model.getInstance().cenaPalety * Model.getInstance().rozvezenychPalet)
						- (Model.getInstance().ujetychKm * 25))
				+ "K�";
		return vypis;
	}

	/**
	 * Nastav� aktu�ln� �as
	 * @param cas		aktu�ln� �as
	 */
	public void setCas(int cas) {
		this.cas = cas;
	}

	/**
	 * Metoda zpracov�v� objedn�vky dokola, dokud se nevypr�zdn� seznam nezpracovan�ch objedn�vek.
	 */
	public void zpracujObjednavky() {

		while (!objednavky.isEmpty()) {
			int casCoZbyva = KONEC_DNE - cas;
			mc.vypisTA.appendText(zpracujObJednoAuto(casCoZbyva));
		}
	}
}
