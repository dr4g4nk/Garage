package application;
	

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import application.controller.AdminController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import util.LoggerWrapper;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("view"+File.separator+"Admin.fxml"));
			Scene scene = new Scene(root,1000,600);
			scene.getStylesheets().add(getClass().getResource("style"+File.separator+"application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Garaža - Administrator");
			primaryStage.getIcons().add(new Image(new FileInputStream("icons"+File.separator+"parking.png")));
			primaryStage.setOnCloseRequest(e ->AdminController.serialize(AdminController.getGaraza()));
			primaryStage.show();
		} catch(Exception e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}