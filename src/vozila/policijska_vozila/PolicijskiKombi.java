package vozila.policijska_vozila;

import java.io.File;
import util.Util;

public class PolicijskiKombi extends PolicijskoVozilo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6029594700066154202L;
	private double nosivost;
	
	public PolicijskiKombi() {
		setFotografija(new File("slikeVozila"+File.separator+"Policijski kombi.jpg"));
		nosivost = Util.rand.nextDouble();
	}

	public PolicijskiKombi(String naziv, String brojSasije, String brojMotora, String registarskiBroj, double nosivost,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
		this.nosivost = nosivost;
	}
	
	public PolicijskiKombi(PolicijskiKombi k) {
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
