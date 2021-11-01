package vozila.policijska_vozila;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Stream;
import interfaces.RotacijaInterface;
import util.LoggerWrapper;
import vozila.SpecijalnoVozilo;

public abstract class PolicijskoVozilo extends SpecijalnoVozilo implements RotacijaInterface {
	private static final long serialVersionUID = 49322415490528059L;
	private boolean zauzeto;
	private static File potjernice = new File("potjernice.txt");
	private static File uvidjaji = new File(System.getProperty("user.home")+File.separator+"uvidjaji");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
	
	public PolicijskoVozilo() {
		super();
		if(!potjernice.exists()) {
			try {
				potjernice.createNewFile();
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
		if(!uvidjaji.exists()) {
			try {
				uvidjaji.createNewFile();
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
	}

	public PolicijskoVozilo(String naziv, String brojSasije, String brojMotora, String registartskiBroj,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registartskiBroj, fotografija);
		if(!potjernice.exists()) {
			try {
				potjernice.createNewFile();
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
		if(!uvidjaji.exists()) {
			try {
				uvidjaji.createNewFile();
			} catch (IOException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
	}
	
	public PolicijskoVozilo(PolicijskoVozilo v) {
		super(v);
		this.zauzeto = v.isZauzeto();
	}
	
	public boolean isZauzeto() {
		return zauzeto;
	}

	public void setZauzeto(boolean zauzeto) {
		this.zauzeto = zauzeto;
	}
	


	public boolean provjeriRegistarskiBroj(String registarskiBroj) {
		try (BufferedReader reader = new BufferedReader(new FileReader (potjernice))){
			if(reader.lines().anyMatch(e -> e.equals(registarskiBroj)))
					return true;
		} catch (FileNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
		return false;
	}
	
	public synchronized boolean pregledajPotjernice(String registarskiBroj) {
		try (BufferedReader reader = new BufferedReader(new FileReader (potjernice))){
			reader.mark(1);
			if(reader.lines().anyMatch(e -> e.equals(registarskiBroj))){
				reader.reset();
				Stream<String> stream = reader.lines().filter(e -> !e.equals(registarskiBroj));
				try(PrintWriter writer = new PrintWriter(new FileWriter(potjernice))){
					stream.forEach(e -> writer.println(e));
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		} catch (IOException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
		
		return false;
	}
	
	public synchronized void evidentirajVozila(String msg, Object...vozila) {
		try(PrintStream writer = new PrintStream(new FileOutputStream(uvidjaji, true))){
			writer.println("Vrijeme: "+dateFormat.format(new Date()));
			writer.println(msg);
			for(Object v : vozila)
				writer.println(v.toString());
		} catch(FileNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
	
	public void evidentirajSudar() {
		evidentirajVozila("Vozila koja su ucestvovala u nesreci:", udarenaVozila.toArray());
		try {
			sleep(new Random().nextInt(7000) + 3000);
		} catch(InterruptedException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
}