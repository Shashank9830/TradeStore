package com.barclays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.barclays.service.TradeStoreBatchService;

/** SpringRunner class for this project.
 * Defines the entry point for the application.
 * Starts the REST API Controller and the Batch Service thread.
 * 
 * REST API Controller handles all the incoming trades.
 * 
 * Batch Service Thread periodically updates the expired flag for
 * matured trades in the store.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
@SpringBootApplication
public class BarclaysTradeStoreApplication implements CommandLineRunner {
	
	@Autowired
	private TradeStoreBatchService tradeStoreBatchService;

	/** Entry point for the application.
	 * Program execution starts from this method.
	 * 
	 * @param args -- command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BarclaysTradeStoreApplication.class, args);
	}

	
	/** run() method implementation of the CommandLineRunner interface.
	 * This is used to create a new thread during app startup to handle 
	 * the batch job.
	 * 
	 * It looks for trades that have matured and sets their
	 * expired flag to Y if it isn't already set.
	 * 
	 * This logic runs periodically.
	 * 
	 * The time is in milliseconds and can be modified in application.properties.
	 * 
	 */
	@Override
	public void run(String... args) throws Exception {
		Thread batchServiceThread=new Thread(tradeStoreBatchService);
		batchServiceThread.setDaemon(true);
		batchServiceThread.start();
	}
}
