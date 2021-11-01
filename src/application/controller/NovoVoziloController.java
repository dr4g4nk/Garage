package application.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.io.FileInputStream;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import util.LoggerWrapper;
import javafx.stage.Stage;
import vozila.Automobil;
import vozila.Kombi;
import vozila.Motocikl;
import vozila.Vozilo;
import vozila.policijska_vozila.PolicijskiAutomobil;
import vozila.policijska_vozila.PolicijskiKombi;
import vozila.policijska_vozila.PolicijskiMotocikl;
import vozila.policijska_vozila.PolicijskoVozilo;
import vozila.sanitetska_vozila.SanitetskiAutomobil;
import vozila.sanitetska_vozila.SanitetskiKombi;
import vozila.sanitetska_vozila.SanitetskoVozilo;
import vozila.vatrogasna_vozila.VatrogasniKombi;


public class NovoVoziloController {
	@FXML private Button dodajButton;
	@FXML private Button izadjiButton;
	@FXML private ButtonBar buttonBar;
	@FXML private Label brojVrataLabel;
	@FXML private Label nosivostLabel;
	@FXML private TextField nazivTextField;
	@FXML private TextField registarskiBrojTextField;
	@FXML private TextField brojSasijeTextField;
	@FXML private TextField brojMotoraTextField;
	@FXML private TextField textField;
	@FXML private Label fotografijaLabel;
	@FXML private Button traziButton;
	@FXML private ImageView imageView;

	private String naziv, registarskiBroj, brojSasije, brojMotora;
	private int brojVrata;
	private double nosivost;
	private File fotografija;
	private static Vozilo vozilo;
	static Vozilo oldVozilo;
	static String value;
	static boolean izmijeni = false;

	@FXML private void initialize() {
		if(izmijeni && oldVozilo!=null) {
			value = oldVozilo.getClass().getSimpleName();
			nazivTextField.setText(oldVozilo.getNaziv());
			registarskiBrojTextField.setText(oldVozilo.getRegistarskiBroj());
			brojSasijeTextField.setText(oldVozilo.getBrojSasije());
			brojMotoraTextField.setText(oldVozilo.getBrojMotora());
			fotografija = oldVozilo.getFotografija();
			dodajButton.setText("Sacuvaj");
			try {
				imageView.setImage(new Image(new FileInputStream(oldVozilo.getFotografija())));
			} catch (FileNotFoundException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
		
		if(value.contains("utomobil") || value.contains("ombi")) {
			GridPane gridPane = (GridPane)fotografijaLabel.getParent();
			VBox vBox = new VBox();
			gridPane.add(vBox, 1, 5);
			Label label = new Label("* Unesite broj");
			label.getStyleClass().add("label-notes");
			textField = new TextField();
			vBox.getChildren().addAll(label, textField);
			
			if(value.contains("utomobil")) {
				brojVrataLabel = new Label("Broj vrata");
				gridPane.add(brojVrataLabel, 0, 5);
				if(izmijeni && oldVozilo!=null) {
					if(oldVozilo instanceof PolicijskoVozilo) {
						textField.setText(((PolicijskiAutomobil)oldVozilo).getBrojVrata()+"");
					}	
					else if(oldVozilo instanceof SanitetskoVozilo) {
						textField.setText(((SanitetskiAutomobil)oldVozilo).getBrojVrata()+"");
					}
					else if(oldVozilo instanceof Automobil) {
						textField.setText(((Automobil)oldVozilo).getBrojVrata()+"");
					}
				}
			}
			else {
				nosivostLabel = new Label("Nosivost");
				gridPane.add(nosivostLabel, 0, 5);
				Label kg = new Label(" kg");
				gridPane.add(kg,  2, 5);
				
				if(izmijeni && oldVozilo!=null) {
					if(oldVozilo instanceof PolicijskoVozilo) {
						textField.setText(((PolicijskiKombi)oldVozilo).getNosivost()+"");
					}
					else if(oldVozilo instanceof SanitetskoVozilo) {
						textField.setText(((SanitetskiKombi)oldVozilo).getNosivost()+"");
					}
					else if(oldVozilo instanceof VatrogasniKombi) {
							textField.setText(((VatrogasniKombi)oldVozilo).getNosivost()+"");
					}
					else if(oldVozilo instanceof Kombi) {
						textField.setText(((Kombi)oldVozilo).getNosivost()+"");
					}
				}
			}
			moveFotografijaLabelAndTraziButton();
			buttonBar.getButtons().removeAll(izadjiButton, dodajButton);
			buttonBar.getButtons().addAll(dodajButton, izadjiButton);
		}
		
		vozilo = null;
	}
	
	private void moveFotografijaLabelAndTraziButton() {
		GridPane gridPane = (GridPane)fotografijaLabel.getParent();
		gridPane.getChildren().removeAll(fotografijaLabel, traziButton);
		gridPane.add(fotografijaLabel, 0, 6);
		gridPane.add(traziButton, 1, 6);
	}
	
	private void inputWithoutWhiteSpace(String msg, TextField text) {
		ErrorWindowController.errorWindow(msg, "Greška");
		text.setText("");
    }
	
	private void registarskiBrojTextFieldError() {
		inputWithoutWhiteSpace("Polje Registarski broj ne smije sadržavati razmak između karaktera.", registarskiBrojTextField);
	}
	
	private void brojSasijeTextFieldError() {
		inputWithoutWhiteSpace("Polje Broj šasije ne smije sadržavati razmak između karaktera.", brojSasijeTextField);
	}
	
	private void brojMotoraTextFieldError() {
		inputWithoutWhiteSpace("Polje Broj motora ne smije sadržavati razmak između karaktera.", brojMotoraTextField);
	}
	
	@FXML public void dodajButtonAction() {
		vozilo = null;
		
		naziv = nazivTextField.getText();
		registarskiBroj = registarskiBrojTextField.getText();
		brojSasije = brojSasijeTextField.getText();
		brojMotora = brojMotoraTextField.getText();
	
		boolean dodaj = true;
		if(brojVrataLabel!=null) {
			try {
				brojVrata = Integer.parseInt(textField.getText());
			} catch(NumberFormatException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				ErrorWindowController.errorWindow("Nepravilan unos za broj vrata, potrebno je unijeti cijeli broj.", "Greška");
				dodaj = false;
				textField.setText("");
			}
		}
		else if(nosivostLabel!=null)
		{
			try {
				nosivost = Double.parseDouble(textField.getText());
			} catch(NumberFormatException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				ErrorWindowController.errorWindow("Nepravilan unos za nosivost, potrebno je unijeti realan broj.", "Greška");
				dodaj = false;
				textField.setText("");
			}
		}
		
		if(naziv.equals("") || registarskiBroj.equals("") || brojSasije.equals("") || brojMotora.equals("") || fotografija==null) {
			ErrorWindowController.errorWindow("Niste unijeli sve podatke.", "Greška");
			dodaj = false;
		}
		else if(registarskiBroj.contains(" ")) {
			registarskiBrojTextFieldError();
			dodaj = false;
		}
		else if(brojSasije.contains(" ")) {
			brojSasijeTextFieldError();
			dodaj = false;
		}
		else if(brojMotora.contains(" ")) {
			brojMotoraTextFieldError();
			dodaj = false;
		}
		
		if(!izmijeni && AdminController.getGaraza().getEvidencijaUlaska().containsKey(registarskiBroj)) {
			registarskiBrojTextField.setText("");
			registarskiBroj = "";
			ErrorWindowController.errorWindow("Vozilo sa unesenim registarskim brojem vec postoji. Unesite novi registarski broj.", "Greška");
			dodaj = false;
		}
		else if(dodaj){
			if(value.equals("Automobil")) {
				vozilo = new Automobil(naziv, brojSasije, brojMotora, registarskiBroj, brojVrata, fotografija);
			}
			else if(value.equals("Kombi")) {
				vozilo = new Kombi(naziv, brojSasije, brojMotora, registarskiBroj, nosivost, fotografija);
			}
			else if(value.equals("Motocikl")) {
				vozilo = new Motocikl(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
			}
			else if(value.contains("Policijski")) {
				if(value.contains("automobil")) {
					vozilo = new PolicijskiAutomobil(naziv, brojSasije, brojMotora, registarskiBroj, brojVrata, fotografija);
				}
				else if(value.contains("kombi")) {
					vozilo = new PolicijskiKombi(naziv, brojSasije, brojMotora, registarskiBroj, nosivost, fotografija);
				}
	
				else if(value.contains("motocikl")) {
					vozilo = new PolicijskiMotocikl(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
				}
			}
			else if(value.contains("Sanitetski")) {
				if(value.contains("automobil")) {
						vozilo = new SanitetskiAutomobil(naziv, brojSasije, brojMotora, registarskiBroj, brojVrata, fotografija);
					}
				else if(value.contains("kombi")) {
					vozilo = new SanitetskiKombi(naziv, brojSasije, brojMotora, registarskiBroj, nosivost, fotografija);
				}
			}
			else if(value.contains("Vatrogasni")) {
				vozilo = new VatrogasniKombi(naziv, brojSasije, brojMotora, registarskiBroj, nosivost, fotografija);
			}
	
			((Stage)dodajButton.getScene().getWindow()).close();
			izmijeni = false;
		}
	}
	@FXML public void izadjiButtonAction() {
		vozilo = null;
		((Stage)dodajButton.getScene().getWindow()).close();
		izmijeni = false;
	}
	
	@FXML public void traziButtonAction() {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		fc.getExtensionFilters().addAll(new ExtensionFilter("Fotografije", "*.jpg", "*.png", "*.jpeg"));
		fotografija = fc.showOpenDialog(null);
		if(fotografija != null) {
			try {
				imageView.setImage(new Image(new FileInputStream(fotografija)));
			} catch (FileNotFoundException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
	}
	public static Vozilo getVozilo() {
		return vozilo;
	}
}
