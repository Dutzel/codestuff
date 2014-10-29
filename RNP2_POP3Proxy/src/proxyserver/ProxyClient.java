package proxyserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import account.POP3Account;

/**
 * Ein ProxyClient-Thread der die Kommunikation mit dem POP3MailServer steuert
 * @author Fabian Reiber und Francis Opoku
 *
 */

public class ProxyClient extends Thread{

//	private Socket connection;
	/**
	 * TIMEINTERVAL in ms
	 */
	private static int TIMEINTERVAL;
	/**
	 * Liste der Accounts von denen die Mails abegrufen werden sollen
	 */
	private List<POP3Account> accountList;
	
	public ProxyClient(){
		//this.connection = connection;
		TIMEINTERVAL = 30000;
		this.accountList = new ArrayList<POP3Account>();
	}
	
	/**
	 * Hauptroutine in der der Proxy ggü. dem POP3MailServer als Client fungiert und
	 * alle TIMEINTERVAL ms die Mails abholt und in einem "Abhol-Account" sichert
	 */
	public void run(){
		try {
			Thread.sleep(TIMEINTERVAL);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//loop(every 30secs) do fuer jeden account holen
			//loop(i to accountList.length) authentifizieren und mails holen
				//loop(j to numberOfMail) alle vorhandenen Mails vom Server holen und speichern
	}
	
	/**
	 * geholte Mails in einen Abholaccount sichern
	 */
	private void saveMail(){
		//Mail in Abholaccount sichern
	}
	
	/**
	 * hinzufuegen eines neuen Accounts in die Liste
	 * @param a POP3Account
	 */
	public void addAccount(POP3Account a){
		this.accountList.add(a);
	}
}
