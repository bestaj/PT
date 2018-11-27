package gui.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TimeZone;

import data.Dispetcher;
import data.Mesto;
import data.Model;
import data.Objednavka;
import data.Vlakno;
import data.ZpracujObjednavky;
import gui.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/** 
 * Tøída {@code MainController} reprezentující kontrolér,
 * který se stará o funkcionality hlavního okna uživatelského rozhraní.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class MainController implements Initializable {
	
	@FXML
	private VBox seznamObjednavek;
	@FXML
	private TextField objednavkaTF;
	@FXML
	private Label timeLbl;
	@FXML
	private ChoiceBox paletCB;
	@FXML
	public TextArea vypisTA;
	@FXML
	private Button pridatBtn, spustitBtn, pozastavitBtn, ukoncitBtn;
	@FXML
	private MenuItem spustitMI, pozastavitMI, ukoncitMI; 
	
	public static MainController mc;
	/** Cesta k souboru se vstupními daty. */
	public static final String VSTUP1 = "src/vstupnidata/VstupniData.txt";
	/** Soubor s celkovými statistikami. */
	public static final File VSTUP2 = new File("src/vystupnidata/Statistiky.txt");
	/** Cesta k souboru se záznamem poslední simulace. */
	public static final String VYSTUP = "src/vystupnidata/VystupniData.txt";
	
	// Naètení ikon
	public static final Image playImg = new Image("/gui/icons/play.png");
	public static final Image pauseImg = new Image("/gui/icons/pause.png");
	public static final Image stopImg = new Image("/gui/icons/stop.png");
	
	/** Èas, kdy konèí simulace. (20:30) */
	private final long KONEC_DNE = 73800000;
	/** Èas, kdy konèí chodit objednávky. (16:00) */
	private final long KONEC_OBJEDNAVEK = 57600000;
	/** Èas, kdy zaèíná simulace. (8:00) */
	private final long CAS = 28800000;
	/** Velké èislo pro neexistenci cesty mezi dvìma mìsty */
	private final int INF = 9999;
	/** Aktuální èas */
	private long time;
	/** Èas spuštìní simulace */
	private long startTime;
	/** Èas pozastavení simulace */
	private long casPozastaveni;
	/** Èas opìtovného spuštìní simulace */
	private long casSpusteni;
	
	// Druhé vlákno pro generování objednávek
	private Thread vlakno;
	private Vlakno mojeVlakno;
	
	/** Hlavni èasovaè pro bìh simulace
	 * Vypisuje aktuálnÍ èas a stará se o ukonèení pøíchodu nových objednávek 
	 * a o ukonèení simulace ve 20:30. 
	 */
	private Timeline timeline;
	/** Èasovaè, který kontroluje rozvážené objednávky. 
	 * Doruèené objednávky jsou oznaèené zeleným pozadím.
	 */
	private Timeline timeline2;
	/** Seznam právì doruèovaných objednávek */
	private List<Objednavka> dorucovaneObjednavky;
	/** Seznam popiskù jednotlivých objednávek 
	 * Každý popisek má pøidìleno id, podle èísla objednávky.
	 */
	private List<Label> nedoruceneObjLbl;
	// Detekuje, zda bìží simulace.
	private boolean beziSimulace = false;
	// Detekuje, zda už byla simulace spuštìna.
	private boolean poSpusteni = false;
	// Indikuje pøíchod nové objednávky.
	private boolean prislaObjednavka = false;
	private ZpracujObjednavky zpracujOb;
	private final Random rng = new Random();
	
	/** Provede se pøi vytvoøení hlavního okna. Nastaví se všechny potøebné instance, stavové promìnné  
	 * a zavolají se metody pro naètení vstupních dat {@link nacteniVstupnichDat},
	 * inicializace uživatelského rozhraní {@link inicializaceGUI}.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mc = new MainController();
		nacteniVstupnichDat();
		Model.getInstance().disp = new Dispetcher(Model.getInstance().pocetMest);
		Model.getInstance().casy = Model.getInstance().disp.generujCasyMeziMesty(Model.getInstance().vzdalenosti);
		
		int[][] vzd = new int[Model.getInstance().pocetMest][Model.getInstance().pocetMest];
		int[][] cas = new int[Model.getInstance().pocetMest][Model.getInstance().pocetMest];
		for (int i = 0; i < Model.getInstance().pocetMest; i++) {
			for (int j = 0; j < Model.getInstance().pocetMest; j++) {
				vzd[i][j] = Model.getInstance().vzdalenosti[i][j];
				cas[i][j] = Model.getInstance().casy[i][j];
			}
		}
		
		Model.getInstance().nejkratsiCesty = Model.getInstance().disp.floydAlg(vzd, 0); 
		Model.getInstance().nejrychlejsiCesty = Model.getInstance().disp.floydAlg(cas, 1); 
		
		Model.getInstance().nezpracovaneObjednavky = new ArrayList<>();
		Model.getInstance().dorucovaneObjednavky = new ArrayList<>();
		this.dorucovaneObjednavky = Model.getInstance().dorucovaneObjednavky;
		nedoruceneObjLbl = new ArrayList<>();
		inicializaceGUI();
		time = CAS; 
		Main.window.setOnCloseRequest(confirmCloseEventHandler);
	}

	/** Provede naètení vstupních dat ze souboru. 
	 * - poèet mìst
	 * - kolik palet mùže maximálnì objednat dané mìsto
	 * - cesty mezi mìsty a jejich vzdálenost
	 */
	public void nacteniVstupnichDat() {
		try(Scanner sc = new Scanner(new File(VSTUP1))) {
			int pocetMest = Integer.parseInt(sc.nextLine());
			// Inicializace poètu mìst, vèetnì naší firmy
			Model.getInstance().pocetMest = pocetMest + 1; 
			// Inicializace matice pro vzdálenosti mezi mìsty
			Model.getInstance().vzdalenosti = new int[pocetMest+1][pocetMest+1];
			for (int i = 0; i < pocetMest + 1; i++) {
				for (int j = 0; j < pocetMest + 1; j++) {
					if (i == j) {
						Model.getInstance().vzdalenosti[i][j] = 0;
					}
					else {
						Model.getInstance().vzdalenosti[i][j] = INF;
					}
				}
			}
			// Naètení seznamu mìst s maximálním množstvím objednávaných palet
			for (int i = 0; i < pocetMest; i++) {
				String mesta = sc.nextLine();
				String[] prvky = mesta.split(" ");
				Model.getInstance().mesta.add(new Mesto(Integer.parseInt(prvky[0]), Integer.parseInt(prvky[1])));
			}
			// Naètení jednotlivých cest mezi mìsty a jejich vzdáleností
			while (sc.hasNextLine()) {
				String radka = sc.nextLine();
				String[] prvky = radka.split(" ");
				Model.getInstance().vzdalenosti[Integer.parseInt(prvky[0])][Integer.parseInt(prvky[1])] = Integer.parseInt(prvky[2]);
			}
		} catch(IOException e) {
			System.out.println("Chyba");
		}
	}
	
	/** Provede inicializaci uživatelského rozhraní. */
	public void inicializaceGUI() {
		spustitBtn.setGraphic(new ImageView(playImg));
		pozastavitBtn.setGraphic(new ImageView(pauseImg));
		ukoncitBtn.setGraphic(new ImageView(stopImg));
		pridatBtn.setDisable(true);
		ukoncitBtn.setDisable(true);
		ukoncitMI.setDisable(true);
		pozastavitBtn.setDisable(true);
		pozastavitMI.setDisable(true);
		
		// Nastaví choiceBox na patøièný poèet palet pro výbìr, podle mìsta, které objednává.
		objednavkaTF.setOnKeyReleased(e -> {
			if (jeSpravnaHodnota()) {
				int palet = Model.getInstance().mesta.get(Integer.parseInt(objednavkaTF.getText())-1).getMaxPalet();
				ObservableList<Integer> items = FXCollections.observableArrayList();
				for (int i = 1; i <= palet; i++) {
					items.add(i);
				}
				paletCB.setItems(items);
				paletCB.setValue(items.get(palet-1));
				pridatBtn.setDisable(false);
			}	
		});	
		timeLbl.setText(setTime(CAS));
		vypisTA.textProperty().addListener(new ChangeListener<Object>() {
			@Override
		    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
		        vypisTA.setScrollTop(Double.MAX_VALUE); // Vždy po pøidání nové objednávky do textarey, scrollování dolù
			}
		});
	}
	
	/** Testuje, zda je ve stringu èíslo
	 * a jestli je v intervalu od 1 do poètu mìst.
	 * @return true, pokud je zadané èíslo od 1 do pocetMest a false, pokud ne
	 */
	public boolean jeSpravnaHodnota() {
		try {
			int hodnota = Integer.parseInt(objednavkaTF.getText());
			if (hodnota > 0 && hodnota < Model.getInstance().pocetMest) {
				return true;
			}
			else {
				pridatBtn.setDisable(true);
				paletCB.getSelectionModel().clearSelection();
				return false;
			}
		} catch(Exception ex) {
			pridatBtn.setDisable(true);
			paletCB.getSelectionModel().clearSelection();
			return false;
		}
	}
	
	/** Spustí simulaci.
	 * Nastaví se potøebné stavové promìnné.
	 */
	@FXML
	public void spustitSimulaci() {
		beziSimulace = true;
		poSpusteni = true;
		seznamObjednavek.getChildren().clear();
		vypisTA.clear();
		spustitBtn.setDisable(true);
		spustitMI.setDisable(true);
		pozastavitBtn.setDisable(false);
		pozastavitMI.setDisable(false);
		ukoncitBtn.setDisable(false);
		ukoncitMI.setDisable(false);
		
		zjistiCenuADen();
		startTime = System.currentTimeMillis();
		vypisTA.appendText("Simulace spuštìna.\n\n");
		timeline = new Timeline(
		      new KeyFrame(Duration.seconds(1.0/6.0), 
		    		 new EventHandler<ActionEvent>() {
		          		@Override 
		          		public void handle(ActionEvent actionEvent) {
		          			if (beziSimulace) {
		          				time = (CAS + ((System.currentTimeMillis() - startTime) - (casSpusteni - casPozastaveni)) * 120);
		    		            zpracujOb.setCas((int)(time/1000));
		          				timeLbl.setText(setTime(time));
		          				
		          				
		          				
		          				if (time > KONEC_DNE) {
		          					ukoncitSimulaci();
		          				}
		          				if (time > 	KONEC_OBJEDNAVEK) {
		          					vlakno.interrupt();
		          				}
		          			}
		          			
		          }
		        }
		      )
		    );
		    timeline.setCycleCount(Animation.INDEFINITE);
		    timeline.play();
		    
		    mojeVlakno = new Vlakno();
		    mojeVlakno.setMainController(this);
		    vlakno = new Thread(mojeVlakno);
		    vlakno.start();
		    
		    zpracujOb = new ZpracujObjednavky((int)(time/1000), Model.getInstance().nezpracovaneObjednavky);
		    zpracujOb.setMainController(this);
            zpracujOb.zpracujObjednavky();
		    
            timeline2 = new Timeline(
  			      new KeyFrame(Duration.seconds(5), 
  			    		 new EventHandler<ActionEvent>() {
  			          		@Override 
  			          		public void handle(ActionEvent actionEvent) {
  			          			if (beziSimulace) {
			                	
      			          			if (prislaObjednavka) {
			          					zpracujOb.zpracujObjednavky();
			          					prislaObjednavka = false;
			          				}
  			          				
				                	for (int i = 0; i < dorucovaneObjednavky.size(); i++) {
				                		if ((dorucovaneObjednavky.get(i).getCasDoruceni() * 1000) < time) {
				                			for (Label objText: nedoruceneObjLbl) {
				                				if (objText.getId().equals(String.valueOf(dorucovaneObjednavky.get(i).getCisloObjednavky()))) {
				                					objText.setStyle("-fx-background-color: #ccffcc");
				                				}
				                			}
				                			dorucovaneObjednavky.remove(dorucovaneObjednavky.get(i));
				                		}
				                	}
  			          			}	
  				          }
  				        }
  				      )
  				    );
            timeline2.setCycleCount(Animation.INDEFINITE);
		    timeline2.play();
	}
	
	/** Pozastaví simulaci */
	@FXML
	public void pozastavitSimulaci() {
		if (beziSimulace) {
			timeline.pause();
			timeline2.pause();
			vlakno.interrupt();
			casPozastaveni += System.currentTimeMillis();
			beziSimulace = false;
			vypisTA.appendText("Simulace pozastavena.\n");
			
		}
		else {
			timeline.play();
			timeline2.play();
			if (time < 	KONEC_OBJEDNAVEK) {
				mojeVlakno = new Vlakno();
				mojeVlakno.setMainController(this);
				vlakno = new Thread(mojeVlakno);
				vlakno.start();
			}
			casSpusteni += System.currentTimeMillis();
			beziSimulace = true;
			vypisTA.appendText("Simulace pokraèuje.\n\n");
		}
		
	}
	
	/** Ukonèí simulaci */
	@FXML
	public void ukoncitSimulaci() {
		beziSimulace = false; 
		vlakno.interrupt();
		timeline.stop();
		timeline2.stop();
		poSpusteni = false;
		vypisTA.appendText("Simulace ukonèena.\n\n");
		vypisTA.appendText(zpracujOb.statistikySimulace());
		ulozSimulaci();
		ulozStatistiky();
		pozastavitBtn.setDisable(true);
		pozastavitMI.setDisable(true);
		ukoncitBtn.setDisable(true);
		ukoncitMI.setDisable(true);
		spustitBtn.setDisable(false);
		spustitMI.setDisable(false);
		casPozastaveni = 0;
		casSpusteni = 0;
		time = CAS;
		Model.getInstance().nezpracovaneObjednavky.clear();
		Model.getInstance().odmitnutychObjednavek = 0;
		Model.getInstance().prijatychObjednavek = 0;
		Model.getInstance().ujetychKm = 0;
		Model.getInstance().rozvezenychPalet = 0;
		timeLbl.setText(setTime(0));
		nedoruceneObjLbl.clear();
		seznamObjednavek.getChildren().clear();
		paletCB.getSelectionModel().clearSelection();
		objednavkaTF.clear();
	}
	
	/** Pøidá novou objednávku do seznamu objednávek. */
	@FXML
	public void pridatObjednavku() {
		Mesto mesto = Model.getInstance().mesta.get(Integer.parseInt(objednavkaTF.getText()) - 1);
		if (mesto.isDnesObjednano()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Potvrzení");
			alert.setHeaderText("Vybrané mìsto dnes již objednávalo.");
			alert.setContentText("Pøejete si pøesto pøidat objednávku?");
		
			ButtonType btn1 = new ButtonType("Ano");
			ButtonType btn2 = new ButtonType("Ne");
			
			alert.getButtonTypes().setAll(btn1, btn2);
			
			Optional<ButtonType> result = alert.showAndWait();
			
			if (result.get() == btn1){
				Objednavka novaObj = new Objednavka(Integer.parseInt(objednavkaTF.getText()), (int)paletCB.getValue(), setTime(time));
				Model.getInstance().nezpracovaneObjednavky.add(novaObj);
				prislaObjednavka = true;
				mesto.setDnesObjednano(true);
				if (!poSpusteni) {
					pridejObjednavku(novaObj, poSpusteni, true);
				}
			}
			
		}
		else {
			Objednavka novaObj = new Objednavka(Integer.parseInt(objednavkaTF.getText()), (int)paletCB.getValue(), setTime(time));
			Model.getInstance().nezpracovaneObjednavky.add(novaObj);
			prislaObjednavka = true;
			mesto.setDnesObjednano(true);
			if (!poSpusteni) {
				pridejObjednavku(novaObj, poSpusteni, true);
			}
		}
	}
	
	/** Vygeneruje 50 náhodných objednávek */
	@FXML
	public void genObj50() {
		generujObjednavky(50);
	}
	
	/** Vygeneruje 150 náhodných objednávek */
	@FXML
	public void genObj150() {
		generujObjednavky(150);
	}
	
	/** Vygeneruje 300 náhodných objednávek */
	@FXML
	public void genObj300() {
		generujObjednavky(300);
	}
	
	/** Vygeneruje daný poèet náhodných objednávek 
	 * @param pocet, poèet generovaných objednávek 
	 */
	public void generujObjednavky(int pocet) {
		int mesto;
		int palet;
		for (int i = 0; i < pocet; i++) {
			do {
			mesto = 1 + rng.nextInt(Model.getInstance().pocetMest - 1);
			} while (Model.getInstance().mesta.get(mesto -1).isDnesObjednano());
			int maxPalet = Model.getInstance().mesta.get(mesto - 1).getMaxPalet();
			palet = 1 + rng.nextInt(maxPalet);
			Objednavka novaObj = new Objednavka(mesto, palet, setTime(time));
			Model.getInstance().nezpracovaneObjednavky.add(novaObj);
			if (!poSpusteni) {
				pridejObjednavku(novaObj, poSpusteni, true);
			}
		}
		prislaObjednavka = true;
	}
	
	/** Vrátí èas ve formátu hh:mm:ss
	 * @param millis èas, který se má pøevést (v milisekundách)
	 * @return èas ve formátu hh:mm:ss
	 */
	public String setTime(long millis) {
		if (millis == 0) {
			setTime(CAS);
		}
	    return String.format("%tT", millis-TimeZone.getDefault().getRawOffset());
	}
    
	/** Do panulu objednávek pøidá novou 
	 * textovou reprezentaci objednávky 
	 * @param novaObj pøidávaná objednávka
	 */
	public void pridejObjednavku(Objednavka novaObj, boolean poSpusteni, boolean prijata) {
		Label textObj = new Label(novaObj.strucnyPopis());
		textObj.setPadding(new Insets(3));
		if (prijata) {
			textObj.setStyle("-fx-border-color: BLACK");
		}
		else {
			textObj.setStyle("-fx-background-color: #ffcccc");
		}
		textObj.setId(String.valueOf(novaObj.getCisloObjednavky()));
		textObj.setMinSize(150, 80);
		nedoruceneObjLbl.add(textObj);
		if (poSpusteni && novaObj.isPrijato()) {
			textObj.setCursor(Cursor.HAND);
			textObj.setOnMouseClicked(e -> {	
				vytvorOknoObjednavky(novaObj);
			});
		}
		seznamObjednavek.getChildren().add(0, textObj);
	}
	
	/** Otevøe okno pro vybranou objednávku
	 * @param novaObj, vybraná objednávka
	 */
	private void vytvorOknoObjednavky(Objednavka novaObj) {
		Stage oknoObjednavky = new Stage();
		oknoObjednavky.setTitle("Výpis");
		oknoObjednavky.setMinWidth(500);
		oknoObjednavky.setMinHeight(500);
		BorderPane pane = new BorderPane();
		Label lbl = new Label("Stav objednávky");
		lbl.setFont(new Font(24));
		pane.setAlignment(lbl, Pos.CENTER);
		TextArea textAr = new TextArea();
		pane.setMargin(textAr, new Insets(10));
		textAr.appendText(zpracujOb.stavObjednavky(novaObj.getCisloObjednavky(), (int)(time/1000)));
		textAr.appendText(zpracujOb.stavNakladaku((int)(time/1000), novaObj.getCisloNakladaku()));
		pane.setTop(lbl);
		pane.setCenter(textAr);
		oknoObjednavky.setScene(new Scene(pane));
		oknoObjednavky.show();
	}
	
	
	/** Uloží prùbìh celé simulace do souboru. */
	private void ulozSimulaci() {
		try (FileWriter vystup = new FileWriter(new File(VYSTUP))) {
	        vystup.write(vypisTA.getText());
	        vystup.close();
		}
	   catch (IOException e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Nastala chyba pøi zápisu do souboru.");
            alert.showAndWait();
            return;
        }
	}
	
	/** Zobrazí nové okno, ve kterém je vypsán prùbìh 
	 * naposledy spuštìné simulace. 
	 */
	@FXML
	private void dataZposledniSimulace() {
		Stage noveOkno = new Stage();
		noveOkno.setTitle("Info");
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/gui/fxml/Vypis1.fxml"));
			noveOkno.setScene(new Scene(root));
			noveOkno.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Zobrazí nové okno, ve kterém jsou vypsány
	 * statistiky z pøedchozích simulací. 
	 */
	@FXML
	private void celkoveStatistiky() {
		Stage noveOkno = new Stage();
		noveOkno.setTitle("Info");
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/gui/fxml/Vypis2.fxml"));
			noveOkno.setScene(new Scene(root));
			noveOkno.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Zjistí, jaká byla cena palety pøi poslední simulaci
	 * a také èíslo simulace.
	 */
	private void zjistiCenuADen() {
		try(Scanner sc = new Scanner(VSTUP2)) {
			if (!sc.hasNextLine()) {
				Model.getInstance().den = 1;
				Model.getInstance().cenaPalety = 3000;
				sc.close();
			}
			else {
				String den = sc.nextLine();
				String cenaPalety = sc.nextLine();
				String[] pom1 = den.split(" ");
				String[] pom2 = cenaPalety.split("\t");
				pom2 = pom2[4].split(" ");
				Model.getInstance().den = Integer.parseInt(pom1[1]) + 1;
				Model.getInstance().cenaPalety = 3000; //Integer.parseInt(pom2[0]) + 300;
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/** Uloží statistiky právì probìhlé simulace. */
	private void ulozStatistiky() {
		String statistiky = "";
		
		try(Scanner sc = new Scanner(VSTUP2)) {
			if (!sc.hasNextLine()) {
				sc.close();
			}
			else {
				while(sc.hasNextLine()) {
					statistiky += sc.nextLine() + "\n";
				}
				sc.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		int zisk = (Model.getInstance().cenaPalety * Model.getInstance().rozvezenychPalet) - (Model.getInstance().ujetychKm * 25);
		try(PrintWriter writer = new PrintWriter(VSTUP2)) {
			writer.println("\t\t\t\tDen " + Model.getInstance().den);
			writer.println("Cena palety:\t\t\t\t" + Model.getInstance().cenaPalety + " Kè");
			writer.println("-------------------------------------------");
			writer.println("Pøijatých objednávek:\t\t" + Model.getInstance().prijatychObjednavek);
			writer.println("Odmítnutých objednávek:\t" + Model.getInstance().odmitnutychObjednavek);
			writer.println("-------------------------------------------");
			writer.println("Rozvezených palet:\t\t\t" + Model.getInstance().rozvezenychPalet);
			writer.format("Cena za palety:\t\t\t%,d Kè\n", Model.getInstance().rozvezenychPalet * Model.getInstance().cenaPalety);
			writer.println("Ujetých km:\t\t\t\t" + Model.getInstance().ujetychKm);
			writer.format("Náklady na dopravu:\t\t%,d Kè\n", Model.getInstance().ujetychKm * 25);
			writer.format("Zisk:\t\t\t\t\t\t%,d Kè\n", zisk);
			writer.println("----------------------------------------------------------------\n");
			writer.println(statistiky);
			writer.close();
		} catch (IOException ex2) {
			ex2.printStackTrace();
		}
	}
	
	/** Ukonèí aplikaci */
	@FXML
	public void ukoncitAplikaci() {
		if (beziSimulace) {
			timeline.stop();
			vlakno.interrupt();
		}
		
		Platform.exit();
	}
	
	/** Reakce na ukonèení aplikace køížkem */
	private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
		if (vlakno != null) {
			vlakno.interrupt();
		}
    };
    
	
}
