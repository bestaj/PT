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
 * T��da {@code MainController} reprezentuj�c� kontrol�r,
 * kter� se star� o funkcionality hlavn�ho okna u�ivatelsk�ho rozhran�.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
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
	/** Cesta k souboru se vstupn�mi daty. */
	public static final String VSTUP1 = "src/vstupnidata/VstupniData.txt";
	/** Soubor s celkov�mi statistikami. */
	public static final File VSTUP2 = new File("src/vystupnidata/Statistiky.txt");
	/** Cesta k souboru se z�znamem posledn� simulace. */
	public static final String VYSTUP = "src/vystupnidata/VystupniData.txt";
	
	// Na�ten� ikon
	public static final Image playImg = new Image("/gui/icons/play.png");
	public static final Image pauseImg = new Image("/gui/icons/pause.png");
	public static final Image stopImg = new Image("/gui/icons/stop.png");
	
	/** �as, kdy kon�� simulace. (20:30) */
	private final long KONEC_DNE = 73800000;
	/** �as, kdy kon�� chodit objedn�vky. (16:00) */
	private final long KONEC_OBJEDNAVEK = 57600000;
	/** �as, kdy za��n� simulace. (8:00) */
	private final long CAS = 28800000;
	/** Velk� �islo pro neexistenci cesty mezi dv�ma m�sty */
	private final int INF = 9999;
	/** Aktu�ln� �as */
	private long time;
	/** �as spu�t�n� simulace */
	private long startTime;
	/** �as pozastaven� simulace */
	private long casPozastaveni;
	/** �as op�tovn�ho spu�t�n� simulace */
	private long casSpusteni;
	
	// Druh� vl�kno pro generov�n� objedn�vek
	private Thread vlakno;
	private Vlakno mojeVlakno;
	
	/** Hlavni �asova� pro b�h simulace
	 * Vypisuje aktu�ln� �as a star� se o ukon�en� p��chodu nov�ch objedn�vek 
	 * a o ukon�en� simulace ve 20:30. 
	 */
	private Timeline timeline;
	/** �asova�, kter� kontroluje rozv�en� objedn�vky. 
	 * Doru�en� objedn�vky jsou ozna�en� zelen�m pozad�m.
	 */
	private Timeline timeline2;
	/** Seznam pr�v� doru�ovan�ch objedn�vek */
	private List<Objednavka> dorucovaneObjednavky;
	/** Seznam popisk� jednotliv�ch objedn�vek 
	 * Ka�d� popisek m� p�id�leno id, podle ��sla objedn�vky.
	 */
	private List<Label> nedoruceneObjLbl;
	// Detekuje, zda b�� simulace.
	private boolean beziSimulace = false;
	// Detekuje, zda u� byla simulace spu�t�na.
	private boolean poSpusteni = false;
	// Indikuje p��chod nov� objedn�vky.
	private boolean prislaObjednavka = false;
	private ZpracujObjednavky zpracujOb;
	private final Random rng = new Random();
	
	/** Provede se p�i vytvo�en� hlavn�ho okna. Nastav� se v�echny pot�ebn� instance, stavov� prom�nn�  
	 * a zavolaj� se metody pro na�ten� vstupn�ch dat {@link nacteniVstupnichDat},
	 * inicializace u�ivatelsk�ho rozhran� {@link inicializaceGUI}.
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

	/** Provede na�ten� vstupn�ch dat ze souboru. 
	 * - po�et m�st
	 * - kolik palet m��e maxim�ln� objednat dan� m�sto
	 * - cesty mezi m�sty a jejich vzd�lenost
	 */
	public void nacteniVstupnichDat() {
		try(Scanner sc = new Scanner(new File(VSTUP1))) {
			int pocetMest = Integer.parseInt(sc.nextLine());
			// Inicializace po�tu m�st, v�etn� na�� firmy
			Model.getInstance().pocetMest = pocetMest + 1; 
			// Inicializace matice pro vzd�lenosti mezi m�sty
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
			// Na�ten� seznamu m�st s maxim�ln�m mno�stv�m objedn�van�ch palet
			for (int i = 0; i < pocetMest; i++) {
				String mesta = sc.nextLine();
				String[] prvky = mesta.split(" ");
				Model.getInstance().mesta.add(new Mesto(Integer.parseInt(prvky[0]), Integer.parseInt(prvky[1])));
			}
			// Na�ten� jednotliv�ch cest mezi m�sty a jejich vzd�lenost�
			while (sc.hasNextLine()) {
				String radka = sc.nextLine();
				String[] prvky = radka.split(" ");
				Model.getInstance().vzdalenosti[Integer.parseInt(prvky[0])][Integer.parseInt(prvky[1])] = Integer.parseInt(prvky[2]);
			}
		} catch(IOException e) {
			System.out.println("Chyba");
		}
	}
	
	/** Provede inicializaci u�ivatelsk�ho rozhran�. */
	public void inicializaceGUI() {
		spustitBtn.setGraphic(new ImageView(playImg));
		pozastavitBtn.setGraphic(new ImageView(pauseImg));
		ukoncitBtn.setGraphic(new ImageView(stopImg));
		pridatBtn.setDisable(true);
		ukoncitBtn.setDisable(true);
		ukoncitMI.setDisable(true);
		pozastavitBtn.setDisable(true);
		pozastavitMI.setDisable(true);
		
		// Nastav� choiceBox na pat�i�n� po�et palet pro v�b�r, podle m�sta, kter� objedn�v�.
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
		        vypisTA.setScrollTop(Double.MAX_VALUE); // V�dy po p�id�n� nov� objedn�vky do textarey, scrollov�n� dol�
			}
		});
	}
	
	/** Testuje, zda je ve stringu ��slo
	 * a jestli je v intervalu od 1 do po�tu m�st.
	 * @return true, pokud je zadan� ��slo od 1 do pocetMest a false, pokud ne
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
	
	/** Spust� simulaci.
	 * Nastav� se pot�ebn� stavov� prom�nn�.
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
		vypisTA.appendText("Simulace spu�t�na.\n\n");
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
	
	/** Pozastav� simulaci */
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
			vypisTA.appendText("Simulace pokra�uje.\n\n");
		}
		
	}
	
	/** Ukon�� simulaci */
	@FXML
	public void ukoncitSimulaci() {
		beziSimulace = false; 
		vlakno.interrupt();
		timeline.stop();
		timeline2.stop();
		poSpusteni = false;
		vypisTA.appendText("Simulace ukon�ena.\n\n");
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
	
	/** P�id� novou objedn�vku do seznamu objedn�vek. */
	@FXML
	public void pridatObjednavku() {
		Mesto mesto = Model.getInstance().mesta.get(Integer.parseInt(objednavkaTF.getText()) - 1);
		if (mesto.isDnesObjednano()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Potvrzen�");
			alert.setHeaderText("Vybran� m�sto dnes ji� objedn�valo.");
			alert.setContentText("P�ejete si p�esto p�idat objedn�vku?");
		
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
	
	/** Vygeneruje 50 n�hodn�ch objedn�vek */
	@FXML
	public void genObj50() {
		generujObjednavky(50);
	}
	
	/** Vygeneruje 150 n�hodn�ch objedn�vek */
	@FXML
	public void genObj150() {
		generujObjednavky(150);
	}
	
	/** Vygeneruje 300 n�hodn�ch objedn�vek */
	@FXML
	public void genObj300() {
		generujObjednavky(300);
	}
	
	/** Vygeneruje dan� po�et n�hodn�ch objedn�vek 
	 * @param pocet, po�et generovan�ch objedn�vek 
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
	
	/** Vr�t� �as ve form�tu hh:mm:ss
	 * @param millis �as, kter� se m� p�ev�st (v milisekund�ch)
	 * @return �as ve form�tu hh:mm:ss
	 */
	public String setTime(long millis) {
		if (millis == 0) {
			setTime(CAS);
		}
	    return String.format("%tT", millis-TimeZone.getDefault().getRawOffset());
	}
    
	/** Do panulu objedn�vek p�id� novou 
	 * textovou reprezentaci objedn�vky 
	 * @param novaObj p�id�van� objedn�vka
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
	
	/** Otev�e okno pro vybranou objedn�vku
	 * @param novaObj, vybran� objedn�vka
	 */
	private void vytvorOknoObjednavky(Objednavka novaObj) {
		Stage oknoObjednavky = new Stage();
		oknoObjednavky.setTitle("V�pis");
		oknoObjednavky.setMinWidth(500);
		oknoObjednavky.setMinHeight(500);
		BorderPane pane = new BorderPane();
		Label lbl = new Label("Stav objedn�vky");
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
	
	
	/** Ulo�� pr�b�h cel� simulace do souboru. */
	private void ulozSimulaci() {
		try (FileWriter vystup = new FileWriter(new File(VYSTUP))) {
	        vystup.write(vypisTA.getText());
	        vystup.close();
		}
	   catch (IOException e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Nastala chyba p�i z�pisu do souboru.");
            alert.showAndWait();
            return;
        }
	}
	
	/** Zobraz� nov� okno, ve kter�m je vyps�n pr�b�h 
	 * naposledy spu�t�n� simulace. 
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
	
	/** Zobraz� nov� okno, ve kter�m jsou vyps�ny
	 * statistiky z p�edchoz�ch simulac�. 
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
	
	/** Zjist�, jak� byla cena palety p�i posledn� simulaci
	 * a tak� ��slo simulace.
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
	
	/** Ulo�� statistiky pr�v� prob�hl� simulace. */
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
			writer.println("Cena palety:\t\t\t\t" + Model.getInstance().cenaPalety + " K�");
			writer.println("-------------------------------------------");
			writer.println("P�ijat�ch objedn�vek:\t\t" + Model.getInstance().prijatychObjednavek);
			writer.println("Odm�tnut�ch objedn�vek:\t" + Model.getInstance().odmitnutychObjednavek);
			writer.println("-------------------------------------------");
			writer.println("Rozvezen�ch palet:\t\t\t" + Model.getInstance().rozvezenychPalet);
			writer.format("Cena za palety:\t\t\t%,d K�\n", Model.getInstance().rozvezenychPalet * Model.getInstance().cenaPalety);
			writer.println("Ujet�ch km:\t\t\t\t" + Model.getInstance().ujetychKm);
			writer.format("N�klady na dopravu:\t\t%,d K�\n", Model.getInstance().ujetychKm * 25);
			writer.format("Zisk:\t\t\t\t\t\t%,d K�\n", zisk);
			writer.println("----------------------------------------------------------------\n");
			writer.println(statistiky);
			writer.close();
		} catch (IOException ex2) {
			ex2.printStackTrace();
		}
	}
	
	/** Ukon�� aplikaci */
	@FXML
	public void ukoncitAplikaci() {
		if (beziSimulace) {
			timeline.stop();
			vlakno.interrupt();
		}
		
		Platform.exit();
	}
	
	/** Reakce na ukon�en� aplikace k��kem */
	private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
		if (vlakno != null) {
			vlakno.interrupt();
		}
    };
    
	
}
