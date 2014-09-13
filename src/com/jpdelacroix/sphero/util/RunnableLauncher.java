package com.jpdelacroix.sphero.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Copyright (C) 2014, Jean-Pierre de la Croix
//see the LICENSE file included with this software

public class RunnableLauncher {

	private final ExecutorService runnableService = Executors.newSingleThreadExecutor();
	
	public void launch(Runnable aRunnable) {
		runnableService.submit(aRunnable);
		runnableService.shutdown();
		try {
			while(!runnableService.awaitTermination(60, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			System.err.println("Interrupted while waiting for Runnable to finish.");
		}
	}
}
