package data;

import java.util.ArrayList;
import java.util.HashMap;

import data.Model;
import data.Objednavka;
import gui.controller.MainController;
import sun.management.MappedMXBeanType;

public class ZpracujObjednavky {

	private ArrayList<Objednavka> objednavky;
	private final int INF = 9999;
	private final int CAS_V_JEDNOM_MESTE = 1800;
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

	public boolean prijmout(int kolikCasuPotreba, int kolikCasuZbyva) {
		if (kolikCasuPotreba > kolikCasuZbyva) {
			return false;
		} else {
			return true;
		}
	}

	public String zpracujObJednoAuto(int casCoZbyva) {
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
		HashMap<Integer, Integer> kandidatiNaObjednavku = new HashMap<>();

		while (true) {
			if (jdiDomu) {
				break;
			}

			indexMesta = rozhodniKam(pocatek, objednavky);
			kolikCasuPotreba = Model.getInstance().nejrychlejsiCesty[pocatek][indexMesta];

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
						naklad = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;
						nakladak.setKolikUjel(Model.getInstance().nejkratsiCesty[pocatek][indexMesta]);
						nakladNaJednoAuto += naklad;
						this.naklad += naklad;
						casUjedeJednoAuto += (kolikCasuPotreba + CAS_V_JEDNOM_MESTE);
						nakladak.setKolikUjelCasu(casUjedeJednoAuto + cas);
						pocatek = indexMesta;
						pocetPalet -= pomocne.get(this.aktualniIndex).getPalet();
						pomocne.remove(this.aktualniIndex);
						stavObjednavky += "Objednávka èíslo " + cisloObjednavky + " je pøijata.\n";
						stavObjednavky += "Objednávku bude vyøizovat nákladní auto èíslo " + cisloNakladaku + ".\n";
						stavObjednavky += "Pøedpokádaný èas doruèení je " + mc.setTime((casUjedeJednoAuto + cas)*1000) + ".\n\n";
						objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex))
								.setCisloObjednavky(cisloObjednavky);
						objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex))
								.setCasDoruceni(casUjedeJednoAuto + cas);
						objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex))
								.setCisloNakladaku(cisloNakladaku);
						objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)).setPrijato(true);
						nakladak.setObjednavkyCoVeze(cisloObjednavky);
						zpracovaneObjednavky.put(cisloObjednavky,
								objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)));
						
						mc.pridejObjednavku(objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)), true);
						Model.getInstance().dorucovaneObjednavky.add(objednavky.get((int) kandidatiNaObjednavku.get(aktualniIndex)));
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

					}

				} else {

					naklad = (Model.getInstance().nejkratsiCesty[pocatek][indexMesta]) * 25;
					nakladNaJednoAuto += naklad;
					this.naklad += naklad;
					casUjedeJednoAuto += (kolikCasuPotreba + CAS_V_JEDNOM_MESTE);
					nakladak.setKolikUjelCasu(casUjedeJednoAuto + cas);
					pocatek = indexMesta;
					pocetPalet -= objednavky.get(this.aktualniIndex).getPalet();
					stavObjednavky += "Objednávka èíslo " + cisloObjednavky + " je pøijata.\n";
					stavObjednavky += "Objednávku bude vyøizovat nákladní auto èíslo " + cisloNakladaku + ".\n";
					stavObjednavky += "Pøedpokádaný èas doruèení je " + mc.setTime((casUjedeJednoAuto + cas)*1000) + ".\n\n";
					objednavky.get(aktualniIndex).setCisloObjednavky(cisloObjednavky);
					objednavky.get(aktualniIndex).setCasDoruceni(casUjedeJednoAuto + cas);
					objednavky.get(aktualniIndex).setCisloNakladaku(cisloNakladaku);
					objednavky.get(aktualniIndex).setPrijato(true);
					
					mc.pridejObjednavku(objednavky.get(aktualniIndex), true);
					
					nakladak.setObjednavkyCoVeze(cisloObjednavky);
					zpracovaneObjednavky.put(cisloObjednavky, objednavky.get(aktualniIndex));
					Model.getInstance().dorucovaneObjednavky.add(objednavky.get(aktualniIndex));
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
				for (int i = 0; i < objednavky.size(); i++) {
					stavObjednavky += "Objednávka èíslo" + cisloObjednavky + " je zamítnuta.\n";
					objednavky.get(aktualniIndex).setCisloObjednavky(cisloObjednavky);
					zpracovaneObjednavky.put(cisloObjednavky, objednavky.get(aktualniIndex));
					objednavky.remove(i);
					if (objednavky.isEmpty()) {
						break;
					}
					cisloObjednavky++;
				}
				jdiDomu = true;
			}
		}
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

	public String stavNakladaku(int aktualniCas, boolean detailniInfo, int cisloNakladaku) {
		String vypis = "\n---------------------------------------\n";

		if (detailniInfo) {
			 vypis += pouziteNakladaky.get(cisloNakladaku).toString();
			 if(pouziteNakladaky.get(cisloNakladaku).getKolikUjelCasu() > aktualniCas) {
				 vypis += "V dané chvíli nákladní auto ještì rozváží.\n";
			 }else {
				 vypis += "Nákladní auto již rozvezlo všechny palety.";
			 }
		} else {
			vypis += "Celkem bylo použito " + pouziteNakladaky.size() + " nákladních aut.\nRozvezeno " + celkovyPocetRozvPalet
					+ " palet. \nNáklady na dopravu: " + naklad + " Kè\n";
		}
		
		return vypis;

	}

	public void setCas(int cas) {
		this.cas = cas;
	}
	
	public void zpracujObjednavky() {

		int casCoZbyva = KONEC_DNE - cas;
		while (!objednavky.isEmpty()) {

			mc.vypisTA.appendText(zpracujObJednoAuto(casCoZbyva));
			celkovyPocetRozvPalet += pouziteNakladaky.get(cisloNakladaku).getRozvezPalet();

		}
	}
}
