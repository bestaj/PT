package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hlavn� t��da {@code Main}, kter� je vstupn�m bodem aplikace.
 * Vytvo�� okno s obsahem dan�m z fxml souboru.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Main extends Application {
	/** Hlavn� okno aplikace */
	public static Stage window;
	
	/**
	 * Vstupn� bod programu
	 * @param args, parametry p��kazov� ��dky (nevyu�it�)
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
