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
 * T��da {@code VypisController} reprezentuj�c� kontrol�r,
 * kter� se star� o funkcionalitu okna pro v�pis naposledy prob�hl� simulace.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class VypisController implements Initializable {

	/** Cesta k souboru, ve kter�m jsou ulo�eny data z posledn� simulace */
	public static final String VSTUP = "src/vystupnidata/VystupniData.txt";
	
	@FXML
	private TextArea vypisTA;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try(Scanner sc = new Scanner(new File(VSTUP))) {
			while(sc.hasNextLine()) {
				vypisTA.appendText(sc.nextLine() + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
}
