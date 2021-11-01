package vozila;

import java.io.File;
import java.util.Random;



public class Automobil extends Vozilo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1177591136203558493L;
	private int brojVrata;
	
	public Automobil() {
		Random r = new Random();
		brojVrata = r.nextInt(100) < 50 ? 3 : 5 ;
		setFotografija(new File("slikeVozila"+File.separator+"Automobil.jpg"));
	}

	public Automobil(String naziv, String brojSasije, String brojMotora, String registarskiBroj, int brojVrata, File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
		
		this.brojVrata = brojVrata;
	}
	
	public Automobil(Automobil a) {
		super(a);
		this.brojVrata = a.getBrojVrata();
	}

	public int getBrojVrata() {
		return brojVrata;
	}
	
	public void setBrojVrata(int brojVrata)
	{
		this.brojVrata = brojVrata;
	}
}
