package util;

import java.io.Serializable;
import java.util.logging.Level;

public class Barijera implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int number;
	private int cekaNaBarijeri;
	
	public Barijera(int num) {
		number = num;
		cekaNaBarijeri = 0;
	}
	
	public synchronized void dostigaoBarijeru() {
		cekaNaBarijeri++;
		if(cekaNaBarijeri==number) {
			cekaNaBarijeri = 0;
			notifyAll();
		}
		else {
			try {
				wait();
			} catch (InterruptedException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
	}
}
