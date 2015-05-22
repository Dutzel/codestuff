package mware_lib;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import accessor_one.ObjectAdapterAO;
import accessor_two.ObjectAdapterAT;

public class RequestDemultiplexer {
	ArrayList<ObjectAdapter> adapter;

	public RequestDemultiplexer() {
		// TODO Auto-generated constructor stub
		//this.komModAddr = komModAddr;
		adapter = new ArrayList<ObjectAdapter>(
				Arrays.asList(new ObjectAdapterAO(), new ObjectAdapterAT()));
	}

	public void pass(MessageADT m) {
		int objectNumber = m.getObjectRef().getObjectNumber();
		for (ObjectAdapter item : this.adapter) {
			if(item.getSkeletonIDs().contains(objectNumber)){
				item.initSkeleton(m);
				break;
			}
		}
	}

}
