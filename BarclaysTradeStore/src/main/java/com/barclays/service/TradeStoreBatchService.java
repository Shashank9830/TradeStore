package com.barclays.service;

/** Interface for Batch Services. Extends the Runnable interface
 * to support multi-threading needed during app startup.
 * 
 * One method must be implemented to periodically scan and update
 * the expired flag on matured trades.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
public interface TradeStoreBatchService extends Runnable {
	
	/** Method to update the expired flag on matured trades periodically.
	 * 
	 * 
	 * @param sleepTime -- Defines the sleep time till next run.
	 * @throws InterruptedException -- thrown by the Thread if it is interrupted()
	 */
	public void updateExpiredFlagOnMaturedTrades(long sleepTime) throws InterruptedException;
}

