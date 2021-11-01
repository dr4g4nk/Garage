package vozila;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Stream;
import application.controller.KorisnikController;
import garaza.Platforma;
import util.Barijera;
import util.LoggerWrapper;
import util.Util;
import vozila.policijska_vozila.PolicijskiAutomobil;
import vozila.policijska_vozila.PolicijskoVozilo;
import vozila.sanitetska_vozila.SanitetskiKombi;
import vozila.sanitetska_vozila.SanitetskoVozilo;
import vozila.vatrogasna_vozila.VatrogasniKombi;

import static application.controller.KorisnikController.garaza;

public abstract class Vozilo extends Thread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6400835028900135535L;
	private String naziv;
	private String brojSasije;
	private String brojMotora;
	private String registarskiBroj;
	private File fotografija;
	private boolean ulaz = true;
	private boolean uhvacen;
	private boolean move;
	protected int i = -1, j = -1, k = -1;
	public static boolean sudarZabranjen;
	private Random random = new Random();
	
	public void setIJK(int i, int j, int k) {
		this.i= i;
		this.j=j;
		this.k=k;
	}
	public Vozilo(){
		naziv = Util.randomNaziv();
		brojSasije = Util.randomBrojSasije();
		brojMotora = Util.randomBrojMotora();
		registarskiBroj = Util.randomRegistarskiBroj();
	}
	
	
	public Vozilo(String naziv, String brojSasije, String brojMotora, String registarskiBroj, File fotografija) {
		this.naziv = naziv;
		this.brojSasije = brojSasije;
		this.brojMotora = brojMotora;
		this.registarskiBroj = registarskiBroj;
		this.fotografija = fotografija;
	}
	
	public Vozilo(Vozilo v) {
		this.naziv = v.getNaziv();
		this.brojSasije = v.getBrojSasije();
		this.brojMotora = v.getBrojMotora();
		this.registarskiBroj = v.getRegistarskiBroj();
		this.fotografija = v.getFotografija();
		this.ulaz = v.isUlaz();
		this. uhvacen = v.isUhvacen();
	}
	
	public String getNaziv() {
		return naziv;
	}


	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}


	public String getBrojSasije() {
		return brojSasije;
	}


	public void setBrojSasije(String brojSasije) {
		this.brojSasije = brojSasije;
	}


	public String getBrojMotora() {
		return brojMotora;
	}


	public void setBrojMotora(String brojMotora) {
		this.brojMotora = brojMotora;
	}


	public String getRegistarskiBroj() {
		return registarskiBroj;
	}


	public void setRegistarskiBroj(String registarskiBroj) {
		this.registarskiBroj = registarskiBroj;
	}


	public File getFotografija() {
		return fotografija;
	}


	public void setFotografija(File fotografija) {
		this.fotografija = fotografija;
	}

	public boolean isUlaz() {
		return ulaz;
	}
	
	public void setUlaz(boolean ulaz) {
		this.ulaz = ulaz;
	}

	public boolean isUhvacen() {
		return uhvacen;
	}

	public void setUhvacen(boolean uhvacen) {
		this.uhvacen = uhvacen;
	}
	
	public void setMove() {
		move = true;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(!(obj instanceof Vozilo))
			return false;
		Vozilo tmp = (Vozilo)obj;
		return registarskiBroj.equals(tmp.getRegistarskiBroj());
	}
	
	@Override
	public String toString() {
		return "Tip: "+getClass().getSimpleName()+"\nRegistarski broj: "+ getRegistarskiBroj()+"\nFotografija: "+getFotografija()+"\n";
	}
	
	@Override
	public void run() {
		if(!uhvacen) {
			try {
				sleep(random.nextInt(3000) + random.nextInt(1000) + random.nextInt(2000) + random.nextInt(1000) + random.nextInt(1000) + random.nextInt(1000) + new Random().nextInt(3000));
			} catch (InterruptedException e) {
				LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
			}
		}
		
		boolean dalje = true;
		int[] position;
		boolean end = false;
		if(!ulaz) {
			if(!(this instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)this).isSudar())) {
				position = garaza.findVozilo(this);
				if(position != null) {
					i = position[0];
					j = position[1];
					k = position[2];
				}
			}
		}
		else {
			i = 0;
			j = 1;
			k = 0;
			if(!garaza.isFull()) {				
				if(garaza.getPlatforme()[i].isPlatformaBlokirana()) {
					synchronized(garaza.getPlatforme()[i].locker) {
						try {
							garaza.getPlatforme()[i].locker.wait();
						} catch (InterruptedException e) {
							LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
						}
					}
				}
				while(garaza.getPlatforme()[i].getPlatforma()[j][k] instanceof Vozilo) {
					try {
						sleep(800);
					} catch (InterruptedException e) {
						LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
					}
				}
				if(!(this instanceof SpecijalnoVozilo))
					garaza.getEvidencijaUlaska().put(this.getRegistarskiBroj(), System.currentTimeMillis());
				while(garaza.getPlatforme()[i].isFull()){
					while(k!=7) {
						try {
							sleep(800);
						} catch (InterruptedException e) {
							LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
						}
							moveVozilo(true, false, null);
					}
					if(k==7) {
						if(!garaza.getPlatforme()[i].isPlatformaBlokirana() && !(garaza.getPlatforme()[i+1].getPlatforma()[1][0] instanceof Vozilo)){
							garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
							++i;
							j=1;
							k=0;
						}
					}
				}
				if(!garaza.getPlatforme()[i].isFull())
					garaza.getPlatforme()[i].decBrojSlobodnihMjesta();
				garaza.getPlatforme()[i].getPlatforma()[j][k] = this;
			}
			else dalje = false;
		}
		
		while(!end) {
				try {
					sleep(800);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				if(!ulaz) {
					if(k==0 && j>1) {
							moveVozilo(true, false, "*");
					}
					else if(k==1 && (j>1 &&j<9)) {
						if(this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto()) {
							provjeriVozilo();
						}
						else 
							moveVozilo(true, true, null);
					}
					else if(j==9 && k<6) {
						if(k==1 && this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto() && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && ((PolicijskoVozilo)this).pregledajPotjernice(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).getRegistarskiBroj())) {
							((PolicijskoVozilo)this).upaliRotaciju();
							((PolicijskoVozilo)this).setZauzeto(true);
							((PolicijskoVozilo)this).evidentirajVozila("Vozilo sa potjernice: ", (Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]);
							((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).setUhvacen(true);
							try {
								sleep(random.nextInt(2000)+3000);
							} catch (InterruptedException e) {
								LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							}
							((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).start();
						}
						moveVozilo(true, false, null);
					}
					else if(k==6 && j==1) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
							moveVozilo(false, true, null);
						}
					}
					else if(k==6 && j>1) {
						if(this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto()) {
							provjeriVozilo();
						}
						else
							moveVozilo(false, true, null);
					}
					else if(j==0 && k!=0) {
						moveVozilo(false, false, null);
					}
					else if(k==3 && (j>1 &&j<8)) {
						moveVozilo(false, false, "*");
					}
					else if(k==2 && j==1) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo))
							moveVozilo(false, true, null);
					}
					else if(k==2 && j>1) {
						if(this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto()) {
							provjeriVozilo();
						}
						else
							moveVozilo(false, true,null);
					}
					else if(k==4 && (j>1 &&j<8)) {
						moveVozilo(true, false, "*");
					}
					else if(k==5 && j!=8) {
						if(this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto()) {
							provjeriVozilo();
						}
						else
							moveVozilo(true, true, null);
					}
					else if(k==7 && j>1) {
						moveVozilo(false, false, "*");
					}
					else if(j==8 && k!=2) {
						moveVozilo(false, false, null);
					}
					else if(k==0 && j==0) {
						if(!garaza.getPlatforme()[i].isPlatformaBlokirana()) {
							if(i>0) {
								if((!garaza.getPlatforme()[i-1].isPlatformaBlokirana() || (this instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)this).isSudar())) && !(garaza.getPlatforme()[i-1].getPlatforma()[0][7] instanceof Vozilo)){
									garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
									--i;
									garaza.getPlatforme()[i].getPlatforma()[j=0][k=7] = this;
								}
							}
							else {
								garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
								if(!(this instanceof SpecijalnoVozilo))
									garaza.naplataParkinga(this);
								end = true;
							}
						}
					}
				}
				else if(dalje){
					if(j==1 && k<1) {
						moveVozilo(true, false, null);
					}
					else if(j==1 && k==1) {
						moveVozilo(true, true, null);
					}
					else if(j==9 && k==1) {
						if(this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto() && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && ((PolicijskoVozilo)this).pregledajPotjernice(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).getRegistarskiBroj())) {
							((PolicijskoVozilo)this).upaliRotaciju();
							((PolicijskoVozilo)this).setZauzeto(true);
							((PolicijskoVozilo)this).evidentirajVozila("Vozilo sa potjernice: ", (Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]);
							((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).setUhvacen(true);
							try {
								sleep(random.nextInt(2000)+3000);
							} catch (InterruptedException e) {
								LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							}
							ulaz = false;
							garaza.getPlatforme()[i].incBrojSlobodnihMjesta();
							KorisnikController.pokrenutaVozila.add((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]);
							((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).start();
						}
						else if("*".equals(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
							moveVozilo(false, false, null);
							garaza.getPlatforme()[i].getVozila().add(this);
							ulaz = false;
							Platforma.resetThread(garaza.getPlatforme()[i], j, k);
							end = true;
						}
						else {
							moveVozilo(true, false, null);
						}
					}
					else if(k==6 && j>1) {
						if((this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto()) && provjeriLijevoIDesno(i, j, k)) {
							provjeriVozilo();
						}
						else if("*".equals(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
							moveVozilo(true, false, null);
							garaza.getPlatforme()[i].getVozila().add(this);
							ulaz = false;
							Platforma.resetThread(garaza.getPlatforme()[i], j, k);
							end = true;
						}
						else if("*".equals(garaza.getPlatforme()[i].getPlatforma()[j][k-2])) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, null);
								try {
									sleep(800);
								} catch (InterruptedException e) {
									LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
								}
								moveVozilo(false, false, null);
								garaza.getPlatforme()[i].getVozila().add(this);
								ulaz = false;
								Platforma.resetThread(garaza.getPlatforme()[i], j, k);
								end = true;
							}
						}
						else {
							moveVozilo(false, true, null);
						}
					}
					else if(k==6 && j==1) {
						moveVozilo(true, false, null);
					}
					else if(j==9 && k>1) {
						moveVozilo(true, false, null);
					}
					else if(j>1 && k==1) {
						if(this instanceof PolicijskoVozilo && !((PolicijskoVozilo)this).isZauzeto() && provjeriLijevoIDesno(i, j, k)) {
							provjeriVozilo();
						}
						else if("*".equals(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
							moveVozilo(false, false, null);
							garaza.getPlatforme()[i].getVozila().add(this);
							ulaz = false;
							Platforma.resetThread(garaza.getPlatforme()[i], j, k);
							end = true;
						}
						else if("*".equals(garaza.getPlatforme()[i].getPlatforma()[j][k+2])) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, null);
								try {
									sleep(800);
								} catch (InterruptedException e) {
									LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
								}
								moveVozilo(true, false, null);
								garaza.getPlatforme()[i].getVozila().add(this);
								ulaz = false;
								Platforma.resetThread(garaza.getPlatforme()[i], j, k);
								end = true;
							}
						}
						else {
							moveVozilo(true, true, null);
						}
					}
				}
			}
	}
	
	private boolean provjeriLijevoIDesno(int i, int j, int k) {
		if(k==2 || k==6)
			return (garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && ((PolicijskoVozilo)this).provjeriRegistarskiBroj(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).getRegistarskiBroj())) || 
					(garaza.getPlatforme()[i].getPlatforma()[j][k-2] instanceof Vozilo && ((PolicijskoVozilo)this).provjeriRegistarskiBroj(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-2]).getRegistarskiBroj()));
		else {
			return (garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && ((PolicijskoVozilo)this).provjeriRegistarskiBroj(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).getRegistarskiBroj())) || 
					(garaza.getPlatforme()[i].getPlatforma()[j][k+2] instanceof Vozilo && ((PolicijskoVozilo)this).provjeriRegistarskiBroj(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+2]).getRegistarskiBroj()));
		}
	}
	
	protected void provjeriVozilo() {
		if(k==1 || k==5) {
			if(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && ((PolicijskoVozilo)this).pregledajPotjernice(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).getRegistarskiBroj())) {
				((PolicijskoVozilo)this).upaliRotaciju();
				((PolicijskoVozilo)this).setZauzeto(true);
				((PolicijskoVozilo)this).evidentirajVozila("Vozilo sa potjernice: ", (Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]);
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).setUhvacen(true);
				try {
					sleep(random.nextInt(2000)+3000);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				KorisnikController.pokrenutaVozila.add(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]));
				garaza.getPlatforme()[i].incBrojSlobodnihMjesta();
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).start();
				 moveVozilo(true, true, null);
				 ulaz = false;
			}
			else if(garaza.getPlatforme()[i].getPlatforma()[j][k+2] instanceof Vozilo && ((PolicijskoVozilo)this).pregledajPotjernice(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+2]).getRegistarskiBroj())){
				((PolicijskoVozilo)this).upaliRotaciju();
				((PolicijskoVozilo)this).setZauzeto(true);
				((PolicijskoVozilo)this).evidentirajVozila("Vozilo sa potjernice: ", (Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+2]);
				moveVozilo(true, false, null);
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).setUhvacen(true);
				try {
					sleep(random.nextInt(2000)+3000);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				KorisnikController.pokrenutaVozila.add((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]);
				garaza.getPlatforme()[i].incBrojSlobodnihMjesta();
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).start();
				try {
					sleep(800);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				moveVozilo(false, true, null);
				 ulaz = false;
			}
			else 
				moveVozilo(true, true, null);
		}
		else if(k==2 || k==6) {
			if(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && ((PolicijskoVozilo)this).pregledajPotjernice(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).getRegistarskiBroj())) {
				((PolicijskoVozilo)this).upaliRotaciju();
				((PolicijskoVozilo)this).setZauzeto(true);
				((PolicijskoVozilo)this).evidentirajVozila("Vozilo sa potjernice: ", (Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]);
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).setUhvacen(true);
				try {
					sleep(random.nextInt(2000)+3000);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				KorisnikController.pokrenutaVozila.add((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]);
				garaza.getPlatforme()[i].incBrojSlobodnihMjesta();
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).start();
				 moveVozilo(false, true, null);
				 ulaz = false;
			}
			else if(garaza.getPlatforme()[i].getPlatforma()[j][k-2] instanceof Vozilo && ((PolicijskoVozilo)this).pregledajPotjernice(((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-2]).getRegistarskiBroj())){
				((PolicijskoVozilo)this).upaliRotaciju();
				((PolicijskoVozilo)this).setZauzeto(true);
				((PolicijskoVozilo)this).evidentirajVozila("Vozilo sa potjernice: ", (Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-2]);
				moveVozilo(false, false, null);
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).setUhvacen(true);
				try {
					sleep(random.nextInt(2000)+3000);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				KorisnikController.pokrenutaVozila.add((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]);
				garaza.getPlatforme()[i].incBrojSlobodnihMjesta();
				((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).start();
				try {
					sleep(800);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				moveVozilo(true, true, null);
				 ulaz = false;
			}
			else
				moveVozilo( false, true,null);
		}
	}
	public void moveVozilo(boolean inc, boolean changeFirstOp, Object obj) {
		if(!garaza.getPlatforme()[i].isPlatformaBlokirana() || (this instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)this).isSudar()) || move) {
			if(move)
				move = false;
			else
				zaustavi();
			Object v;
			if(inc) {
				if(changeFirstOp) {
					if((v = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof Vozilo) {
						if(!uhvacen && random.nextInt(100)<10 && !sudarZabranjen) {
							garaza.getPlatforme()[i].setPlatformaBlokirana(true);
							findPHF(i, j+1, k, (Vozilo)v);
						}
						else if(!uhvacen) {
							try {
								sleep(500);
							} catch(InterruptedException e) {
								LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							}
						}
					}
					else {
						if("*".equals(obj))
							garaza.getPlatforme()[i].remove(this);
						else
							garaza.getPlatforme()[i].getPlatforma()[j][k] = obj;
						garaza.getPlatforme()[i].getPlatforma()[++j][k] = this;
					}
				}
				else {
					if((v = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof Vozilo){
						if(!uhvacen && random.nextInt(100)<10 && !sudarZabranjen) {
							garaza.getPlatforme()[i].setPlatformaBlokirana(true);
							findPHF(i, j, k+1, (Vozilo)v);
						}
						else if(!uhvacen) {
							try {
								sleep(500);
							} catch(InterruptedException e) {
								LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							}
						}
					}
					else {
						if("*".equals(obj))
							garaza.getPlatforme()[i].remove(this);
						else
							garaza.getPlatforme()[i].getPlatforma()[j][k] = obj;
						
						garaza.getPlatforme()[i].getPlatforma()[j][++k] = this;
					}
				}
			}
			else {
				if(changeFirstOp) {
					if((v = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof Vozilo){
						if(!uhvacen && random.nextInt(100)<10 && !sudarZabranjen) {
							garaza.getPlatforme()[i].setPlatformaBlokirana(true);
							findPHF(i, j-1, k, (Vozilo)v);
						}
						else if(!uhvacen) {
							try  {
								sleep(500);
							} catch(InterruptedException e) {
								LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							}
						} 
					}
					else {
						if("*".equals(obj))
							garaza.getPlatforme()[i].remove(this);
						else
							garaza.getPlatforme()[i].getPlatforma()[j][k] = obj;
						
						garaza.getPlatforme()[i].getPlatforma()[--j][k] = this;
					}
				}
				else {
					if((v = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof Vozilo){
						if(!uhvacen && random.nextInt(100)<10 && !sudarZabranjen) {
							garaza.getPlatforme()[i].setPlatformaBlokirana(true);
							findPHF(i, j, k-1, (Vozilo)v);
						}
						else if(!uhvacen) {
							try {
								sleep(500);
							} catch(InterruptedException e) {
								LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
							}
						}
					}
					else {
						if("*".equals(obj))
							garaza.getPlatforme()[i].remove(this);
						else
							garaza.getPlatforme()[i].getPlatforma()[j][k] = obj;
						
						garaza.getPlatforme()[i].getPlatforma()[j][--k] = this;
					}
				}
			}
		}
		else {
			synchronized(garaza.getPlatforme()[i].locker) {
				try {
					garaza.getPlatforme()[i].locker.wait();
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
			}
		}
	}
	
	public void zaustavi() {
		if(!(this instanceof SpecijalnoVozilo && (((SpecijalnoVozilo)this).isSudar() || ((SpecijalnoVozilo)this).isEvidentiraoSudar()))) {
			boolean wait = false;
			Object sv = null;
			if((k>0 && k<7) && (j<9 && j>0)) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(j==0 && (k>0 && k<7)) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(j==0 && k==0) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(j==0 && k==7) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(j==9 && (k>0 && k<7)) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(j==9 && k==0) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(j==9 && k==7) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(k==0 && (j<9 && j>0)) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k+1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).isSudar())) {
					
					wait = true;
				}
			}
			else if(k==7 && (j<9 && j>0)) {
				if(((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]).isSudar())
						|| ((sv = garaza.getPlatforme()[i].getPlatforma()[j][k-1]) instanceof SpecijalnoVozilo && ((SpecijalnoVozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).isSudar())) {
					
					wait = true;
				}
			}
			
			if(wait) {
			/*	try {
					sleep(3000);
				} catch (InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
				*/
				if(sv!=null) {
					synchronized(sv) {
						try {
							sv.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void findPHF(int platforma, int x, int y, Vozilo v) {
		sudarZabranjen = true;
		System.out.println("SUDAR "+platforma+" "+x+" "+y);
		
		SanitetskoVozilo sVozilo = (SanitetskoVozilo) getPHFVozilo("vozila.sanitetska_vozila.SanitetskoVozilo", platforma, x, y);
		VatrogasniKombi vVozilo = (VatrogasniKombi) getPHFVozilo("vozila.vatrogasna_vozila.VatrogasniKombi", platforma, x, y);
		PolicijskoVozilo pVozilo = (PolicijskoVozilo) getPHFVozilo("vozila.policijska_vozila.PolicijskoVozilo", platforma, x, y);
		
		if(sVozilo==null) {
			sVozilo = new SanitetskiKombi();
		}
		if(vVozilo==null) {
			vVozilo = new VatrogasniKombi();
		}
		if(pVozilo==null) {
			pVozilo = new PolicijskiAutomobil();
		}
		
		Barijera barijera = new Barijera(3);
		pokreniPHF(sVozilo, platforma, x, y, v, barijera);
		pokreniPHF(vVozilo, platforma, x, y, v, barijera);
		pokreniPHF(pVozilo, platforma, x, y, v, barijera);
	}
	
	private void pokreniPHF(SpecijalnoVozilo sv, int platforma, int x, int y, Vozilo v, Barijera barijera) {
		sv.setSudar(true);
		sv.setUdarenaVozila(v, this);
		sv.setCoordinates(platforma, x, y);
		sv.setBarijera(barijera);
		KorisnikController.pokrenutaVozila.add(sv);
		sv.start();
	}
	
	private SpecijalnoVozilo getPHFVozilo(String className , int platforma, int x, int y) {
		try {
			Class<?> cls = Class.forName(className);
			boolean contain = garaza.getPlatforme()[platforma].getVozila().stream().anyMatch(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive()));
			Stream<SpecijalnoVozilo> vozila;
			
			if(contain) {
				vozila = garaza.getPlatforme()[platforma].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
				vozila.forEach (e -> {
					int[] pos = garaza.findVozilo(e);
					e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1]-x, 2)+Math.pow(pos[2]-y, 2))));
				});
				
				vozila = garaza.getPlatforme()[platforma].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
				
				return vozila.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
			}
			else {
				boolean end = false;
				for(int i=1; !end; ++i) {
					if(platforma-i>=0 && platforma+i<garaza.getBrojPlatformi()) {
						boolean b1 = garaza.getPlatforme()[platforma-i].getVozila().stream().anyMatch(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive()));
						boolean b2 = garaza.getPlatforme()[platforma+i].getVozila().stream().anyMatch(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive()));
						if(b1 && b2) {
							vozila = garaza.getPlatforme()[platforma-i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							Stream<SpecijalnoVozilo> vozila2 = garaza.getPlatforme()[platforma+i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
								
							vozila.forEach(e -> {
								int[] pos = garaza.findVozilo(e);
								e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1]-1, 2)+Math.pow(pos[2]-7, 2))) + Math.sqrt(Math.pow(x-1, 2) + Math.pow(y, 2)));
							});
							
							vozila = garaza.getPlatforme()[platforma-i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
								
							vozila2.forEach(e -> {
								int[] pos = garaza.findVozilo(e);
								e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1], 2)+Math.pow(pos[2], 2))) + Math.sqrt(Math.pow(x, 2) + Math.pow(y-7, 2)));
							});
							
							vozila2 = garaza.getPlatforme()[platforma+i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
								
							SpecijalnoVozilo p1 = vozila.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
							SpecijalnoVozilo p2 = vozila2.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
							return p1.getUdaljenostOdMjestaNesrece() > p2.getUdaljenostOdMjestaNesrece() ? p1 : p2;
						}
						else if(b1) {
							vozila = garaza.getPlatforme()[platforma-i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							vozila.forEach(e -> {
								int[] pos = garaza.findVozilo(e);
								e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1]-1, 2)+Math.pow(pos[2]-7, 2)))  + Math.sqrt(Math.pow(x-1, 2) + Math.pow(y, 2)));
							});
							
							vozila = garaza.getPlatforme()[platforma-i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							return vozila.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
						}
						else if(b2) {
							vozila = garaza.getPlatforme()[platforma+i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							
							vozila.forEach(e -> {
								int[] pos = garaza.findVozilo(e);
								e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1], 2)+Math.pow(pos[2], 2))) + Math.sqrt(Math.pow(x, 2) + Math.pow(y-7, 2)));
							});
							
							vozila = garaza.getPlatforme()[platforma+i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							return vozila.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
						}
					}
					else if(platforma-i>=0) {
						if(garaza.getPlatforme()[platforma-i].getVozila().stream().anyMatch(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive()))) {
							vozila = garaza.getPlatforme()[platforma-i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							vozila.forEach(e -> {
								int[] pos = garaza.findVozilo(e);
								e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1]-1, 2)+Math.pow(pos[2]-7, 2))) + Math.sqrt(Math.pow(x-1, 2) + Math.pow(y, 2)));
							});
							
							vozila = garaza.getPlatforme()[platforma-i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
							return vozila.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
						}
					}
					else if(platforma+i<garaza.getBrojPlatformi()) {
						if(garaza.getPlatforme()[platforma+i].getVozila().stream().anyMatch(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive()))) {
							if(garaza.getPlatforme()[platforma+i].getVozila().stream().anyMatch(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive()))) {
								vozila = garaza.getPlatforme()[platforma+i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);
								vozila.forEach(e -> {
									int[] pos = garaza.findVozilo(e);
									e.setUdaljenostOdMjestaNesrece(Math.sqrt((Math.pow(pos[1], 2)+Math.pow(pos[2], 2))) + Math.sqrt(Math.pow(x, 2) + Math.pow(y-7, 2)));
								});
								vozila = garaza.getPlatforme()[platforma+i].getVozila().stream().filter(e -> (cls.isAssignableFrom(e.getClass()) && !e.isAlive())).map(e -> (SpecijalnoVozilo)e);		
								return vozila.min(Comparator.comparing(SpecijalnoVozilo::getUdaljenostOdMjestaNesrece)).get();
							}
						}
					}
					else
						end = true;
				}
			}
		} catch (ClassNotFoundException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
		return null;
	}
}