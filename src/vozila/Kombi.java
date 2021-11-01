package vozila;

import java.io.File;
import util.Util;

public class Kombi extends Vozilo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2786340264819204752L;
	private double nosivost;
	
	
	public Kombi() {
		super();
		nosivost = Util.rand.nextDouble();
		setFotografija(new File("slikeVozila"+File.separator+"Kombi.jpg"));
	}

	public Kombi(String naziv, String brojSasije, String brojMotora, String registarskiBroj, double nosivost, File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
		
		this.nosivost = nosivost;
	}
	
	public Kombi(Kombi k) {
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
