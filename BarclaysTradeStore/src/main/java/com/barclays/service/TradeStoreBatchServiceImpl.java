package com.barclays.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.barclays.dto.Expired;
import com.barclays.entity.Trade;
import com.barclays.repository.TradeStoreRepository;

/** Implementation of the TradeStoreBatchService interface.
 * Finds trades inside the store for which maturity date is a 
 * past date. Updates their expired flag to Y if they're marked as N.
 * 
 * Performs this task after fixed intervals. Interval size is defined
 * in application.properties using BatchService.SLEEP_TIME key.
 * 
 * Time is defined in milliseconds. Default sleep time is 24 hours or
 * 86400000 ms.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
@Service(value="tradeStoreBatchService")
public class TradeStoreBatchServiceImpl implements TradeStoreBatchService {

	@Autowired
	private TradeStoreRepository tradeStoreRepository;
	
	@Autowired
	Environment environment;
	
	/** Thread execution starts from this method.
	 * Reads the sleep time property from application property and
	 * calls appropriate method to update the expired flag.
	 */
	@Override
	public void run() {
		long sleepTime=Long.parseLong(
				environment.getProperty("BatchService.SLEEP_TIME", String.class, "86400000"));
		try {
			updateExpiredFlagOnMaturedTrades(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** Method to perform the actual updation in DB.
	 * Method is marked with Transactional annotation to enable
	 * DB transactions. 
	 * 
	 * It gains a lock on the repository object before performing DB related operations.
	 * 
	 * Finds all the trades with maturity date of past and expired flag set to N.
	 * 
	 * It then updates the expired flag to Y for all of them.
	 * 
	 * Releases the lock after completion.
	 */
	@Transactional
	private void updateRecordsInDB () {
		synchronized (tradeStoreRepository) {
			List<Trade> trades=tradeStoreRepository
					.findByMaturityDateLessThanAndExpiredEquals(LocalDate.now(), Expired.N);
			for (Trade trade: trades)
				trade.setExpired(Expired.Y);
			tradeStoreRepository.saveAll(trades);
			tradeStoreRepository.notifyAll();
		}
	}
	
	/** Keeps the process alive and runs update after defined interval.
	 */
	@Override
	public void updateExpiredFlagOnMaturedTrades(long sleepTime) throws InterruptedException {
		
		while (true) {
			updateRecordsInDB();
			Thread.sleep(sleepTime);
		}
	}
}
