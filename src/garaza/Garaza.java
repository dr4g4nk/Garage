package garaza;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import vozila.Automobil;
import vozila.Kombi;
import vozila.Motocikl;
import vozila.Vozilo;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.logging.Level;
import util.LoggerWrapper;


public class Garaza implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6762418065621439759L;
	private Platforma[] platforme;
	private int brojPlatformi;
	private Random r;
	private Hashtable<String, Long> evidencijaUlaska;
	private File evidencijaNaplateParkinga;
	
	public Garaza() {
		try(BufferedReader br = new BufferedReader(new FileReader(new File("properties.txt")))) {
			brojPlatformi = Integer.parseInt(br.readLine());
		} catch(FileNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		} catch(NumberFormatException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
		platforme = new Platforma[brojPlatformi];
		for(int i=0; i<brojPlatformi; ++i)
			platforme[i] = new Platforma();
		
		r = new Random();
		evidencijaUlaska = new Hashtable<>();
		evidencijaNaplateParkinga = new File("Evidencija naplate parkinga.csv");
		try(PrintWriter writer = new PrintWriter(new FileWriter(evidencijaNaplateParkinga))) {
			writer.println("Redni broj,Registarski broj,Vrijeme ulaska,Vrijeme izlaska,Cijena u KM");
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
	
	public boolean add(Vozilo v) {
		if(v instanceof Automobil || v instanceof Kombi || v instanceof Motocikl)
			evidencijaUlaska.put(v.getRegistarskiBroj(), System.currentTimeMillis());
		v.setUlaz(false);
		int i = r.nextInt(brojPlatformi);
		
		while(platforme[i].isFull()) {
			i = r.nextInt(brojPlatformi);
		}
		return platforme[i].add(v);
	}
	
	public void removeVozilo(Vozilo v, int platforma) {
		evidencijaUlaska.remove(v.getRegistarskiBroj());
		platforme[platforma].remove(v);
	}
	public int[] findVozilo(Vozilo v){
		if(v!=null) {
			for(int i=0; i<brojPlatformi; ++i) {
				Object[][] platforma = platforme[i].getPlatforma();
				for(int j=0; j<10; ++j)
					for(int k = 0; k<8; ++k)
					{
						if(platforma[j][k] instanceof Vozilo && v.equals(platforma[j][k])) {
							return new int[] {i, j, k};
						}
					}
			}
		}
		return null;
	}
	
	public void replace(Vozilo oldVozilo, int platforma, Vozilo newVozilo) {
		Long tmp = evidencijaUlaska.get(oldVozilo.getRegistarskiBroj());
		evidencijaUlaska.remove(oldVozilo.getRegistarskiBroj());
		evidencijaUlaska.put(newVozilo.getRegistarskiBroj(), tmp);
		platforme[platforma].replace(oldVozilo, newVozilo);
	}

	public Platforma[] getPlatforme() {
		return platforme;
	}
	
	public int getBrojPlatformi() {
		return brojPlatformi;
	}
	
	public Hashtable<String, Long> getEvidencijaUlaska() {
		return evidencijaUlaska;
	}

	public int getBrojVozila() {
		int num = 0;
		for(int i=0; i<brojPlatformi; ++i)
			num += platforme[i].getBrojVozila();
		return num;
	}
	
	public void pokreniVozila() {
		for(int i=0; i<brojPlatformi; ++i) {
			platforme[i].pokreniVozila();
		}
	}
	
	public void pokreniSvaVozila() {
		platforme[0].pokreniSvaVozila();
	}
	
	public boolean isFull() {
		for(int i=0; i<brojPlatformi; ++i) {
			if(!platforme[i].isFull())
				return false;
		}
		return true;
	}
	public int capacity() {
		return brojPlatformi*28;
	}
	
	private static int redniBroj = 1;
	
	public synchronized void naplataParkinga(Vozilo v) {
		long vrijemeIzlaska = System.currentTimeMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
		String line = redniBroj+","+
		v.getRegistarskiBroj()+","
				+dateFormat.format(new Date(
						evidencijaUlaska.get(
						v.getRegistarskiBroj())))+
				","+dateFormat.format(new Date(vrijemeIzlaska))+",";
		redniBroj++;
		long vrijemeZadrzavanjaMillis = vrijemeIzlaska -  evidencijaUlaska.get(v.getRegistarskiBroj());
		long vrijemeZadrzavanja = vrijemeZadrzavanjaMillis/(3600*1000);
		line += vrijemeZadrzavanja  <= 1 ? 1 : vrijemeZadrzavanja <= 3 ? 2 : 8;
		
		evidencijaUlaska.remove(v.getRegistarskiBroj());
		
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(evidencijaNaplateParkinga, true));
			writer.println(line);
			writer.close();
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
}