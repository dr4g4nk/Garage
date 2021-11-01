package vozila.sanitetska_vozila;

import java.io.File;


public class SanitetskiAutomobil extends SanitetskoVozilo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5495706163557974673L;
	private int brojVrata;
	
	public SanitetskiAutomobil() {
		super();
		setFotografija(new File("slikeVozila"+File.separator+"SanitetskiAutomobil.jpg"));
		brojVrata = 5;
	}

	public SanitetskiAutomobil(String naziv, String brojSasije, String brojMotora, String registarskiBroj, int brojVrata,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
		this.brojVrata = brojVrata;
	}
	
	public SanitetskiAutomobil(SanitetskiAutomobil a) {
		super(a);
		this.brojVrata = a.getBrojVrata();
	}
	
	public int getBrojVrata()
	{
		return brojVrata;
	}
	
	public void setBrojVrata(int brojVrata)
	{
		this.brojVrata = brojVrata;
	}
}
