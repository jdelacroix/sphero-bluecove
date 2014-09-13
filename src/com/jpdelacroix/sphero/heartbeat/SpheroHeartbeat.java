package com.jpdelacroix.sphero.heartbeat;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class SpheroHeartbeat {

	private final Lock heartbeatLock = new ReentrantLock();
	private final Condition heartbeatLost = heartbeatLock.newCondition();
	
	public void monitor() {
		heartbeatLock.lock();
		try {
			heartbeatLost.await();
		} catch (InterruptedException e) {
			System.err.println("Monitoring heartbeat was interrupted.");
		} finally {
			heartbeatLock.unlock();
		}
	}
	
	public void kill() {
		heartbeatLock.lock();
		try {
			heartbeatLost.signal();
		} finally {
			heartbeatLock.unlock();
		}
	}
	
}
