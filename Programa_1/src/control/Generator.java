package control;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Generator {

	public static void main(String[] args) {
		//Chance to malfunction
		int functionControler = (int) (Math.random() * 100);
		
		if (functionControler < 30) {
			System.exit(-1);			
		} else {
			Scanner sc = new Scanner(System.in);
			int transfersNumber = Integer.parseInt(sc.nextLine());
			
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
			DecimalFormat twoDecimals = new DecimalFormat("0.00", symbols);
			
			for (int i = 0; i < transfersNumber; i++) {
				int createdAccount = ((int) (Math.random()*(2)+1) * 100000000) + ((int) (Math.random()*99999999));
				double salary = (double) (Math.random()*(3000-1500)+1500);
				System.out.println(createdAccount + ";" + twoDecimals.format(salary));
			}
			
			sc.close();
		}
	}

}
