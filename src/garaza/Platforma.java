package garaza;

import vozila.*;
import vozila.policijska_vozila.*;
import vozila.sanitetska_vozila.*;
import vozila.vatrogasna_vozila.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import application.controller.KorisnikController;

public class Platforma implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734043591020447616L;
	private Object[][] platforma;
	private Random r;
	private int[] kolona = new int[] {0, 3, 4, 7};
	private int brojSlobodnihMjesta;
	private List<Vozilo> list;
	private boolean platformaBlokirana;
	public Vozilo locker = new Motocikl();
	
	public Platforma() {
		r = new Random();
		brojSlobodnihMjesta = 28;
		platforma = new Object[10][8];
		for(int i=0; i<10; ++i)
			for(int j=0; j<8; ++j)
			{
				if(i>1 && ((j==0 || j==7) || (i<8 && (j==3 || j==4))))
						platforma[i][j] = "*";
						
			}
		list = new ArrayList<>();
	}
	
	public boolean add(Vozilo v) {
		int i, j;
		if(isFull())
			return false;
		do {
			j = kolona[r.nextInt(4)];
			if(j>0 && j<7)
				i = r.nextInt(6)+2;
			else i = r.nextInt(8)+2;
		}while(!"*".equals(platforma[i][j]));
		platforma[i][j] = v;
		brojSlobodnihMjesta--;
		list.add(v);
		return true;
	}
	
	public void remove(Vozilo v) {
		int j, i;
		boolean flag = true;
		for(i=2; flag && i<10; ++i) {
			for(j=0; flag && j<8; ++j)
			{
				if(v.equals(platforma[i][j]))
				{
					platforma[i][j] = "*";
					brojSlobodnihMjesta++;
					flag = false;
				}
			}
		}
		list.remove(v);
	}
	
	public void replace(Vozilo oldVozilo, Vozilo newVozilo) {
		int i, j;
		boolean flag = true;
		for(i=2; flag && i<10; ++i) {
			for(j=0; flag && j<8;)
			{
				if(oldVozilo.equals(platforma[i][j]))
				{
					platforma[i][j] = newVozilo;
					flag = false;
				}
				else if(j==7) j=8;
				else if(j==4) j=7;
				else if(j==3) j=4;
				else if(j==0) j=3;
			}
			j=0;
		}
		int n = list.indexOf(oldVozilo);
		list.remove(n);
		list.add(n, newVozilo);
	}
	
	public boolean isFull() {
		return brojSlobodnihMjesta == 0;
	}
	
	@Override
	public String toString() {
		String str="";
		for(int i=0; i<10; str+="\n", ++i)
		{
			for(int j=0; j<8; ++j)
			{
				if(platforma[i][j] == null) {
					str += "       ";
				}
				else {
					if(platforma[i][j] instanceof Automobil || platforma[i][j] instanceof Kombi || platforma[i][j] instanceof Motocikl)
						str += "   V   ";
					else if(platforma[i][j] instanceof PolicijskoVozilo) {
						if(((PolicijskoVozilo)platforma[i][j]).isRotacija())
							str += "   PR   ";
						else
							str += "   P   ";
					}
					else if(platforma[i][j] instanceof SanitetskoVozilo) {
						if(((SanitetskoVozilo)platforma[i][j]).isRotacija())
							str += "   HR   ";
						else
							str += "   H   ";
					}
					else if(platforma[i][j] instanceof VatrogasniKombi) {
						if(((VatrogasniKombi)platforma[i][j]).isRotacija())
							str += "   FR   ";
						else 
							str += "   F   ";
					}
					else
						str += "   *   ";	
				}
				if(i>7 && j==6)
					str += "  ";
			}
		}
		return str;
	}
	
	public void decBrojSlobodnihMjesta() {
		brojSlobodnihMjesta--;
	}
	
	public void incBrojSlobodnihMjesta() {
		brojSlobodnihMjesta++;
	}
	
	public int getBrojSlobodnihMjesta() {
		return brojSlobodnihMjesta;
	}
	public void pokreniVozila() {
		int i, j;
		int num = list.size()*15/100;
		do {
			j = kolona[r.nextInt(4)];
			if(j>0 && j<7)
				i = r.nextInt(6)+2;
			else i = r.nextInt(8)+2;
			if(platforma[i][j] instanceof Vozilo && !((Vozilo)platforma[i][j]).isAlive()) {
				if(((Vozilo)platforma[i][j]).getState() == Thread.State.TERMINATED)
					resetThread(this, i, j);
				KorisnikController.pokrenutaVozila.add((Vozilo)platforma[i][j]);
				((Vozilo)platforma[i][j]).start();
				num--;
			}
		}while(num>0);
	}

	public static void resetThread(Platforma platforma, int x, int y) {
		String simpleName = ((Vozilo)platforma.getPlatforma()[x][y]).getClass().getSimpleName();
		if(simpleName.equals("Automobil")) {
			platforma.getPlatforma()[x][y] = new Automobil((Automobil)platforma.getPlatforma()[x][y]);
		}
		else if(simpleName.equals("Motocikl")) {
			platforma.getPlatforma()[x][y] = new Motocikl((Motocikl)platforma.getPlatforma()[x][y]);
		}
		else if(simpleName.equals("Kombi")) {
			platforma.getPlatforma()[x][y] = new Kombi((Kombi)platforma.getPlatforma()[x][y]);
		}
		else if(simpleName.contains("Policijski")) {
			if(simpleName.contains("Automobi")) {
				platforma.getPlatforma()[x][y] = new PolicijskiAutomobil(((PolicijskiAutomobil)platforma.getPlatforma()[x][y]));
			}
			else if(simpleName.contains("Kombi")) {
				platforma.getPlatforma()[x][y] = new PolicijskiKombi(((PolicijskiKombi)platforma.getPlatforma()[x][y]));
			}
			else	
				platforma.getPlatforma()[x][y] = new PolicijskiMotocikl(((PolicijskiMotocikl)platforma.getPlatforma()[x][y]));
		}
		else if(simpleName.contains("Sanitetski")) {
			if(simpleName.contains("Automobi")) {
				platforma.getPlatforma()[x][y] = new SanitetskiAutomobil(((SanitetskiAutomobil)platforma.getPlatforma()[x][y]));
			}
			else
				platforma.getPlatforma()[x][y] = new SanitetskiKombi(((SanitetskiKombi)platforma.getPlatforma()[x][y]));
		}
		else if(simpleName.equals("VatrogasniKombi")) {
			platforma.getPlatforma()[x][y] = new VatrogasniKombi((VatrogasniKombi)platforma.getPlatforma()[x][y]);
		}
	}
	
	public void pokreniSvaVozila() {
		for(int i=0; i<10; ++i) {
			for(int j=0; j<8; ++j) {
				if(platforma[i][j] instanceof Vozilo && !((Vozilo)platforma[i][j]).isAlive()) {
					KorisnikController.pokrenutaVozila.add((Vozilo)platforma[i][j]);
						((Vozilo)platforma[i][j]).start();
				}	
			}
		}
	}
	public List<Vozilo> getVozila() {
		return list;
	}
	
	public Object[][] getPlatforma(){
		return platforma;
	}
	
	public int getBrojVozila() {
		return list.size();
	}

	public boolean isPlatformaBlokirana() {
		return platformaBlokirana;
	}

	public void setPlatformaBlokirana(boolean platformaBlokirana) {
		this.platformaBlokirana = platformaBlokirana;
	}
}