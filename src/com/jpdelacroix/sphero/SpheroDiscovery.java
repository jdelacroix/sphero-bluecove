package com.jpdelacroix.sphero;

// Copyright (C) 2014, Jean-Pierre de la Croix
// see the LICENSE file included with this software

import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class SpheroDiscovery {

	private ArrayList<Sphero> spherosDiscovered = new ArrayList<>();
	private String spheroServiceURL = null;
	
	private final Object inquiryCompletedEvent = new Object();
	private final Object serviceSearchCompletedEvent = new Object();
	
	private final SpheroDiscoveryListener listener = new SpheroDiscoveryListener();
	
	private final String SPHERO_IEEE_OUI = "000666"; // IEEE OUI for Roving Networks
	
	public ArrayList<Sphero> findNearbySpheros() throws BluetoothStateException, InterruptedException {
		spherosDiscovered.clear();
				
		synchronized(inquiryCompletedEvent) {
            boolean isInquiryStarted = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, (DiscoveryListener) listener);
            if (isInquiryStarted) {
                System.out.println("Waiting for inquiry to complete...");
                inquiryCompletedEvent.wait();
                System.out.println("Found " + spherosDiscovered.size() +  " Sphero(s)!");
            }
        }
		
		// Search for RN-SPP service on Sphero
		UUID serviceUUID = new UUID("0000110100001000800000805F9B34FB", false); // RN-SPP (RN-42)
		
		UUID[] searchUuidSet = new UUID[] { serviceUUID };
		int[] attrIDs = null;

        for(Sphero s : spherosDiscovered) {
            synchronized(serviceSearchCompletedEvent) {
                System.out.println("Searching services on " + s.getBluetoothAddress() + "  " + s.getFriendlyName() + "...");
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, s, listener);
                serviceSearchCompletedEvent.wait();
            }
            if(spheroServiceURL == null) {
            	System.out.println("No services found. Using default Sphero service URL.");
            } else {
            	s.setSpheroUrl(spheroServiceURL);
            }
        }
        
		return spherosDiscovered;
	}
	
	
	private class SpheroDiscoveryListener implements DiscoveryListener {

		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass btClass) {
			if(btDevice.getBluetoothAddress().startsWith(SPHERO_IEEE_OUI)) {
				System.out.println("Found a Sphero nearby!");
				Sphero s = null;
				try {
					s = new Sphero(btDevice.getBluetoothAddress(), btDevice.getFriendlyName(false), Sphero.SPP_DEFAULT_CHANNEL);
				} catch (IOException e) {
					s = new Sphero(btDevice.getBluetoothAddress(), "Unknown", 1);
				}
				System.out.println(s.getBluetoothAddress() + "  " + s.getFriendlyName());
		        spherosDiscovered.add(s);
			} else {
				System.out.println("Found some other Bluetooth device.");
			}
		
	    }

	    public void inquiryCompleted(int discType) {
	        System.out.println("Inquiry for any nearby Spheros completed!");
	        synchronized(inquiryCompletedEvent){
	            inquiryCompletedEvent.notifyAll();
	        }
	    }

	    public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
            for (int i = 0; i < serviceRecord.length; i++) {
                String url = serviceRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url == null) {
                    continue;
                }
                spheroServiceURL = new String(url);
                System.out.println("Found service (RN-SPP) at " + url + ".");
                break;	// Sphero only has a single RN-SPP service.
            }
        }

        public void serviceSearchCompleted(int transID, int respCode) {
            System.out.println("Service search on Sphero completed!");
            synchronized(serviceSearchCompletedEvent){
                serviceSearchCompletedEvent.notifyAll();
            }
        }
	}
}
