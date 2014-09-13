package com.jpdelacroix.sphero.heartbeat;


//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public abstract class SpheroHeartbeatListener implements Runnable {

	private SpheroHeartbeat spheroHeartbeat = null;
	
	public final void setHeartbeat(SpheroHeartbeat aHeartbeat) {
		spheroHeartbeat = aHeartbeat;
	}
	
	@Override
	public final void run() {
		spheroHeartbeat.monitor();
		onChange();
	}
	
	public abstract void onChange();

}
