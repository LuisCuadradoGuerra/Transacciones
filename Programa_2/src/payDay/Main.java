package payDay;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Main {
	public static void main(String[] args) {
		//Tools of control
		int error404 = 0;
		
		// 1 Internal 2 External
		Scanner sc = new Scanner(System.in);
		
		String transfersFileName;
		String transfersFilePath;
		int transfersNumber;

		//Collect input from console
		System.out.println("Document name:");
		transfersFileName = sc.nextLine();

		System.out.println("Document path:");
		transfersFilePath = sc.nextLine();

		System.out.println("Number of transfers to process:");
		transfersNumber = Integer.parseInt(sc.nextLine());

		
		
		//Create if don´t exits or path to document
		String completePath = transfersFilePath + "\\" + transfersFileName;
		File transfersFile = new File(completePath);
		try {
			transfersFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Send the transfers required to the first program
		File directorio = new File("F:\\Z. Segundo año\\PSP_WorkSpace\\Programa_1\\bin");
		ProcessBuilder pb = new ProcessBuilder("java", "control.Generator");
		pb.directory(directorio);
		pb.redirectOutput(transfersFile);
		try {
			Process p = pb.start();
			try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))){
				bw.write(transfersNumber + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
			error404 = p.waitFor();
			System.out.println("Valor de Salida: " + error404);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (error404 < 0) {
			System.out.println("Error con la generación del fichero o su ruta");
		} else {
			//String to double from a double with two decimals calculated from the total transfers required x ramdom double between 3000 and 2000
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
			DecimalFormat twoDecimals = new DecimalFormat("0.00", symbols);
			//Calculate the company cash
			double companyAccountCash = Double.parseDouble(twoDecimals.format(transfersNumber * ((double) (Math.random()*(3000-2000)+2000))));
			System.out.println("Total cuenta empresa: " + companyAccountCash);
			
			
			//Start the ProcessorThreads
			AssignControler manager = new AssignControler(companyAccountCash, transfersFile);
			ProcessorThread subcontracOne = new ProcessorThread("Deloitte", manager);
			ProcessorThread subcontracTwo= new ProcessorThread("Allfinanz", manager);
			ProcessorThread subcontracThree = new ProcessorThread("Afi", manager);
			
			subcontracOne.start();
			subcontracTwo.start();
			subcontracThree.start();
		}
	}
}
