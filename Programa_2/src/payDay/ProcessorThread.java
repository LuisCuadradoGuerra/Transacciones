package payDay;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ProcessorThread extends Thread {
	private double moneyMoved;
	private AssignControler bank;
	private String subcontractCompany;

	public ProcessorThread(String subcontractCompany, AssignControler bank) {
		this.subcontractCompany = subcontractCompany;
		this.bank = bank;
		moneyMoved = 0;
	}

	public String getSubcontractCompany() {
		return subcontractCompany;
	}

	public void setSubcontractCompany(String subcontractCompany) {
		this.subcontractCompany = subcontractCompany;
	}

	public double getMoneyMoved() {
		return moneyMoved;
	}

	public void setMoneyMoved(double moneyMoved) {
		this.moneyMoved = moneyMoved;
	}

	@Override
	public void run() {
		while (!bank.isTaskDone()) {
			bank.processing(this);
		}
		
		//Esta espera es meramente visual, para que saque los datos en orden. Puede comentarse y funciona igual el programa.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		bank.setProcessesEnded(bank.getProcessesEnded() + 1);
		if (bank.getProcessesEnded() == 1) {
			System.out.println();
		}
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		DecimalFormat twoDecimals = new DecimalFormat("0.00", symbols);
		
		System.out.println(
				"Subcontrata " + subcontractCompany + " ha hecho transferencias por valor de: " + twoDecimals.format(moneyMoved) + " €.");

		if (bank.getProcessesEnded() == 3) {
			System.out.println();
			System.out.println("Quedan " + twoDecimals.format(bank.getCompanyAccountCash()) + " € en la cuenta.");
			System.out.println("Fin del proceso.");
		}
	}
}
