package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static Stage window;
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("/gui/fxml/Main.fxml"));
	//	window.getIcons().add(new Image(getClass().getResourceAsStream("/uur/sp/calendar/assets/windowIcon.png")));
		window.setTitle("Mistr Paleta, syn a vnuci");
		window.setMinWidth(800);
		window.setMinHeight(800);
		window.setScene(new Scene(root));
		window.show();
		
	}

}
