package gui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class StatistikyController implements Initializable {

	public static final String VSTUP = "src/vystupnidata/Statistiky.txt";

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
