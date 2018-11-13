package data;

import java.util.ArrayList;
import java.util.HashMap;

import data.Model;
import data.Objednavka;
import gui.controller.MainController;

public class ZpracujObjednavky {

	private ArrayList<Objednavka> objednavky;
	private final int INF = 9999;
	private final int CAS_V_JEDNEM_MESTE = 1800;
	private final int KONEC_DNE = 72000;
	private int pocetPalet;
	private int potrebujemePalet;
	private int aktualniIndex;
	private int cisloObjednavky = 1;
	public int cas;
	public int naklad;
	private MainController mc;
	HashMap<Integer, Objednavka> zpracovaneObjednavky;
	HashMap<Integer, Nakladak> pouziteNakladaky;
	private int cisloNakladaku;
	private int celkovyPocetRozvPalet;
	
	public ZpracujObjednavky(int cas, ArrayList<Objednavka> objednavky) {
		this.cas = cas;
		this.objednavky = objednavky;
		this.zpracovaneObjednavky = new HashMap<>();
		this.pouziteNakladaky = new HashMap<>();
	}

	// Get main controller
	public void setMainController(MainController mainController) {
		this.mc = mainController;
	}

	public int rozhodniKam(int a, ArrayList<Objednavka> objednavky) {
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

	public void nastavObj(Objednavka ob, int cisloObjednavky, int casDoruceni, int cisloNakladaku, boolean jePrijata) {
		ob.setCisloObjednavky(cisloObjednavky);
		ob.setCasDoruceni(casDoruceni);
		ob.setCisloNakladaku(cisloNakladaku);
		ob.setPrijato(jePrijata);
	}

	public boolean prijmout(int kolikCasuPotreba, int kolikCasuZbyva) {
		if (kolikCasuPotreba > kolikCasuZbyva) {
			return false;
		} else {
			return true;
		}
	}

	public int dorucPoCeste(ArrayList<Integer> mestaNaCeste, ArrayList<Objednavka> ob) {
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

	public String zpracujObJednoAuto(int casCoZbyva) {
		int cenaZaPaletu = 2000;
		ArrayList<Integer> mestaNaCeste = new ArrayList<>();
		Nakladak nakladak = new Nakladak(++cisloNakladaku);
		int casUjedeJednoAuto = 0;
		int pocatek = 0;
		pocetPalet = 6;
		int indexMesta = 0;
		int nakladNaJednoAuto = 0;
		int naklad = 0;
		boolean jdiDomu = false;
		String stavObjednavky = "";
		int kolikCasuPotreba = 0;
		int kolikVydelame = 0;
		int kolikUtratime = 0;

		HashMap<Integer, Integer> kandidatiNaObjednavku = new HashMap<>();

		while (true) {
			if (jdiDomu) {
				break;
			}

			indexMesta = rozhodniKam(pocatek, objednavky);
			mestaNaCeste = Model.getInstance().disp.cesta(pocatek, indexMesta, 0);
			//kolikCasuPotreba = Model.getInstance().nejrychlejsiCesty[pocatek][indexMesta];
			kolikCasuPotreba = 0;
			for(int i = 0; i < mestaNaCeste.size() - 1; i++) {
				kolikCasuPotreba += (Model.getInstance().casy[mestaNaCeste.get(i)][i+1])/1000;
			}
			
			kolikVydelame = potrebujemePalet * cenaZaPaletu;
			kolikUtratime = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;

			if (prijmout(kolikCasuPotreba, casCoZbyva)) {
				if (pocetPalet < potrebujemePalet) {
					ArrayList<Objednavka> pomocne = new ArrayList<>();
					for (int k = 0; k < objednavky.size(); k++) {
						if (objednavky.get(k).getPalet() <= pocetPalet) {
							pomocne.add(objednavky.get(k));
							kandidatiNaObjednavku.put(pomocne.size() - 1, k);
						}
					}

					if (pomocne.isEmpty()) {
						naklad = (Model.getInstance().nejkratsiCesty[pocatek][0]) * 25;
						nakladak.setKolikUjel(Model.getInstance().nejkratsiCesty[pocatek][0]);
						nakladNaJednoAuto += naklad;
						nakladak.setNaklad(nakladNaJednoAuto);
						nakladak.setKolikUjelCasu(casUjedeJednoAuto);
						this.naklad += naklad;
						nakladak.setRozvezPalet(pocetPalet);
						pouziteNakladaky.put(cisloNakladaku, nakladak);
						jdiDomu = true;

					} else {
						indexMesta = rozhodniKam(pocatek, pomocne);
						if ((dorucPoCeste(mestaNaCeste, objednavky)) > 0) {
							indexMesta = dorucPoCeste(mestaNaCeste, objednavky);
						}
						kolikVydelame = potrebujemePalet * cenaZaPaletu;
						kolikUtratime = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;

						// Pokud je to posledni mesto kam pojedeme
						if (((pocetPalet - potrebujemePalet) == 0)
								&& ((kolikUtratime + (Model.getInstance().nejkratsiCesty[indexMesta][0]) * 25)
										- kolikVydelame) < (Model.getInstance().nejkratsiCesty[pocatek][0]) * 25) {
							naklad = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;
							nakladak.setKolikUjel(Model.getInstance().nejkratsiCesty[pocatek][indexMesta]);
							nakladNaJednoAuto += naklad;
							this.naklad += naklad;
							kolikCasuPotreba += (CAS_V_JEDNEM_MESTE * potrebujemePalet);
							casUjedeJednoAuto += kolikCasuPotreba;
							
							casCoZbyva -= kolikCasuPotreba;
							
							
							nakladak.setKolikUjelCasu(casUjedeJednoAuto + cas);
							pocatek = indexMesta;
							pocetPalet -= pomocne.get(this.aktualniIndex).getPalet();
							pomocne.remove(this.aktualniIndex);
							stavObjednavky += "Objednávka èíslo " + cisloObjednavky + " je pøijata.\n";
							stavObjednavky += "Objednávku bude vyøizovat nákladní auto èíslo " + cisloNakladaku + ".\n";
							stavObjednavky += "Pøedpokádaný èas doruèení je " + mc.setTime((casUjedeJednoAuto + cas) * 1000)
									+ ".\n\n";
							nastavObj(objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)), cisloObjednavky,
									(casUjedeJednoAuto + cas), cisloNakladaku, true);
							nakladak.setObjednavkyCoVeze(cisloObjednavky);
							zpracovaneObjednavky.put(cisloObjednavky,
									objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)));

							mc.pridejObjednavku(objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)), true);
							Model.getInstance().dorucovaneObjednavky.add(objednavky.get(aktualniIndex));
							
							objednavky.remove((int) kandidatiNaObjednavku.get(aktualniIndex));
							cisloObjednavky++;
							if (objednavky.isEmpty()) {
								naklad = (Model.getInstance().nejkratsiCesty[pocatek][0]) * 25;
								nakladak.setKolikUjel(Model.getInstance().nejkratsiCesty[pocatek][0]);
								nakladNaJednoAuto += naklad;
								nakladak.setNaklad(nakladNaJednoAuto);
								this.naklad += naklad;
								nakladak.setRozvezPalet(pocetPalet);
								pouziteNakladaky.put(cisloNakladaku, nakladak);
								break;
							}

						}else {
							naklad = (Model.getInstance().nejkratsiCesty[pocatek][0]) * 25;
							nakladak.setKolikUjel(Model.getInstance().nejkratsiCesty[pocatek][0]);
							nakladNaJednoAuto += naklad;
							nakladak.setNaklad(nakladNaJednoAuto);
							nakladak.setKolikUjelCasu(casUjedeJednoAuto);
							this.naklad += naklad;
							nakladak.setRozvezPalet(pocetPalet);
							pouziteNakladaky.put(cisloNakladaku, nakladak);
							jdiDomu = true;
							
						}
						

					}

				} else {
					if ((dorucPoCeste(mestaNaCeste, objednavky)) > 0) {
						indexMesta = dorucPoCeste(mestaNaCeste, objednavky);
					}
					naklad = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;
					nakladNaJednoAuto += naklad;
					this.naklad += naklad;
					kolikCasuPotreba += (CAS_V_JEDNEM_MESTE * potrebujemePalet);
					
					
					casCoZbyva -= kolikCasuPotreba;
					
					
					casUjedeJednoAuto += kolikCasuPotreba;
					nakladak.setKolikUjelCasu(casUjedeJednoAuto + cas);
					pocatek = indexMesta;
					pocetPalet -= objednavky.get(this.aktualniIndex).getPalet();
					stavObjednavky += "Objednávka èíslo " + cisloObjednavky + " je pøijata.\n";
					stavObjednavky += "Objednávku bude vyøizovat nákladní auto èíslo " + cisloNakladaku + ".\n";
					stavObjednavky += "Pøedpokádaný èas doruèení je " + mc.setTime((casUjedeJednoAuto + cas) * 1000) + ".\n\n";
					nastavObj(objednavky.get(aktualniIndex), cisloObjednavky, (casUjedeJednoAuto + cas), cisloNakladaku,
							true);

					mc.pridejObjednavku(objednavky.get(aktualniIndex), true);
					Model.getInstance().dorucovaneObjednavky.add(objednavky.get(aktualniIndex));
					
					nakladak.setObjednavkyCoVeze(cisloObjednavky);
					zpracovaneObjednavky.put(cisloObjednavky, objednavky.get(aktualniIndex));
					objednavky.remove(this.aktualniIndex);
					cisloObjednavky++;
					if (objednavky.isEmpty()) {
						naklad = (Model.getInstance().nejkratsiCesty[pocatek][0]) * 25;
						nakladak.setKolikUjel(Model.getInstance().nejkratsiCesty[pocatek][0]);
						nakladNaJednoAuto += naklad;
						nakladak.setNaklad(nakladNaJednoAuto);
						this.naklad += naklad;
						nakladak.setRozvezPalet(pocetPalet);
						pouziteNakladaky.put(cisloNakladaku, nakladak);
						break;
					}

				}
			} else {
			//	for (int i = 0; i < objednavky.size(); i++) {
			//		stavObjednavky += "Objednávka èíslo " + cisloObjednavky + " je zamítnuta.\n\n";
					objednavky.get(aktualniIndex).setCisloObjednavky(cisloObjednavky);
			//		zpracovaneObjednavky.put(cisloObjednavky, objednavky.get(aktualniIndex));
			//		objednavky.remove(i);
			//		mc.pridejObjednavku(objednavky.get(aktualniIndex), true);
					if (objednavky.isEmpty()) {
						break;
					}
			//		cisloObjednavky++;
				}
				
				jdiDomu = true;
			}
	//	}
		return stavObjednavky;
	}

	public String stavObjednavky(int cisloObjednavky, long aktualniCas) {
		String vypis = "---------------------------------------\n";
		if (zpracovaneObjednavky.containsKey(cisloObjednavky)) {
			vypis += zpracovaneObjednavky.get(cisloObjednavky).toString() + ". \n";
			if (aktualniCas < zpracovaneObjednavky.get(cisloObjednavky).getCasDoruceni()) {
				vypis += "Objednávka ještì není doruèena. (Probíhá doruèení)\n---------------------------------------\n";
			} else {
				vypis += "Objednávka již byla doruèena.\n---------------------------------------\n";
			}
		} else {
			vypis += "Objednávka èíslo " + cisloObjednavky + " není v seznamu.\n---------------------------------------\n";
		}

		return vypis;

	}

	public String stavNakladaku(int aktualniCas, int cisloNakladaku) {
		String vypis = "\n---------------------------------------\n";

		vypis += pouziteNakladaky.get(cisloNakladaku).toString();
		if (pouziteNakladaky.get(cisloNakladaku).getKolikUjelCasu() > aktualniCas) {
			vypis += "V dané chvíli nákladní auto ještì rozváží.\n";
		} else {
			vypis += "Nákladní auto již rozvezlo všechny palety.";
		}
		return vypis;

	}
	
	public String statistikySimulace() {
		String vypis = "\n---------------------------------------\n";
		vypis += "Celkem bylo použito " + pouziteNakladaky.size() + " nákladních aut.\nRozvezeno " + celkovyPocetRozvPalet
				+ " palet. \nNáklady na dopravu: " + naklad + " Kè\n";
		return vypis;
	}

	public void setCas(int cas) {
		this.cas = cas;
	}

	public void zpracujObjednavky() {

		int casCoZbyva = KONEC_DNE - cas;
		while (!objednavky.isEmpty()) {
			mc.vypisTA.appendText(zpracujObJednoAuto(casCoZbyva));
			casCoZbyva = KONEC_DNE - cas;
			if (pouziteNakladaky.get(cisloNakladaku) != null) {
				celkovyPocetRozvPalet += pouziteNakladaky.get(cisloNakladaku).getRozvezPalet();
			}
		}
	}
}
