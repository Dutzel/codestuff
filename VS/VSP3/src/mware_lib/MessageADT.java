package mware_lib;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

public class MessageADT implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InetAddress iNetAdrress;
	private int messageID;
	private String methodName;
	private int messageType;
	private RemoteObjectRef objectRef;
	private byte[] returnVal;
	private List<byte[]> arguments;
	private List<Exception> exceptionList;

	public MessageADT(InetAddress iNetAddress, Integer messageID,
			String methodName, Integer messageType, RemoteObjectRef objectRef,
			byte[] returnVal, List<byte[]> arguments,
			List<Exception> exceptionList) {
		this.iNetAdrress = iNetAddress;
		this.messageID = messageID;
		this.methodName = methodName;
		this.messageType = messageType;
		this.objectRef = objectRef;
		this.returnVal = returnVal;
		this.arguments = arguments;
		this.exceptionList = exceptionList;
	}

	public InetAddress getiNetAdrress() {
		return iNetAdrress;
	}

	public void setiNetAdrress(InetAddress iNetAdrress) {
		this.iNetAdrress = iNetAdrress;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public RemoteObjectRef getObjectRef() {
		return objectRef;
	}

	public void setObjectRef(RemoteObjectRef objectRef) {
		this.objectRef = objectRef;
	}

	public byte[] getReturnVal() {
		return returnVal;
	}

	public void setReturnVal(byte[] returnVal) {
		this.returnVal = returnVal;
	}

	public List<byte[]> getArguments() {
		return arguments;
	}

	public void setArguments(List<byte[]> arguments) {
		this.arguments = arguments;
	}

	public List<Exception> getExceptionList() {
		return exceptionList;
	}

	public void setExceptionList(List<Exception> exceptionList) {
		this.exceptionList = exceptionList;
	}

}