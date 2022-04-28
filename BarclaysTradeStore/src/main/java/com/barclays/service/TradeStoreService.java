package com.barclays.service;

import com.barclays.dto.TradeDTO;
import com.barclays.entity.Trade;
import com.barclays.exception.BarclaysTradeStoreException;

/** Interface defining what services are offered by the store.
 * 
 * Implementation must have two different methods for processing trades.
 * 
 * First one should have minimal DB access.
 * Second one should have less memory usage allowing for frequent DB access.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
public interface TradeStoreService {
	
	
	/** Processes trade with minimal DB access resulting in high memory usage
	 * by the application.
	 * 
	 * 
	 * @param trade -- Trade DTO object containing trade information.
	 * @return -- Trade entity that is saved to the database.
	 * @throws BarclaysTradeStoreException -- thrown when trade request is bad.
	 */
	public Trade processTrade(TradeDTO trade) throws BarclaysTradeStoreException;
	
	/** Processes trade with frequent DB access resulting in low memory usage
	 * by the application. It is due to the fact that complex processing is done
	 * by the DBMS instead of the application.
	 * 
	 * 
	 * @param trade -- Trade DTO object containing trade information.
	 * @return -- Trade entity that is saved to the database.
	 * @throws BarclaysTradeStoreException -- thrown when trade request is bad.
	 */
	public Trade processTradeWithLessMemory(TradeDTO trade) throws BarclaysTradeStoreException;
}
