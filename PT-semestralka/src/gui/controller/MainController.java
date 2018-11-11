package gui.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

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
	public static final String VSTUP = "src/vstupnidata/VstupniData.txt";
	public static final String VYSTUP = "src/vystupnidata/VystupniData.txt";
	public static final Image playImg = new Image("/gui/icons/play.png");
	public static final Image pauseImg = new Image("/gui/icons/pause.png");
	public static final Image stopImg = new Image("/gui/icons/stop.png");
	
	private final long KONEC_DNE = 73800000;
	private final long KONEC_OBJEDNAVEK = 57600000;
	private final long CAS = 28800000;
	public Random rng = new Random();
	private final int INF = 9999;
	/** Aktualni simulacni cas */
	private long time;
	/** Cas spusteni simulace */
	private long startTime;
	/** Cas pozastaveni simulace */
	private long casPozastaveni;
	private long casSpusteni;
	
	private Thread vlakno;
	private Vlakno mojeVlakno;
	
	/** Casovac pro simulaci */
	private Timeline timeline;
	private Timeline timeline2;
	
	private ArrayList<Objednavka> dorucovaneObjednavky;
	private ArrayList<Label> nedoruceneObjLbl;
	private boolean beziSimulace = false;
	private boolean poSpusteni = false;
	private boolean prislaObjednavka = false;
	private int pomCisloObj = 1;
	public ZpracujObjednavky zpracujOb;
	
	
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
		dorucovaneObjednavky = Model.getInstance().dorucovaneObjednavky;
		nedoruceneObjLbl = new ArrayList<>();
		inicializaceGUI();
		time = CAS;
		Main.window.setOnCloseRequest(confirmCloseEventHandler);
		
	}

	public void nacteniVstupnichDat() {
		try(Scanner sc = new Scanner(new File(VSTUP))) {
			int pocetMest = Integer.parseInt(sc.nextLine());
			// Inicializace poctu mest vcetne firmy
			Model.getInstance().pocetMest = pocetMest + 1; 
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
			
			for (int i = 0; i < pocetMest; i++) {
				String mesta = sc.nextLine();
				String[] prvky = mesta.split(" ");
				Model.getInstance().mesta.add(new Mesto(Integer.parseInt(prvky[0]), Integer.parseInt(prvky[1])));
			}
			
			while (sc.hasNextLine()) {
				String radka = sc.nextLine();
				String[] prvky = radka.split(" ");
				Model.getInstance().vzdalenosti[Integer.parseInt(prvky[0])][Integer.parseInt(prvky[1])] = Integer.parseInt(prvky[2]);
			}
		} catch(IOException e) {
			System.out.println("Chyba");
		}
		
	}
	
	public void inicializaceGUI() {
		spustitBtn.setGraphic(new ImageView(playImg));
		pozastavitBtn.setGraphic(new ImageView(pauseImg));
		ukoncitBtn.setGraphic(new ImageView(stopImg));
		pridatBtn.setDisable(true);
		ukoncitBtn.setDisable(true);
		ukoncitMI.setDisable(true);
		pozastavitBtn.setDisable(true);
		pozastavitMI.setDisable(true);
		
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
		        vypisTA.setScrollTop(Double.MAX_VALUE); // vzdy po pridani do textarey, scrolovani dolu
			}
		});
		
			
		
	}
	
	/**
	 * Testuje zda je ve stringu cislo
	 * a jestli je v intervalu od 1 do poctu mest
	 * @return true, pokud je zadane cislo od 1 do pocetMest a false, pokud ne
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
			          				
			          				if (prislaObjednavka == true) {
			          					zpracujOb.zpracujObjednavky();
			          					prislaObjednavka = false;
			          				}
			          				
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
			mojeVlakno = new Vlakno();
			mojeVlakno.setMainController(this);
			vlakno = new Thread(mojeVlakno);
			vlakno.start();
			casSpusteni += System.currentTimeMillis();
			beziSimulace = true;
			vypisTA.appendText("Simulace pokraèuje.\n\n");
		}
		
	}
	
	/**
	 * Ukonèí prùbìh simulace
	 */
	@FXML
	public void ukoncitSimulaci() {
		beziSimulace = false; 
		vlakno.interrupt();
		timeline.stop();
		timeline2.stop();
		poSpusteni = false;
		vypisTA.appendText("Simulace ukonèena.\n\n");
		vypisTA.appendText(zpracujOb.stavNakladaku((int)(time/1000), false, 1));
		ulozSimulaci();
		pozastavitBtn.setDisable(true);
		pozastavitMI.setDisable(true);
		ukoncitBtn.setDisable(true);
		ukoncitMI.setDisable(true);
		spustitBtn.setDisable(false);
		spustitMI.setDisable(false);
		casPozastaveni = 0;
		casSpusteni = 0;
		Model.getInstance().nezpracovaneObjednavky.clear();
		timeLbl.setText(setTime(0));
		seznamObjednavek.getChildren().clear();
		paletCB.getSelectionModel().clearSelection();
		objednavkaTF.clear();
	}
	
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
					pridejObjednavku(novaObj, poSpusteni);
				}
			}
			
		}
		else {
			Objednavka novaObj = new Objednavka(Integer.parseInt(objednavkaTF.getText()), (int)paletCB.getValue(), setTime(time));
			Model.getInstance().nezpracovaneObjednavky.add(novaObj);
			prislaObjednavka = true;
			mesto.setDnesObjednano(true);
			if (!poSpusteni) {
				pridejObjednavku(novaObj, poSpusteni);
			}
		}
	}
	
	@FXML
	public void genObj50() {
		generujObjednavky(50);
	}
	
	@FXML
	public void genObj150() {
		generujObjednavky(150);
	}
	
	@FXML
	public void genObj300() {
		generujObjednavky(300);
	}
	
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
				pridejObjednavku(novaObj, poSpusteni);
			}
		}
		prislaObjednavka = true;
	}
	
	/** Vrati retezec casu
	 * @param millis cas, ktery se ma prevest (v milisekundach)
	 * @return cas ve formatu hh:mm:ss
	 */
	public String setTime(long millis) {
		if (millis == 0) {
			setTime(CAS);
		}
		long sec = millis/1000;
	    long second = sec % 60;
	    int minute = (int)((sec / 60) % 60);
	    int hour = (int)(sec / 3600);
	    return String.format("%tT", millis-TimeZone.getDefault().getRawOffset());
		
	}
    
	/** Do panulo objednavek prida novou 
	 * textovou reprezentaci objednavky 
	 * @param novaObj nova objednavka
	 */
	public void pridejObjednavku(Objednavka novaObj, boolean poSpusteni) {
		Label textObj = new Label(novaObj.strucnyPopis());
		textObj.setPadding(new Insets(3));
		textObj.setStyle("-fx-border-color: BLACK;");
		textObj.setId(String.valueOf(novaObj.getCisloObjednavky()));
		textObj.setMinSize(150, 80);
		nedoruceneObjLbl.add(textObj);
		if (poSpusteni) {
			textObj.setCursor(Cursor.HAND);
			textObj.setOnMouseClicked(e -> {	
				vytvorOknoObjednavky(novaObj);
			});
		}
		seznamObjednavek.getChildren().add(0, textObj);
	}
	
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
		textAr.appendText(zpracujOb.stavNakladaku((int)(time/1000), true, novaObj.getCisloNakladaku()));
		pane.setTop(lbl);
		pane.setCenter(textAr);
		oknoObjednavky.setScene(new Scene(pane));
		oknoObjednavky.show();
		
	}
	
	
	/** Ulozi prubeh cele simulace do souboru */
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
	
	@FXML
	private void ukazData() {
		Stage noveOkno = new Stage();
		noveOkno.setTitle("Info");
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/gui/fxml/Vypis.fxml"));
			noveOkno.setScene(new Scene(root));
			noveOkno.show();
		} catch (IOException e) {
			e.printStackTrace();
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
