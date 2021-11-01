package vozila;

import static application.controller.KorisnikController.garaza;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import util.Barijera;
import interfaces.RotacijaInterface;
import util.LoggerWrapper;
import java.util.logging.Level;
import vozila.Vozilo;
import vozila.policijska_vozila.PolicijskoVozilo;

public abstract class SpecijalnoVozilo extends Vozilo implements RotacijaInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean rotacija;
	protected int platforma, x, y;
	protected List<Vozilo> udarenaVozila;
	private double udaljenostOdMjestaNesrece;
	private boolean sudar;
	private boolean evidentiraoSudar;
	private boolean flag;
	private boolean endSaVisePlatforme;
	private Barijera barijera;

	public SpecijalnoVozilo() {
		super();
	}

	public SpecijalnoVozilo(String naziv, String brojSasije, String brojMotora, String registarskiBroj,
			File fotografija) {
		super(naziv, brojSasije, brojMotora, registarskiBroj, fotografija);
	}

	public SpecijalnoVozilo(SpecijalnoVozilo v) {
		super(v);
		rotacija = v.isRotacija();
		sudar = v.isSudar();
	}

	@Override
	public void upaliRotaciju() {
		rotacija = true;
	}

	@Override
	public void ugasiRotaciju() {
		rotacija = false;
	}

	public boolean isRotacija() {
		return rotacija;
	}
	
	public void setCoordinates(int platforma, int x, int y) {
		this.platforma = platforma;
		this.x = x;
		this.y = y;
	}

	public boolean isSudar() {
		return sudar;
	}

	public void setSudar(boolean sudar) {
		this.sudar = sudar;
	}
	
	public void setUdarenaVozila(Vozilo... vozila) {
		udarenaVozila = new ArrayList<>();
		for(Vozilo v : vozila)
			udarenaVozila.add(v);
	}
	

	public double getUdaljenostOdMjestaNesrece() {
		return udaljenostOdMjestaNesrece;
	}

	public void setUdaljenostOdMjestaNesrece(double udaljenostOdMjestaNesrece) {
		this.udaljenostOdMjestaNesrece = udaljenostOdMjestaNesrece;
	}

	public void setBarijera(Barijera barijera) {
		this.barijera = barijera;
	}

	public boolean isEvidentiraoSudar() {
		return evidentiraoSudar;
	}

	public void setEvidentiraoSudar(boolean evidentiraoSudar) {
		this.evidentiraoSudar = evidentiraoSudar;
	}

	@Override
	public void run() {
		if(isSudar()){
			upaliRotaciju();
			int[] pos = garaza.findVozilo(this);
			if(pos!=null) {
				i=pos[0];
				j=pos[1];
				k=pos[2];
			}
			else {
				while(garaza.getPlatforme()[0].getPlatforma()[1][0] instanceof Vozilo) {
					pauza();
				}
				garaza.getPlatforme()[i=0].getPlatforma()[j=1][k=0] = this;
			}
			boolean end = false;
			while(!end) {
				pauza();
				
				if(i==platforma) {
					if((k==0 || (k==4 && j<8)) && j>1) {
						if(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
							end = true;
						}
						else if(x>=j) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, "*");
							}
							else if(j<9 && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).moveVozilo(false, true, null);
								moveVozilo(true, false, "*");
							}
						}
						else {
							boolean dalje = false;
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, "*");
								dalje = true;
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k+1]).moveVozilo(true, true, null);
								pauza();
								moveVozilo(true, false, null);
								dalje = true;
							}
							else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1])) {
								end = true;
							}
							if(dalje) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
									pauza();
									moveVozilo(true, false, null);
								}
								else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									
									while(!end && j>0 && (!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
										pauza();
										if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
											end = true;
										}
										else {
											moveVozilo(false, true, null);
										}
									}
									if(!end) {
										if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
											moveVozilo(true, false, null);
										}
										else if(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
											end = true;
										}
									}
								}
							}
						}
					}
					else if(j>1 && (k==7 || (k==3 && j<8))) {
						if(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
							end = true;
						}
						else if(x<j) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, "*");
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).moveVozilo(true, true, null);
								moveVozilo(false, false, "*");
							}
						}
						else {
							boolean dalje = false;
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, "*");
								dalje = true;
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo)) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j][k-1]).moveVozilo(false, true, null);
								pauza();
								moveVozilo(false, false, null);
								dalje = true;
							}
							else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1])) {
								end = true;
							}
							if(dalje) {	
								if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									moveVozilo(false, false, null);
								}
								else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									while(!end && (!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
										pauza();
										if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
											end = true;
										}
										else {
											moveVozilo(true, true, null);
										}
									}
									if(!end){
										if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
											moveVozilo(false, false, null);
										}
										else if(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
											end = true;
										}
									}
								}
							}
						}
					}
					else if(j==0 && k>0) {
						if(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
							end = true;
						}
						else if(k==1 && y>=1) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								moveVozilo(true, true, null);
							}
							else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
								end = true;
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).moveVozilo(false, false, null);
								pauza();
								moveVozilo(true, true, null);
							}
						}
						else if(k==5 && y>=5 && !flag) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								moveVozilo(true, true, null);
							}
							else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
								end = true;
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).moveVozilo(false, false, null);
								pauza();
								moveVozilo(true, true, null);
							}
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
							moveVozilo(false, false, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]))) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
							moveVozilo(true, true, null);
							pauza();
							moveVozilo(false, false, null);
							while(!end && k>0 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
									end = true;
								}
								else {
									moveVozilo(false, false, null);
								}
							}
							if(!end) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
									end = true;
								}
								else {
									flag = true;
								}
							}	
						}
						else {
							while(k<5 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, null);
								pauza();
							}
							if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								moveVozilo(true, true, null);
								flag = true;
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)){
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).moveVozilo(false, false, null);
								pauza();
								moveVozilo(true, true, null);
								flag = true;
							}
						}
					}
					else if(k==1 && (j>1 && j<9)) {
						//dolje
						if(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
							moveVozilo(true, true, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) 
								|| (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
							end = true;
						}
						else if(((j==7 && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) || j==8) && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
							if(j==7) {
								moveVozilo(true, false, null);
								pauza();
								moveVozilo(true, true, null);
							}
							else if(j==8) {
								moveVozilo(true, false, null);
							}
								
							while(!end && k<6 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) 
										|| (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
									end = true;
								}
								else {
									moveVozilo(true, false, null);
								}
							}
							if(!end) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									moveVozilo(true, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
									end = true;
								}
								else {
									flag = true;
								}
							}
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
							moveVozilo(true, false, null);
							pauza();
							moveVozilo(true, true, null);
							
							while(!end && j<9 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
									end = true;
								}
								else {
									moveVozilo(true, true, null);
								}
							}
							if(!end) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
								else if(j==9) {
									while(j<1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
										pauza();
										moveVozilo(false, true, null);
									}
									if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
										moveVozilo(true, false, null);
									}
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]))) {
									end = true;
								}
								else {
									flag = true;
								}
							}
						}
						else {
							while(j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) {
								moveVozilo(false, true, null);
								pauza();
							}
							moveVozilo(true, false, null);
							flag = true;
						}
					}
					else if(j==9 && k<6) {
						//lijevo
						if(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
							moveVozilo(true, false, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
							moveVozilo(false, true, null);
							pauza();
							moveVozilo(true, false, null);
							
							while(!end && k<6 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
									end = true;
								}
								else {
									moveVozilo(true, false, null);
								}
							}
							if(!end) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									moveVozilo(true, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
									end = true;
								}
								else {
									flag = true;
								}
							}
						}
						else {
							while(k>2 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) {
								moveVozilo(false, false, null);
								pauza();
							}
							moveVozilo(false, true, null);
							flag = true;
						}
					}
					else if(((k==2 && j<=8) || k==6) && j>1) {
						//ici gore
						if(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
							moveVozilo(false, true, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]))) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo)) {
							moveVozilo(false, false, null);
							pauza();
							moveVozilo(false, true, null);
							
							while(!end && j>0 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
									end = true;
								}
								else {
									moveVozilo(false, true, null);
								}
							}
							if(!end) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
									moveVozilo(true, false, null);
								}
								else if(j==0) {
									flag = true;
									moveVozilo(true, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
									end = true;
								}
								else {
									flag = true;
								}
							}
						}
						else {
							while(j<8 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) {
								moveVozilo(true, true, null);
								pauza();
							}
							moveVozilo(false, false, null);
							flag = true;
						}
					}
					else if(k==5 && (j>1 && j<8)) {
						//dolje
						if(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])){
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
							moveVozilo(true, true, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
							end = true;
						}
						else if(j==7) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)
								&& !(garaza.getPlatforme()[i].getPlatforma()[j+2][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+2][k] instanceof Vozilo)) {
								moveVozilo(true, false, null);
								pauza();
								moveVozilo(true, true, null);
								pauza();
								moveVozilo(true, true, null);
								pauza();
								moveVozilo(false, false, null);
								
								while(!end && k>1 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									pauza();
									if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
										end = true;
									}
									else {
										moveVozilo(false, false, null);
									}
								}
								if(!end) {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
										moveVozilo(false, true, null);
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]))) {
										end = true;
									}
								}
							}
						}
						else {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, null);
								pauza();
								moveVozilo(true, true, null);
								
								while(!end && j<9 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									pauza();
									if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
										end = true;
									}
									else {
										moveVozilo(true, true, null);
									}
								}
								if(!end) {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
										moveVozilo(false, false, null);
									}
									else if(j==9 && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
										while(!end && k>1 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
											pauza();
											if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
												end = true;
											}
											else {
												moveVozilo(false, false, null);
											}
										}
										if(!end) {
											if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
												moveVozilo(false, true, null);
											}
											else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]))) {
												end = true;
											}
											else {
												flag = true;
											}
										}
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]))) {
										end = true;
									}
								}
							}
							else {
								while(j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) {
									moveVozilo(false, true, null);
									pauza();
								}
								moveVozilo(true, false, null);
								flag = true;
							}
						}
					}
					else if(k==7 && j==1 && y<7) {
						if(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
							moveVozilo(false, true, null);
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
							moveVozilo(false, false, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1]))) {
							end = true;
						}
						else {
							int pom = j;
							while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k-1] instanceof Vozilo) {
								pom++;
							}
							if(j<9) {
								pom--;
								while(pom >= j) {
									((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k-1]).setMove();
									((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k-1]).moveVozilo(true, true, null);
									
									pauza();
								}
								moveVozilo(false, false, null);
							}
						}
					}
					else if(j==1 && (k==2 || k==6)) {
						if(x>1) {
							flag = true;
						}
						if(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
							end = true;
						}
						else if(k==2) {
							if(y<=2) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
									end = true;
								}
								else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									moveVozilo(false, false, null);
									while(k>0 && !end && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
										pauza();
										if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
											end = true;
										}
										else {
											moveVozilo(false, false, null);
										}
									}
									if(!end) {
										if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
											moveVozilo(false, true, null);
										}
										else if(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
											end = true;
										}
										else {
											flag = true;
										}
									}
								}
							}
							else {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
									moveVozilo(true, false, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
									end = true;
								}
								else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
									moveVozilo(false, true, null);
									pauza();
									moveVozilo(true, false, null);
									while(!end && k<7 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
										pauza();
										if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
											end = true;
										}
										else {
											moveVozilo(true, false, null);
										}
									}
									if(!end) {
										if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
											moveVozilo(true, true, null);
										}
										else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
											end = true;
										}
										else {
											flag = true;
										}
									}
								}
							}
						}
						else if(k==6) {
							if(y<7) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
									end = true;
								}
								else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									moveVozilo(false, false, null);
									while(!end && k>0 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
										pauza();
										
										if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
											end = true;
										}
										else {
											moveVozilo(false, false, null);
										}
									}
									if(!end) {
										if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
											moveVozilo(false, true, null);
										}
										else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
											end = true;
										}
										else {
												flag = true;
										}
									}
								}
							}
							else if(y==7) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
									moveVozilo(true, false, null);
									if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
										end = true;
									}
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
									end = true;
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1])) {
									end = true;
								}
							}
						}
					}
					else if(j==1 && (k==1 || k==5)) {
						if((garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) || (garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]))){
							end = true;
						}
						else {
							if(k==1) {
								if((y<3 && x>1) || flag) {
									if(flag) {
										flag = false;
									}
									if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
										moveVozilo(true, true, null);
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
										end = true;
									}
									else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
										moveVozilo(true, false, null);
										pauza();
										moveVozilo(true, true, null);
										while(!end && j<9 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
											pauza();
											if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
												end = true;
											}
											else {
												moveVozilo(true, true, null);
											}
										}
										if(!end) {
											if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
												moveVozilo(false, false, null);
											}
											else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
												end = true;
											}
											else {
												flag = true;
											}
										}
									}
								}
								else {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
										moveVozilo(true, false, null);
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
										end = true;
									}
									else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
										moveVozilo(false, true, null);
										pauza();
										moveVozilo(true, false, null);
										while(!end && k<7 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
											pauza();
											if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
												end = true;
											}
											else {
												moveVozilo(true, false, null);
											}
										}
										if(!end) {
											if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
												moveVozilo(true, true, null);
											}
											else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
												end = true;
											}
											else {
												flag = true;
											}
										}
									}
									else if(!(garaza.getPlatforme()[i].getPlatforma()[8][2] instanceof Vozilo)) {
										int pom = j;
										while(pom<8 && garaza.getPlatforme()[i].getPlatforma()[pom][k+1] instanceof Vozilo) {
											pom++;
										}
										if(pom<8) {
											pom--;
											while(pom >= j) {
												((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k+1]).setMove();
												((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k+1]).moveVozilo(true, true, null);
												pauza();
											}
											moveVozilo(true, false, null);
										}
									}
									else {
										flag = true;
									}
								}
							}
							else if(k==5) {
								if((y<7 && x>1) || flag) {
									if(flag)
										flag = false;
									if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
										moveVozilo(true, true, null);
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k]) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1]))) {
										end = true;
									}
									else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
										moveVozilo(true, false, null);
										pauza();
										moveVozilo(true, true, null);
										
										while(!end && j<9 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
											pauza();
											if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
												end = true;
											}
											else {
												moveVozilo(true, true, null);
											}
										}
										if(!end) {
											if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
												moveVozilo(false, false, null);
											}
											else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
												end = true;
											}
											else {
												flag = true;
											}
										}
									}
									else {
										while(k>1 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
											moveVozilo(false, false, null);
											pauza();
										}
										if((k!=1 && j!=1) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
											moveVozilo(false, true, null);
											flag = true;
										}
									}
								}
								else {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
										moveVozilo(true, false, null);
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
										end = true;
									}
									else {
										int pom = j;
										while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k+1]instanceof Vozilo) {
											pom++;
										}
										if(pom<9) {
											pom--;
											while(pom>=j) {
												((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k+1]).setMove();
												((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k+1]).moveVozilo(true,  true,  null);
												pauza();
											}
											moveVozilo(true, false, null);
									
											if(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])){
												end = true;
											}
											else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
												pauza();
												moveVozilo(true, false, null);
												if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
													end = true;
												}
											}
											else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
												end = true;
											}
										}
									}
								}
							}
						}
					}
					else if(j==1 && k<7) {
						if(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
							moveVozilo(true, false, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1]) || (garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1]))) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
							moveVozilo(false, true, null);
							pauza();
							moveVozilo(true, false, null);
							while(!end && k<7 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
									end = true;
								}
								else {
									moveVozilo(true, false, null);
								}
							}
							if(!end) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									moveVozilo(true, true, null);
								}
								else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+1])) {
									end = true;
								}
								else {
									flag = true;
								}
							}
						}
						else if(k==0 && ((garaza.getPlatforme()[i].getPlatforma()[j-1][k+2] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k+2]))
								|| (garaza.getPlatforme()[i].getPlatforma()[j][k+2] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k+2])))) {
							end = true;
						}
						else {
							while(k>1 && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, null);
								pauza();
							}
							if(j==1) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									moveVozilo(true, true, null);
								}
							}
						}
					}
					else if(j==8 && k>2) {
						//desno
						if(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])){
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) || (garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo && udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1]))) {
							moveVozilo(false, false, null);
						}
						else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
							end = true;
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
							moveVozilo(true, true, null);
							pauza();
							moveVozilo(false, false, null);
							
							while(!end && k>1 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								pauza();
								if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
									end = true;
								}
								else {
									moveVozilo(false, false, null);
								}
							}
							if(!end) {
								if(k==1) {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
										moveVozilo(false, true, null);
										while(!end && j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
											pauza();
											if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j+1][k])) {
												end = true;
											}
											else {
												moveVozilo(false, true, null);
											}
										}
										if(!end) {
											if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
												moveVozilo(true, false, null);
											}
											else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j-1][k])) {
												end = true;
											}
											else {
												flag = true;
											}
										}
									}
								}
								else {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
										moveVozilo(false, true, null);
									}
									else if(udarenaVozila.contains(garaza.getPlatforme()[i].getPlatforma()[j][k-1])) {
										end = true;
									}
									else {
										flag = true;
									}
								}
							}
						}
						else {
							while(k<6 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) {
								moveVozilo(true, false, null);
								pauza();
							}
							moveVozilo(true, true, null);
							flag = true;
						}
					}
				}
				else if(i<platforma) {
					if(j==1) {
						while(k<7) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, null);
								pauza();
							}
							else if(k==1 || k==5) {
								int pom = j;
								while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k+1] instanceof Vozilo) {
									pom++;
								}
								if(pom<9) {
									pom--;
									while(pom>=j) {
										((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k+1]).setMove();
										((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k+1]).moveVozilo(true, true, null);
										pauza();
									}
									moveVozilo(true, false, null);
									pauza();
								}
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
								moveVozilo(false, true, null);
								pauza();
								moveVozilo(true, false, null);
								while(k<7 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
									pauza();
									moveVozilo(true, false, null);
								}
								if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
									moveVozilo(true, true, null);
								}
								else {
									while(k>0 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
										moveVozilo(false, false, null);
										pauza();
									}
									moveVozilo(true, true, null);
								}
							}
							else {
								while(k>1 && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									moveVozilo(false, false, null);
									pauza();
								}
								if(j==1 && k==1) {
									if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
										moveVozilo(true, true, null);
									}
								}
							}
						}
						if(k==7) {
							if(!(garaza.getPlatforme()[i+1].getPlatforma()[1][0] instanceof Vozilo)) {
								garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
								i++;
								garaza.getPlatforme()[i].getPlatforma()[j=1][k=0] = this;
							}
						}	
					}
					else if(j==0 && k>1) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
							moveVozilo(false, false, null);
						}
					}
					else if(j==0 && k==1) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
							moveVozilo(true, true, null);
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
							((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).setMove();
							((Vozilo)garaza.getPlatforme()[i].getPlatforma()[j+1][k]).moveVozilo(false, false, null);
							pauza();
							moveVozilo(true, true, null);
							pauza();
							if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								moveVozilo(true, true, null);
							}
							else {
								int pom = j;
								while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k]instanceof Vozilo) {
									pom++;
								}
								if(pom<9) {
									pom--;
									while(pom>=j) {
										((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k]).setMove();
										((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k]).moveVozilo(true,  true,  null);
										pauza();
									}
									moveVozilo(true, true, null);
								}
							}
						}
					}
					else {
						move();
					}
				}
				else if(i>platforma) {
					if(j==0) {
						while(k>0 && j==0) {
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, null);
								pauza();
							}
							else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
								moveVozilo(true, true, null);
								pauza();
								moveVozilo(false, false, null);
								while(k>0 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									pauza();
									moveVozilo(false, false, null);
								}
								pauza();
								if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
							}
							else if((k==7 || k==3) && garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo) {
								moveVozilo(true, true, null);
								pauza();
								int pom = j;
								while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k]instanceof Vozilo) {
									pom++;
								}
								if(pom<9) {
									pom--;
									while(pom>=j) {
										((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k]).setMove();
										((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k]).moveVozilo(true,  true,  null);
										pauza();
									}
									moveVozilo(true, true, null);
								}
								while(k>0 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
									pauza();
									moveVozilo(false, false, null);
								}
								pauza();
								if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
							}
							else if(k==2 || k==6) {
								int pom = j;
									while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k]instanceof Vozilo) {
										pom++;
									}
									if(pom<9) {
										pom--;
										while(pom>=j) {
											((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k]).setMove();
											((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k]).moveVozilo(true,  true,  null);
											pauza();
										}
										moveVozilo(true, true, null);
									}
								}
							}
					}
					if(k==0 && (j==1 || j==0)) {
						if(j==0) {
							if(!(garaza.getPlatforme()[i-1].getPlatforma()[0][7] instanceof Vozilo)) {
								garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
								garaza.getPlatforme()[--i].getPlatforma()[j=0][k=7] = this;
							}
							else if(!(garaza.getPlatforme()[i-1].getPlatforma()[1][7] instanceof Vozilo)) {
								garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
								garaza.getPlatforme()[--i].getPlatforma()[j=1][k=7] = this;
							}	
							else if(udarenaVozila.contains(garaza.getPlatforme()[i-1].getPlatforma()[0][6]) || udarenaVozila.contains(garaza.getPlatforme()[i-1].getPlatforma()[1][6])) {
								end = true;
								endSaVisePlatforme = true;
							}
						}
						else if(j==1) {
							if(!(garaza.getPlatforme()[i-1].getPlatforma()[1][7] instanceof Vozilo)) {
								garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
								garaza.getPlatforme()[--i].getPlatforma()[j=1][k=7] = this;
							}
						}
					}
					else if((k==2 || k==6) && j==1) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
							moveVozilo(false, true, null);
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
							while(k>0 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, null);
								pauza();
							}
							if(k>0) {
								if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
									moveVozilo(false, true, null);
								}
							}
							else {
								if(!(garaza.getPlatforme()[i-1].getPlatforma()[1][7] instanceof Vozilo)) {
									garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
									garaza.getPlatforme()[--i].getPlatforma()[j=1][k=7] = this;
								}
								else if(!(garaza.getPlatforme()[i-1].getPlatforma()[0][7] instanceof Vozilo)) {
									garaza.getPlatforme()[i].getPlatforma()[j][k] = null;
									garaza.getPlatforme()[--i].getPlatforma()[j=0][k=7] = this;
								}
							}
						}
					}
					else if(j==1 && k==7) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
							moveVozilo(false, true, null);
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
							moveVozilo(false, false, null);
						}
						else {
							int pom = j;
							while(pom<9 && garaza.getPlatforme()[i].getPlatforma()[pom][k-1]instanceof Vozilo) {
								pom++;
							}
							if(pom<9) {
								pom--;
								while(pom>=j) {
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom][k-1]).setMove();
								((Vozilo)garaza.getPlatforme()[i].getPlatforma()[pom--][k-1]).moveVozilo(true,  true,  null);
								pauza();
							}
							moveVozilo(false, false, null);
							}
						}
					}
					else if(k==5 && j==1) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
							moveVozilo(true, true, null);
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
							moveVozilo(true, false, null);
							pauza();
							moveVozilo(true, true, null);
							while(j<9 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								pauza();
								moveVozilo(true, true, null);
							}
							if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
								moveVozilo(false, false, null);
							}
						}
					}
					else if(j==1 &&(k>2 && k<5)) {
						if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								moveVozilo(true, false, null);
						}
						else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
							moveVozilo(false, true, null);
							pauza();
							moveVozilo(true, false, null);
							while(k<5 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
								pauza();
								moveVozilo(true, false, null);
							}
							if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
								moveVozilo(true, true, null);
							}
						}
					}
					else if(j==1 && k<3) {
						while(k>0 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) {
							moveVozilo(false, false, null);
							pauza();
						}
						if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
							moveVozilo(false, true, null);
						}
					}
					else {
						move();
					}
				}
				synchronized(this) {
					notifyAll();
				}
			}
			
			barijera.dostigaoBarijeru();
			if(this instanceof PolicijskoVozilo) {
				((PolicijskoVozilo)this).evidentirajSudar();
				if(endSaVisePlatforme) {
					synchronized(garaza.getPlatforme()[i-1].locker) {
						garaza.getPlatforme()[i-1].setPlatformaBlokirana(false);
						garaza.getPlatforme()[i-1].locker.notifyAll();
					}
				}
				else {
					synchronized(garaza.getPlatforme()[i].locker) {
						garaza.getPlatforme()[i].setPlatformaBlokirana(false);
						garaza.getPlatforme()[i].locker.notifyAll();
					}
				}
				((PolicijskoVozilo)this).setZauzeto(true);
			}
			else {
				try {
					sleep(3000);
				} catch(InterruptedException e) {
					LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
				}
			}
			evidentiraoSudar = true;
			setUlaz(false);
			setSudar(false);
			setUhvacen(true);
			ugasiRotaciju();
			
			if(j==1) {
				int tmp = j;
				while(tmp==j) {
					if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo))
						moveVozilo(false, true, null);
					else 
						pauza();
				}
			}
		}
		super.run();
	}
	
	private void move() {
		if((k==0 && j>1) || (k==4 && (j>1 && j<8))) {
			if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
				moveVozilo(true, false, "*");
				pauza();
				
				if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
					moveVozilo(true, false, null);
				}
				else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
					moveVozilo(false, true, null);
					while(j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) {
						pauza();
						moveVozilo(false, true, null);
					}
					if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
						moveVozilo(true, false, null);
					}
				}
			}
		}
		else if(((k==3 && j<8) || k==7) && j>1) {
			if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
				moveVozilo(false, false, "*");
			}
		}
		else if((k==1 && (j>1 && j<9)) || (k==5 && (j>1 && j<8))) {
			if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
				moveVozilo(true, true, null);
			}
			
			else if(j==7 || j==8) {
				if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
					if(j==7) {
						moveVozilo(true, false, null);
						pauza();
						moveVozilo(true, true, null);
					}
					else if(j==8) {
						moveVozilo(true, false, null);
					}
					
					while(k<6 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) {
						pauza();
						moveVozilo(true, false, null);
					}
					if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
							moveVozilo(true, true, null);
					}
				}
			}
			else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k+1] instanceof Vozilo)) {
				moveVozilo(true, false, null);
				pauza();
				moveVozilo(true, true, null);
				
				while(j<8 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) {
					pauza();
					moveVozilo(true, true, null);
				}
				if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
						moveVozilo(false, true, null);
				}
			}
			else {
				while(j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) {
					moveVozilo(false, true, null);
					pauza();
				}
				moveVozilo(true, false, null);
			}
		}
		else if(j==9 && k<6) {
			if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
				moveVozilo(true, false, null);
			}
			else if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k+1] instanceof Vozilo)) {
				moveVozilo(false, true, null);
				pauza();
				moveVozilo(true, false, null);
				
				while(k<6 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) {
					pauza();
					moveVozilo(true, false, null);
				}
				if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo)) {
						moveVozilo(true, true, null);
				}
			}
		}
		else if((k==6 && j>1) || (k==2 && (j>1 && j<=8))) {
			if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
				moveVozilo(false, true, null);
			}
			else if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k-1] instanceof Vozilo)) {
				moveVozilo(false, false, null);
				pauza();
				moveVozilo(false, true, null);
				
				while(j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo) {
					pauza();
					moveVozilo(false, true, null);
				}
				if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
					moveVozilo(true, false, null);
				}
			}
			else {
				while(j<8 && garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo) {
					moveVozilo(true, true, null);
				}
				moveVozilo(false, false, null);
			}
		}
		else if((k<=5 && k>2) && j==8) {
			if(!(garaza.getPlatforme()[i].getPlatforma()[j][k-1] instanceof Vozilo)) {
				moveVozilo(false, false, null);
			}
			else if(!(garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) && !(garaza.getPlatforme()[i].getPlatforma()[j+1][k-1] instanceof Vozilo)) {
				moveVozilo(true, true, null);
				pauza();
				moveVozilo(false, false, null);
				
				while(k>1 && garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo) {
					pauza();
					moveVozilo(false, false, null);
				}
				if(k==1) {
					if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
						moveVozilo(false, true, null);
						while(j>1 && garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo && !(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
							pauza();
							moveVozilo(false, true, null);
						}
						if(!(garaza.getPlatforme()[i].getPlatforma()[j][k+1] instanceof Vozilo)) {
							moveVozilo(true, false, null);
						}
					}
				}
				else {
					if(!(garaza.getPlatforme()[i].getPlatforma()[j-1][k] instanceof Vozilo)) {
						moveVozilo(false, true, null);
					}
				}
			}
			else {
				while(k<7 && garaza.getPlatforme()[i].getPlatforma()[j+1][k] instanceof Vozilo) {
					moveVozilo(true, false, null);
					pauza();
				}
				moveVozilo(true, true, null);
			}
		}
	}
	public void pauza() {
		try {
			sleep(500);
		} catch(InterruptedException e) {
			LoggerWrapper.getLogger().log(Level.INFO, e.toString(), e);
		}
	}
}