package application.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import garaza.Garaza;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import util.LoggerWrapper;
import vozila.*;
import vozila.policijska_vozila.*;
import vozila.sanitetska_vozila.*;
import vozila.vatrogasna_vozila.*;

public class KorisnikController {
	
	@FXML private SplitMenuButton menuButton;
	@FXML private ComboBox<String> platformeComboBox;
	@FXML private Button pokreniSimulacijuButton;
	@FXML private TextArea textArea;
	@FXML private TextField brojVozilaTextField;
	@FXML private Label msgLabel;
	
	public static Garaza garaza;
	private int brojVozila;
	private int n;
	private boolean load;
	private String selectedItem;
	public static ConcurrentLinkedQueue<Vozilo> pokrenutaVozila;
	
	@FXML private void initialize() {
		pokrenutaVozila = new ConcurrentLinkedQueue<Vozilo>();
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("garaza.ser"))) {
			garaza = (Garaza) ois.readObject();
			ois.close();
			
			n = garaza.getBrojPlatformi();
			
			for(int i=0; i<n; ++i)
				platformeComboBox.getItems().add((i+1)+". platforma");
	/*	
			garaza.getPlatforme()[1].getPlatforma()[1][6] = new Automobil();
		//	garaza.getPlatforme()[1].getPlatforma()[1][2] = new Automobil();
			garaza.getPlatforme()[1].getPlatforma()[0][6] = new Motocikl();
			
			garaza.getPlatforme()[1].getPlatforma()[7][3] = new PolicijskiAutomobil();
			garaza.getPlatforme()[1].getVozila().add((Vozilo)garaza.getPlatforme()[1].getPlatforma()[7][3]);
			
			//garaza.getPlatforme()[1].getPlatforma()[0][3] = new Automobil();
			//garaza.getPlatforme()[1].getPlatforma()[1][3] = new Automobil();
		/*	
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[1][2]).setIJK(0, 1, 2);
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[2][2]).setIJK(0, 2, 2);
			garaza.getPlatforme()[1].setPlatformaBlokirana(true);
			/*garaza.getPlatforme()[1].getPlatforma()[0][3] = new Automobil();
			garaza.getPlatforme()[2].getPlatforma()[0][4] = new Automobil();
			garaza.getPlatforme()[2].getPlatforma()[0][5] = new Automobil();
			garaza.getPlatforme()[1].getPlatforma()[7][3] = new PolicijskiAutomobil();
			garaza.getPlatforme()[1].getVozila().add((Vozilo)garaza.getPlatforme()[1].getPlatforma()[7][3]);
			
			garaza.getPlatforme()[0].setPlatformaBlokirana(true);
		//	garaza.getPlatforme()[1].setPlatformaBlokirana(true);
/*
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[1][6]).setIJK(1, 1, 6);
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[1][2]).start();
			
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[0][7]).setUlaz(false);
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[0][7]).start();
					
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[2][6]).setIJK(1, 2, 6);
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[0][6]).start();
			
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[1][3]).setIJK(0, 1, 3);
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[1][6]).start();
			
		//	((Vozilo)garaza.getPlatforme()[0].getPlatforma()[2][6]).setIJK(0, 2, 6);
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[2][6]).start();
			
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[0][2]).setUlaz(false);
			//((Vozilo)garaza.getPlatforme()[0].getPlatforma()[2][2]).start();
			
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[1][3]).setUlaz(false);
			((Vozilo)garaza.getPlatforme()[1].getPlatforma()[0][4]).setUlaz(false);
		*/
			
		} catch (FileNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			ErrorWindowController.errorWindow("Greska: Fajl garaž.ser ne postoji.", "Greška");
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		} catch (ClassNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
	
	private void generateVozila() {
		Random rand = new Random();
		int p, num = brojVozila>garaza.capacity() ? garaza.capacity()-garaza.getBrojVozila() : brojVozila-garaza.getBrojVozila();
		for(int i=0; i<num; ++i) {
			p = rand.nextInt(100);
			if(p>=10) {
				int tip = rand.nextInt(3);
				if(tip == 0) {
					garaza.add(new Automobil());
				}
				else if(tip == 1) {
					garaza.add(new Kombi());
				}
				else {
					garaza.add(new Motocikl());
				}
				
			}
			else if(p<10) {
				int tip = rand.nextInt(3);
				if(tip == 0) {
					int t = rand.nextInt(3);
					if(t == 0) {
						garaza.add(new PolicijskiAutomobil());
					}
					else if(t == 1) {
						garaza.add(new PolicijskiKombi());
					}
					else {
						garaza.add(new PolicijskiMotocikl());
					}
				}
				else if(tip == 1) {
					int t = rand.nextInt(2);
					if(t == 0) {
						garaza.add(new SanitetskiAutomobil());
					}
					else {
						garaza.add(new SanitetskiKombi());
					}
				}
				else {
					garaza.add(new VatrogasniKombi());
				}
			}
		}
	}
	
	@FXML private void platformeComboBoxAction() {
		selectedItem = platformeComboBox.getSelectionModel().getSelectedItem();
		refreshTextArea(platformeComboBox.getSelectionModel().getSelectedItem());
	}
	
	private synchronized void refreshTextArea(String selectedItem)
	{
		if(selectedItem!=null) {
			for(int i=0, flag=1; flag!=0 && i<n; ++i)
			{
				if(selectedItem.equals((i+1)+". platforma"))
				{
					textArea.setText(garaza.getPlatforme()[i].toString());
					flag = 0;
				}
			}
		}
	}

	  @FXML void newAutomobilAction() {
		  loadNovoVozilo("Automobil");
	  }
	  
	  @FXML  void newKombiAction() {
		  loadNovoVozilo("Kombi");
	  }

	  @FXML void newMotociklAction() {
		  loadNovoVozilo("Motocikl");
	  }
	  
	  @FXML void newPolicijskiAutomobilAction() {
		  loadNovoVozilo("Policijski automobil");
	  }
	  
	  @FXML void newPolicijskiKombiAction() {
		  loadNovoVozilo("Policijski kombi");
	  }

	  @FXML void newPolicijskiMotociklAction() {
		  loadNovoVozilo("Policijski motocikl");
	  }

	  @FXML void newSanitetskiAutomobilAction() {
		  loadNovoVozilo("Sanitetski automobil");
	  }

	  @FXML void newSanitetskiKombiAction() {
		  loadNovoVozilo("Sanitetski kombi");
	  }

	  @FXML  void newVatrogasniKombiAction() {
		  loadNovoVozilo("Vatrogasni kombi");
	  }
	  
	  private void loadNovoVozilo(String tipVozila) {
		  try {
			  load = true;
			  NovoVoziloController.value = tipVozila;
			  AdminController.loadNewStage("Novo vozilo - "+tipVozila, "view"+File.separator+"NovoVozilo.fxml");
			  if(NovoVoziloController.getVozilo()!=null) {
				  pokrenutaVozila.add(NovoVoziloController.getVozilo());
				  NovoVoziloController.getVozilo().start();
			  }
			  load = false;
		  } catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	  }
	  
	  @FXML private void pokreniSimulacijuButtonAction() {
		  try {
			  brojVozila = Integer.parseInt(brojVozilaTextField.getText());
			  msgLabel.setVisible(false);
			  if(brojVozila>garaza.getBrojVozila())
				  generateVozila();
			  pokreniSimulacijuButton.setDisable(true);
			  Thread thread = new Thread() {
				  public void run() {
					  while(pokreniSimulacijuButton.isDisabled()) {
						  try {
							  sleep(100);
						  } catch (InterruptedException e) {
							  LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
						  }
						  refreshTextArea(selectedItem);
					  }
				  }
			  };
			  thread.start();
			  menuButton.setVisible(true);
			  //((Vozilo)garaza.getPlatforme()[0].getPlatforma()[2][6]).findPHF(0, 1, 6, (Vozilo)garaza.getPlatforme()[0].getPlatforma()[1][6]);
		//	  ((Vozilo)garaza.getPlatforme()[1].getPlatforma()[1][6]).findPHF(1, 0, 6, (Vozilo)garaza.getPlatforme()[1].getPlatforma()[0][6]);
			  garaza.pokreniVozila();
			// garaza.pokreniSvaVozila();
			  Thread msg = new Thread() {
				  public void run() {
					  boolean flag = true;
					  while(flag) {
						  if(pokrenutaVozila.stream().noneMatch(e -> e.isAlive()) && !load)
							  flag = false;
						  else {
							  try {
								  sleep(5000);
							  } catch(InterruptedException e) {
								  LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							  }
						  }
					  }
					  pokreniSimulacijuButton.setDisable(false);
					  msgLabel.setVisible(true);
					  menuButton.setVisible(false);
					  pokrenutaVozila.clear();
					  Vozilo.sudarZabranjen = false;
				  }
			  };
			  msg.start();
		  } catch(NumberFormatException e) {
			  LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			  ErrorWindowController.errorWindow("Nedozvoljen unos. Unesite cijeli broj.", "Greška");
			  brojVozilaTextField.setText("");
		  } catch(IllegalStateException e) {
			  LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		  }
	  }
}