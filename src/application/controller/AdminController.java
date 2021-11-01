package application.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import application.Main;
import garaza.Garaza;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.LoggerWrapper;
import javafx.stage.Modality;
import vozila.Vozilo;


public class AdminController {
	@FXML private Button dodajNovoVoziloButton;
	@FXML private ComboBox<String> vozila;
	@FXML private TableView<Vozilo> tableView;
	@FXML private TableColumn<Vozilo, String> nazivColumn;
	@FXML private TableColumn<Vozilo, String> registarskiBrojColumn;
	@FXML private TableColumn<Vozilo, String> brojMotoraColumn;
	@FXML private TableColumn<Vozilo, String> brojSasijeColumn;
	@FXML private Button korisnickaApikacijaButton;
	@FXML private TextArea text;
	@FXML private ComboBox<String> comboBox;
	@FXML private ImageView imageView;
   
	private File file;
	
	private static Garaza garaza;
	private String platformeComboBoxSelectedItem;
	private static int n;
	
	@FXML private void initialize()
	{
		file = new File("garaza.ser");
		if(file.exists())
		{
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				garaza = (Garaza) ois.readObject();
				try(BufferedReader reader = new BufferedReader(new FileReader("properties.txt"))){
					int brojPlatformi = Integer.parseInt(reader.readLine());
					if(garaza.getBrojPlatformi() != brojPlatformi)
						garaza = new Garaza();
				}
			} catch(NumberFormatException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			} catch (FileNotFoundException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			} catch (ClassNotFoundException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
		else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			} finally {
				garaza = new Garaza();
			}
		}
		vozila.getItems().addAll("Automobil", "Kombi", "Motocikl", "Policijski automobil", "Policijski kombi", "Policijski motocikl", "Sanitetski automobil", "Sanitetski kombi", "Vatrogasni kombi");
		n = garaza.getBrojPlatformi();
		
		for(int i=0; i<n; ++i)
			comboBox.getItems().add((i+1)+". platforma");
		nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
		registarskiBrojColumn.setCellValueFactory(new PropertyValueFactory<>("registarskiBroj"));
		brojMotoraColumn.setCellValueFactory(new PropertyValueFactory<>("brojMotora"));
		brojSasijeColumn.setCellValueFactory(new PropertyValueFactory<>("brojSasije"));
	}
	
	@FXML private void dodajNovoVoziloButtonAction(){
		if(vozila.getSelectionModel().getSelectedItem()!=null) {
			NovoVoziloController.value = vozila.getSelectionModel().getSelectedItem();
			try {
				loadNewStage("Novo vozilo - "+NovoVoziloController.value, "view"+File.separator+"NovoVozilo.fxml");
				if(NovoVoziloController.getVozilo()!=null) {
					garaza.add(NovoVoziloController.getVozilo());
					if(platformeComboBoxSelectedItem!=null) {
						refreshTableAndTextArea(platformeComboBoxSelectedItem);
					}
				}		
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
	}
	
	static void loadNewStage(String title, String path) throws IOException {	
		
		GridPane root = (GridPane)FXMLLoader.load(Main.class.getResource(path));
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(title);
		stage.getIcons().add(new Image(new FileInputStream("icons"+File.separator+"parking.png")));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(Main.class.getResource("style"+File.separator+"application.css").toExternalForm());
		stage.setScene(scene);
		stage.setMaxHeight(650);
		stage.setMaxWidth(550);
		stage.showAndWait();
	}
	
	@FXML private void comboBoxAction() {
		String selectedItem = comboBox.getSelectionModel().getSelectedItem();
		refreshTableAndTextArea(selectedItem);
		imageView.setImage(null);
		platformeComboBoxSelectedItem = selectedItem;
	}
	
	@FXML private void selectedItemAction() {
		Vozilo selectedItem = tableView.getSelectionModel().getSelectedItem();
	
		if(selectedItem != null) {
			try {
				imageView.setImage(new Image(new FileInputStream(selectedItem.getFotografija())));
			} catch (FileNotFoundException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
	}
	
	private void refreshTableAndTextArea(String selectedItem)
	{
		for(int i=0, flag=1; flag!=0 && i<n; ++i)
		{
			if(selectedItem.equals((i+1)+". platforma"))
			{
				text.setText(garaza.getPlatforme()[i].toString());
				tableView.getItems().clear();
				tableView.getItems().addAll(garaza.getPlatforme()[i].getVozila());
				flag = 0;
			}
				
		}
	}
	
	@FXML private void obrisiButtonAction() {
		Vozilo v = tableView.getSelectionModel().getSelectedItem();
		if(v!=null) {
			int platforma = trenutnaPlatforma();
			if(platforma!=-1) {
				garaza.removeVozilo(v, platforma);
				text.setText(garaza.getPlatforme()[platforma].toString());
			}
			tableView.getItems().remove(v);
			imageView.setImage(null);
		}
	}
	
	@FXML private void izmijeniButtonAction() {
		Vozilo oldVozilo = tableView.getSelectionModel().getSelectedItem();
		if(oldVozilo != null) {
			try {
				NovoVoziloController.izmijeni = true;
				NovoVoziloController.oldVozilo = oldVozilo;
				loadNewStage("Izmijeni vozilo - "+oldVozilo.getClass().getSimpleName(), "view"+File.separator+"NovoVozilo.fxml");
				int platforma = trenutnaPlatforma();
				if(platforma!=-1 && NovoVoziloController.getVozilo()!=null) {
					garaza.replace(oldVozilo, platforma, NovoVoziloController.getVozilo());
					refreshTableAndTextArea(platformeComboBoxSelectedItem);
				}
				selectedItemAction();
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
			
		}
	}
	
	private int trenutnaPlatforma() {
		for(int i=0; i<n; ++i) {
			if(platformeComboBoxSelectedItem.contains(""+(i+1))) {
				return i;
			}
		}
		return -1;
	}
	
	public static Garaza getGaraza() {
		return garaza;
	}
	
	@FXML private void automobilMenuItemAction() {
	    	vozila.setValue("Automobil");
	    	dodajNovoVoziloButtonAction();
	    }
	    
	@FXML private void kombiMenuItemAction() {
	    	vozila.setValue("Kombi");
	    	dodajNovoVoziloButtonAction();
	    }

	@FXML private void motociklMenuItemAction() {
	    	vozila.setValue("Motocikl");
	    	dodajNovoVoziloButtonAction();
	    }
	
	@FXML private void policijskiAutomobilMenuItemAction() {
	    	vozila.setValue("Policijski automobil");
	    	dodajNovoVoziloButtonAction();
	    }
	 
	@FXML private void policijskiKombiMenuItemAction() {
	    	vozila.setValue("Policijski kombi");
	    	dodajNovoVoziloButtonAction();
	    }

	@FXML private void policijskiMotociklMenuItemAction() {
	    	vozila.setValue("Policijski motocikl");
	    	dodajNovoVoziloButtonAction();
	    }
	  
	@FXML private void sanitetskiAutomobilMenuItem() {
	    	vozila.setValue("Sanitetski automobil");
	    	dodajNovoVoziloButtonAction();
	    }
	
	@FXML private void sanitetskiKombiMenuItemAction() {
	    	vozila.setValue("Sanitetski kombi");
	    	dodajNovoVoziloButtonAction();
	    }
	
	@FXML private void vatrogasniKombiMenuItemAction() {
	    	vozila.setValue("Vatrogasni kombi");
	    	dodajNovoVoziloButtonAction();
	    }
	
	@FXML private void izadjiMenuItemAction() {
		((Stage)korisnickaApikacijaButton.getScene().getWindow()).close();
	}
	
	public static void serialize(Garaza garaza) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("garaza.ser"))){
			oos.writeObject(garaza);
		} catch (FileNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
	
	@FXML private void korisnickaAplikacijaButtonAction() {
		serialize(garaza);
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(Main.class.getResource("view"+File.separator+"Korisnik.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Garaža - Korisnik");
			stage.getIcons().add(new Image(new FileInputStream("icons"+File.separator+"parking.png")));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(Main.class.getResource("style"+File.separator+"application.css").toExternalForm());
			stage.setScene(scene);
			stage.setOnCloseRequest(e -> serialize(KorisnikController.garaza));
			stage.show();
			((Stage)korisnickaApikacijaButton.getScene().getWindow()).close();
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
}