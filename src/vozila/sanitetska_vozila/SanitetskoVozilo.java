package vozila.sanitetska_vozila;

import static application.controller.KorisnikController.garaza;
import java.io.File;
import vozila.SpecijalnoVozilo;

public abstract class SanitetskoVozilo extends SpecijalnoVozilo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6078890331513229997L;
	
	public SanitetskoVozilo() {
		super();
	}

	public SanitetskoVozilo(String naziv, String brojSasije, String brojMotora, String registarskiBroj,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
	}
	
	public SanitetskoVozilo(SanitetskoVozilo v) {
		super(v);
	}
}
