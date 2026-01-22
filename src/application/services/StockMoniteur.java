package application.services;

import java.util.concurrent.Semaphore;

public class StockMoniteur {
	
	// fcfs mutex
	private static final Semaphore transactionMutex = new Semaphore(1, true);
	
	public static void P() throws InterruptedException {
		System.out.println("[StockMoniteur] Demande d'accès au stock...");
		transactionMutex.acquire();
		System.out.println("[StockMoniteur] Accès obtenu au stock.");
	}
	
	public static void V() {
		System.out.println("[StockMoniteur] Transaction achevée.");
		transactionMutex.release();
		System.out.println("[StockMoniteur] Mutex libéré.");
	}
	
	public static boolean estOccupe() {
		return transactionMutex.availablePermits() == 0;
	}
}
