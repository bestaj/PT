package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hlavní tøída {@code Main}, která je vstupním bodem aplikace.
 * Vytvoøí okno s obsahem daným z fxml souboru.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class Main extends Application {
	/** Hlavní okno aplikace */
	public static Stage window;
	
	/**
	 * Vstupní bod programu
	 * @param args, parametry pøíkazové øádky (nevyužité)
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("/gui/fxml/Main.fxml"));
		window.setTitle("Mistr Paleta, syn a vnuci");
		window.setMinWidth(800);
		window.setMinHeight(800);
		window.setScene(new Scene(root));
		window.show();
	}
}
