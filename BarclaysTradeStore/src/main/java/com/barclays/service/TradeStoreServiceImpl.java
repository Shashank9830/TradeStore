package com.barclays.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barclays.dto.Expired;
import com.barclays.dto.TradeDTO;
import com.barclays.entity.Trade;
import com.barclays.exception.BarclaysTradeStoreException;
import com.barclays.repository.TradeStoreRepository;

/** Implementation of TradeStoreService interface.
 * 
 * Implements both high and low memory usage versions of trade processing.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
@Service(value="tradeStoreService")
@Transactional
public class TradeStoreServiceImpl implements TradeStoreService {

	@Autowired
	private TradeStoreRepository tradeStoreRepository;
	
	
	/** Performs validation on the incoming trade and adds it to the store if successful.
	 * 
	 * Trade with a maturity date of past is rejected.
	 * 
	 * All trades with the same trade id as the request are fetched from the database.
	 * The trade list is then iterated to find a higher version trade, presence of which 
	 * would invalidate the current requested trade.
	 * 
	 * If a higher version trade is not found then equal version trade is also checked.
	 * If found, the existing is overwritten with new trade information.
	 * 
	 * If not found, then a new entry is created for the trade.
	 * 
	 * If an existing trade has matured, expired flag is changed to Y.
	 * 
	 * Here, DB is accessed only once and all the trades in memory. This increases memory
	 * usage. Also, the processing for finding greater and equal is done at application end.
	 * This makes this method more costly at application end.
	 * 
	 * 
	 * @param receivedTrade -- Trade DTO for the received trade information.
	 * @return -- Trade entity of the entry that was saved in the database.
	 * @throws BarclaysTradeStoreException -- thrown when a trade is rejected.
	 */
	@Override
	public Trade processTrade(TradeDTO receivedTrade) throws BarclaysTradeStoreException {
		
		if (receivedTrade.getMaturityDate().isBefore(LocalDate.now()))
			throw new BarclaysTradeStoreException("TradeStoreService.INVALID_MATURITY_DATE");
		
		Trade trade=null;
		synchronized (tradeStoreRepository) {
			
			List<Trade> trades=tradeStoreRepository.findByTradeId(receivedTrade.getTradeId());
			
			for (Trade tmpTrade: trades) {
				if (tmpTrade.getVersion()>receivedTrade.getVersion())
					throw new BarclaysTradeStoreException("TradeStoreService.LOWER_VERSION_TRADE");
				if (tmpTrade.getVersion()==receivedTrade.getVersion())
					trade=tmpTrade;
			}
			
			if (trade==null)
				trade=new Trade();
			
			trade.setTradeId(receivedTrade.getTradeId());
			trade.setVersion(receivedTrade.getVersion());
			trade.setCounterPartyId(receivedTrade.getCounterPartyId());
			trade.setBookId(receivedTrade.getBookId());
			trade.setMaturityDate(receivedTrade.getMaturityDate());
			trade.setCreatedDate(receivedTrade.getCreatedDate());
			//trade.setCreatedDate(LocalDate.now());
			trade.setExpired(receivedTrade.getExpired());
			
			if (trade.getMaturityDate().isBefore(LocalDate.now()))
				trade.setExpired(Expired.Y);
			
			trade=tradeStoreRepository.save(trade);
			tradeStoreRepository.notifyAll();
		}
		
		return trade;
	}

	/** Performs validation on the incoming trade and adds it to the store if successful.
	 * 
	 * Trade with a maturity date of past is rejected.
	 * 
	 * All trades with the same trade id and a greater version as the request are fetched
	 * from the database. If the query results in a non-empty list then trade is rejected.
	 * 
	 * If a higher version trade is not found then equal version trade is also checked.
	 * If found, the existing is overwritten with new trade information.
	 * 
	 * If not found, then a new entry is created for the trade.
	 * 
	 * If an existing trade has matured, expired flag is changed to Y.
	 * 
	 * Here, finding greater or equal version is done by the DBMS not the application. 
	 * This reduces memory usage and processing overhead from application end. 
	 * This method is costly at DBMS end.
	 * 
	 * 
	 * @param receivedTrade -- Trade DTO for the received trade information.
	 * @return -- Trade entity of the trade entry that was saved in the database.
	 * @throws BarclaysTradeStoreException -- thrown when a trade is rejected.
	 */
	@Override
	public Trade processTradeWithLessMemory(TradeDTO receivedTrade) throws BarclaysTradeStoreException {
		
		if (receivedTrade.getMaturityDate().isBefore(LocalDate.now()))
			throw new BarclaysTradeStoreException("TradeStoreService.INVALID_MATURITY_DATE");
		
		Trade trade=null;
		synchronized (tradeStoreRepository) {
			
			List<Trade> trades=tradeStoreRepository.findByTradeIdAndVersionGreaterThan(receivedTrade.getTradeId(), receivedTrade.getVersion());
			if (!trades.isEmpty())
				throw new BarclaysTradeStoreException("TradeStoreService.LOWER_VERSION_TRADE");
			
			Optional<Trade> optional=tradeStoreRepository.findOneByTradeIdAndVersion(receivedTrade.getTradeId(), receivedTrade.getVersion());
			trade=optional.orElse(new Trade());
			
			trade.setTradeId(receivedTrade.getTradeId());
			trade.setVersion(receivedTrade.getVersion());
			trade.setCounterPartyId(receivedTrade.getCounterPartyId());
			trade.setBookId(receivedTrade.getBookId());
			trade.setMaturityDate(receivedTrade.getMaturityDate());
			trade.setCreatedDate(receivedTrade.getCreatedDate());
			//trade.setCreatedDate(LocalDate.now());
			trade.setExpired(receivedTrade.getExpired());
			
			if (trade.getMaturityDate().isBefore(LocalDate.now()))
				trade.setExpired(Expired.Y);
			
			trade=tradeStoreRepository.save(trade);
			tradeStoreRepository.notifyAll();
		}
		
		return trade;
	}
}
