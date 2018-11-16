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
 * Tøída {@code VypisController} reprezentující kontrolér,
 * který se stará o funkcionalitu okna pro výpis naposledy probìhlé simulace.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class VypisController implements Initializable {

	/** Cesta k souboru, ve kterém jsou uloženy data z poslední simulace */
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
