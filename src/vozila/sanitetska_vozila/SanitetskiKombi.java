package vozila.sanitetska_vozila;

import java.io.File;
import util.Util;

public class SanitetskiKombi extends SanitetskoVozilo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1313733190047186658L;
	private double nosivost;
	
	public SanitetskiKombi() {
		super();
		nosivost = Util.rand.nextInt();
		setFotografija(new File("slikeVozila"+File.separator+"SanitetskiKombi.jpg"));
	}

	public SanitetskiKombi(String naziv, String brojSasije, String brojMotora, String registarskiBroj, double nosivost,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
		this.nosivost = nosivost;
	}
	
	public SanitetskiKombi(SanitetskiKombi k) {
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
