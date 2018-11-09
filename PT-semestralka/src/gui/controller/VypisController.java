package gui.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import jdk.nashorn.internal.ir.CatchNode;

public class VypisController implements Initializable {

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
