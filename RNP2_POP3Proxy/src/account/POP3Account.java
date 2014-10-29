package account;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Repraesentiert die zu konfigurierenden POP3-Konten
 * @author Fabian Reiber und Francis Opoku
 *
 */
public class POP3Account {

	private String user;
	private String pass;
	private String serveraddress;
	private int port;
	//kein Semaphore fuer address, da nur der Server diese aendern kann
	private InetAddress address;
	private Collectionaccount cAccount;
	private Semaphore connectionSem;
	
	public POP3Account(String user, String pass, String serveraddress, int port){
		this.user = user;
		this.pass = pass;
		this.serveraddress = serveraddress;
		this.port = port;
		try {
			address = InetAddress.getByName(serveraddress);
		} catch (UnknownHostException e) {
			System.out.println("unable to create inetadress");
			e.printStackTrace();
		}
		this.cAccount = new Collectionaccount();
		this.connectionSem = new Semaphore(1);
	}

	/**
	 * fuer Client
	 */
	public void addMails(List<String> mailList){
		try {
			this.connectionSem.acquire();
			this.cAccount.addMails(mailList);
		} catch (InterruptedException e) {
			System.out.println("not possible to acquire sem");
			e.printStackTrace();
		}
		this.connectionSem.release();
	}
	
	/**
	 * fuer server
	 */
	public List<String> getMails(){
		List<String> copyAcc = null;
		
		try {
			this.connectionSem.acquire();
			copyAcc = new ArrayList<String>();
			copyAcc.addAll(this.cAccount.getMailList());
		} catch (InterruptedException e) {
			System.out.println("not possible to acquire sem");
			e.printStackTrace();
		}
		
		this.connectionSem.release();
		return copyAcc;
	}
	
	
	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(String serveraddress) {
		try {
			this.address = InetAddress.getByName(serveraddress);
		} catch (UnknownHostException e) {
			System.out.println("unable to create inetadress");
			e.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((cAccount == null) ? 0 : cAccount.hashCode());
		result = prime * result
				+ ((connectionSem == null) ? 0 : connectionSem.hashCode());
		result = prime * result + ((pass == null) ? 0 : pass.hashCode());
		result = prime * result + port;
		result = prime * result
				+ ((serveraddress == null) ? 0 : serveraddress.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POP3Account other = (POP3Account) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (cAccount == null) {
			if (other.cAccount != null)
				return false;
		} else if (!cAccount.equals(other.cAccount))
			return false;
		if (connectionSem == null) {
			if (other.connectionSem != null)
				return false;
		} else if (!connectionSem.equals(other.connectionSem))
			return false;
		if (pass == null) {
			if (other.pass != null)
				return false;
		} else if (!pass.equals(other.pass))
			return false;
		if (port != other.port)
			return false;
		if (serveraddress == null) {
			if (other.serveraddress != null)
				return false;
		} else if (!serveraddress.equals(other.serveraddress))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
		
}
