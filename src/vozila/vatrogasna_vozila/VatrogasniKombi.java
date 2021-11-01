package vozila.vatrogasna_vozila;

import static application.controller.KorisnikController.garaza;
import java.io.File;
import interfaces.RotacijaInterface;
import vozila.SpecijalnoVozilo;

public class VatrogasniKombi extends SpecijalnoVozilo implements RotacijaInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4825052563456195613L;
	private double nosivost;
	
	public VatrogasniKombi() {
		super();
		setFotografija(new File("slikeVozila"+File.separator+"VatrogasniKombi.jpg"));
	}

	public VatrogasniKombi(String naziv, String brojSasije, String brojMotora, String registarskiBroj, double nosivost,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj,  fotografija);
		this.nosivost = nosivost;
	}
	
	public VatrogasniKombi(VatrogasniKombi k) {
		super(k);
		this.nosivost = k.getNosivost();
	}

	public double getNosivost() {
		return nosivost;
	}

	public void setNosivost(double nosivost) {
		this.nosivost = nosivost;
	}
	
}
