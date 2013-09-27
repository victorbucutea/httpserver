package com.adobe.server.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * manages 
 * 1. Thread pool mgmt , increase decrease number of threads, etc.  
 * 2. Thread saturation - After how many concurrent executing jobs we can say that it's too much.
 * 
 * @author VictorBucutea
 *
 */
public class ThreadManager {
	
	ExecutorService executor = Executors.newCachedThreadPool();
	
	public void execute(Runnable runnable){
		executor.execute(runnable);
	}
	
	public void stopExecuting() { 
		executor.shutdownNow();
	}

}
