package util;

import java.util.Random;

public class Util {

	private static char[] abc = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public static Random rand = new Random();
	
	public static String randomNaziv() {
		String name ="";
		int n = rand.nextInt(10)+5;
		for(int i=0; i<n; ++i)
		{
			if(i!=0)
				name+=abc[rand.nextInt(abc.length)];
			else
			{
				name+=abc[rand.nextInt(abc.length)];
				name = name.toUpperCase();
			}
		}
		return name;
	}
	
	
	public static String randomBrojSasije() {
		String brojSasije = "";
		int n = rand.nextInt(2)+16;
		for(int i=0; i<n; ++i)
		{
			if(rand.nextInt(100)< 50)
				brojSasije += rand.nextInt(9)+1;
			else
				brojSasije += abc[rand.nextInt(abc.length)];
		}
		return brojSasije.toUpperCase();
	}
	
	
	public static String randomBrojMotora() {
		String brojMotora = "";
		for(int i=0; i<9; ++i)
		{
			if(rand.nextInt(100)<50)
				brojMotora += abc[rand.nextInt(abc.length)];
			else brojMotora += rand.nextInt(9)+1;
		}
		return brojMotora.toUpperCase();
	}
	
	
	public static String randomRegistarskiBroj() {
		String registarskiBroj = "";
		for(int i=0; i<9; ++i)
		{
			if((i==0) || (i==4))
				registarskiBroj += abc[rand.nextInt(abc.length)];
			else if((i==3) || (i==5))
				registarskiBroj += "-";
			else
				registarskiBroj += rand.nextInt(10);
		}
		return registarskiBroj.toUpperCase();
	}

}
