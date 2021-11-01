package vozila;

import java.io.File;

public class Motocikl extends Vozilo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2819862554957669594L;

	public Motocikl() {
		setFotografija(new File("slikeVozila"+File.separator+"Motocikl.jpg"));
	}
	
	public Motocikl(String naziv, String brojSasije, String brojMotora, String registarskiBroj, File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
	}
	
	public Motocikl(Motocikl m) {
		super(m);
	}

}
