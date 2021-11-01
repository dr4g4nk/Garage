package application.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.LoggerWrapper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

public class ErrorWindowController {
	@FXML private Label msgLabel;
	public static String msg;

	@FXML private void initialize() {
		msgLabel.setText(msg);
	}
	@FXML public void OKButtonAction() {
		((Stage)msgLabel.getScene().getWindow()).close();
	}
	
	public static void errorWindow(String msg, String title) {
		try {
			ErrorWindowController.msg = msg;
			GridPane root = (GridPane)FXMLLoader.load(Main.class.getResource("view"+File.separator+"ErrorWindow.fxml"));
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.getIcons().add(new Image(new FileInputStream("icons"+File.separator+"error.png")));
			stage.setTitle(title);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(Main.class.getResource("style"+File.separator+"application.css").toExternalForm());
			stage.setScene(scene);
			stage.setMaxWidth(490);
			stage.setMaxHeight(125);
			stage.showAndWait();
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}	
	}
}
