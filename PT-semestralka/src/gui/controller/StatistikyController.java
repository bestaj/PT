package gui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * T��da {@code StatistikyController} reprezentuje kontrol�r,
 * kter� se star� o funkcionalitu okna se statistikami simulac�.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class StatistikyController implements Initializable {

	/** Cesta k souboru, ve kter�m jsou ulo�eny statistiky prob�hl�ch simulac�. */
	public static final String VSTUP = "src/vystupnidata/Statistiky.txt";

	@FXML
	private TextArea vypisTA;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vypisTA.setScrollTop(Double.MAX_VALUE);
		try(Scanner sc = new Scanner(new File(VSTUP))) {
			while(sc.hasNextLine()) {
				vypisTA.appendText(sc.nextLine() + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
