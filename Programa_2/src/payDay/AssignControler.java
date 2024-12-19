package payDay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssignControler {
	// Money aviable and debs
	private double companyAccountCash;
	private double companyDebs;

	// Collection readed from the document and the documents
	private ArrayList<String> transfersCollection;
	private File transfers;
	private File innerTransfers;
	private File externalTransfers;
	private File pendingTransfers;

	// Atributes for control
	private boolean taskDone;
	private boolean withoutCredit;
	private int processesEnded;
	private int nextTransfer;
	private String consolePendingTransfers;

	public AssignControler(double companyAccountCash, File transfers) {
		this.companyAccountCash = companyAccountCash;
		this.companyDebs = 0;

		taskDone = false;
		withoutCredit = false;
		nextTransfer = 0;
		processesEnded = 0;
		consolePendingTransfers = "";

		this.transfersCollection = new ArrayList<String>();
		this.transfers = transfers;
		this.innerTransfers = new File("transferenciasInt.txt");
		this.externalTransfers = new File("transferenciasExt.txt");
		this.pendingTransfers = new File("transferenciasSinSaldo.txt");

		reStartFile(innerTransfers);
		reStartFile(externalTransfers);
		reStartFile(pendingTransfers);

		try (BufferedReader br = new BufferedReader(new FileReader(transfers))) {
			int cont = 0;
			while (br.ready()) {
				transfersCollection.add(br.readLine());
				String[] keyValue = transfersCollection.get(cont).split(";");
				double value = Double.parseDouble(keyValue[1]);
				companyDebs += value;
				cont++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Total deudas empresa: " + companyDebs);
	}

	public int getProcessesEnded() {
		return processesEnded;
	}

	public synchronized void setProcessesEnded(int count) {
		processesEnded = count;
	}

	public boolean isTaskDone() {
		return taskDone;
	}

	public double getCompanyAccountCash() {
		return companyAccountCash;
	}

	public void reStartFile(File document) {
		try {
			if (document.exists()) {
				document.delete();
				document.createNewFile();
			} else {
				document.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processing(ProcessorThread worker) {
		if (nextTransfer == transfersCollection.size()) {
			if (!(consolePendingTransfers.equals(""))) {
				System.out.println();
				System.out.println(consolePendingTransfers);
			}
			taskDone = true;
			return;
		}

		String transfer = transferToProcess();
		String[] keyValue = transfer.split(";");

		if (keyValue[0].indexOf("1") == 0) {
			try {
				Thread.sleep(1000);
				registerTransfer(innerTransfers, keyValue);
				updateAccount(keyValue, worker);
				if (withoutCredit) {
					consolePendingTransfers += "Grabamos transferencia interna sin saldo. Cuenta " + keyValue[0] + "\n";
					withoutCredit = false;
				} else {
					System.out.println("Grabamos transferencia interna. Cuenta " + keyValue[0]);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			registerTransfer(externalTransfers, keyValue);
			updateAccount(keyValue, worker);
			if (withoutCredit) {
				consolePendingTransfers += "Grabamos transferencia externa sin saldo. Cuenta " + keyValue[0] + "\n";
			} else {
				System.out.println("Grabamos transferencia externa. Cuenta " + keyValue[0]);
			}
		}

	}

	public synchronized String transferToProcess() {
		String transfer = transfersCollection.get(nextTransfer);
		nextTransfer++;
		return transfer;
	}

	public synchronized void registerTransfer(File document, String[] keyValue) {
		double salary = Double.parseDouble(keyValue[1]);

		if ((companyAccountCash - salary) < 0) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(pendingTransfers, true))) {
				bw.write(keyValue[0] + ";" + keyValue[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(document, true))) {
				bw.write(keyValue[0] + ";" + keyValue[1] + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void updateAccount(String[] keyValue, ProcessorThread worker) {
		double salary = Double.parseDouble(keyValue[1]);

		if ((companyAccountCash - salary) < 0) {
			consolePendingTransfers += "No hay saldo para la siguiente transferencia: " + salary + " €.\n";
			withoutCredit = true;
			billing(worker, salary);
		} else {
			companyAccountCash -= salary;
			System.out.println("Cuenta: " + keyValue[0] + " - Actualizamos el saldo de la cuenta con el importe: "
					+ salary + " €.");
			billing(worker, salary);
		}
	}

	public synchronized void billing(ProcessorThread worker, Double salary) {
		worker.setMoneyMoved(worker.getMoneyMoved() + salary);
	}

//	public synchronized void increaseEndedProcessesCount() {
//		processesEnded++;
//	}
}
