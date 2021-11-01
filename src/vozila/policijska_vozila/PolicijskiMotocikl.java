package vozila.policijska_vozila;

import java.io.File;

public class PolicijskiMotocikl extends PolicijskoVozilo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8062091338504471624L;

	public PolicijskiMotocikl() {
		super();
		setFotografija(new File("slikeVozila"+File.separator+"Policijski motocikl.jpg"));
	}

	public PolicijskiMotocikl(String naziv, String brojSasije, String brojMotora, String registarskiBroj,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
	}
	
	public PolicijskiMotocikl(PolicijskiMotocikl m) {
		super(m);
	}

}
