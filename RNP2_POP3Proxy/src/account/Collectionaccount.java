package account;

import java.util.ArrayList;
import java.util.List;


/**
 * Repraesentiert den Abholaccount fuer jedes konfigurierte Konto
 * Fuehrt eine Liste aller Mails zum zugehoerigen Account
 * @author Fabian Reiber und Francis Opoku
 *
 */
public class Collectionaccount {

	//liste von mails
	private List<String> mailList;
	
	public Collectionaccount(){
		this.mailList = new ArrayList<String>();
	}

	public void addMails(List<String> mailList){
		this.mailList.addAll(mailList);
	}
	
	public void clearMails(){
		this.mailList.clear();
	}
	
	public void removeMail(int index){
		this.mailList.remove(index);
	}
	
	public List<String> getMailList() {
		return mailList;
	}

	public void setMailList(List<String> mailList) {
		this.mailList = mailList;
	}
	
	
}
