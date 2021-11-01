package vozila.policijska_vozila;

import java.io.File;

public class PolicijskiAutomobil extends PolicijskoVozilo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1227477784966755845L;
	private int brojVrata;
	public PolicijskiAutomobil() {
		super();
		setFotografija(new File("slikeVozila"+File.separator+"Policijski automobil.png"));
		brojVrata = 5;
	}

	public PolicijskiAutomobil(String naziv, String brojSasije, String brojMotora, String registarskiBroj, int brojVrata,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
		this.brojVrata = brojVrata;
	}
	
	public PolicijskiAutomobil(PolicijskiAutomobil a) {
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
