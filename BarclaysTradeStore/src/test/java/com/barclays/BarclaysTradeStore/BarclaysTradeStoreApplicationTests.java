package com.barclays.BarclaysTradeStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.barclays.dto.Expired;
import com.barclays.dto.TradeDTO;
import com.barclays.entity.Trade;
import com.barclays.exception.BarclaysTradeStoreException;
import com.barclays.repository.TradeStoreRepository;
import com.barclays.service.TradeStoreService;
import com.barclays.service.TradeStoreServiceImpl;

/** SpringBoot Test class. Used for writing and executing Unit tests.
 * 
 * Only service layer components are tested here despite the fact that application
 * has a persistence and presentation layer.
 * 
 * The persistence layer is created by extending the CrudRepository interface,
 * implementation for which is automatically generated by the Spring Framework.
 * Hence, unit testing is not done for it.
 * 
 * Persistence layer is mocked to achieve loose coupling and independent testing of
 * business logic present in the service layer.
 * 
 * The presentation layer (or REST API) is created only for transmission of trades 
 * and not for any business logic. Hence, unit testing is not done for it either.
 * Consider it out of scope for this problem statement.
 * 
 * Service layer code is tested for both the implementations of trade processor.
 * 
 * There are four test cases for each implementation. A total of 8 unit test cases
 * are written.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
@SpringBootTest
class BarclaysTradeStoreApplicationTests {
	
	@Mock
	TradeStoreRepository tradeStoreRepository;
	
	@InjectMocks
	TradeStoreService tradeStoreService=new TradeStoreServiceImpl();
	
	/** Test case for a new trade. No trade with same trade id exists in the database.
	 * Trade should be successfully added without any exceptions.
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeTestNewTrade() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2025, 5, 30));
		tradeDto.setCreatedDate(LocalDate.now());
		tradeDto.setExpired(Expired.N);
		
		Trade trade=new Trade();
		trade.setTradeId(tradeDto.getTradeId());
		trade.setVersion(tradeDto.getVersion());
		trade.setCounterPartyId(tradeDto.getCounterPartyId());
		trade.setMaturityDate(tradeDto.getMaturityDate());
		trade.setCreatedDate(tradeDto.getCreatedDate());
		trade.setExpired(tradeDto.getExpired());
		
		Mockito.when(tradeStoreRepository.findByTradeId(Mockito.anyString())).thenReturn(new ArrayList<Trade>());
		Mockito.when(tradeStoreRepository.save(Mockito.any(Trade.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		
		Assertions.assertEquals(trade, tradeStoreService.processTrade(tradeDto));
	}
	
	/** Test case for a trade with invalid maturity date.
	 * Trade should be rejected with a BarclaysTradeStoreException having 
	 * "TradeStoreService.INVALID_MATURITY_DATE" message.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeTestInvalidMaturityDate() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2020, 5, 30));
		tradeDto.setCreatedDate(LocalDate.of(2019, 5, 30));
		tradeDto.setExpired(Expired.Y);
		
		BarclaysTradeStoreException ex=Assertions.assertThrows(BarclaysTradeStoreException.class, ()->tradeStoreService.processTrade(tradeDto));
		Assertions.assertEquals("TradeStoreService.INVALID_MATURITY_DATE", ex.getMessage());
	}
	
	/** Test case for a trade with lower trade version.
	 * Trade should be rejected with a BarclaysTradeStoreException having 
	 * "TradeStoreService.LOWER_VERSION_TRADE" message.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeTestLowerVersion() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2025, 5, 30));
		tradeDto.setCreatedDate(LocalDate.now());
		tradeDto.setExpired(Expired.N);
		
		Trade trade=new Trade();
		trade.setTradeId(tradeDto.getTradeId());
		trade.setVersion(tradeDto.getVersion()+1);
		trade.setCounterPartyId(tradeDto.getCounterPartyId());
		trade.setMaturityDate(tradeDto.getMaturityDate());
		trade.setCreatedDate(tradeDto.getCreatedDate());
		trade.setExpired(tradeDto.getExpired());
		ArrayList<Trade> resultSet=new ArrayList<>();
		resultSet.add(trade);
		
		Mockito.when(tradeStoreRepository.findByTradeId(Mockito.anyString())).thenReturn(resultSet);
		
		BarclaysTradeStoreException ex=Assertions.assertThrows(BarclaysTradeStoreException.class, ()->tradeStoreService.processTrade(tradeDto));
		Assertions.assertEquals("TradeStoreService.LOWER_VERSION_TRADE", ex.getMessage());
	}
	
	/** Test case for a trade with existing entry.
	 * Same entry should be over-written.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeTestExistingVersion() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2025, 5, 30));
		tradeDto.setCreatedDate(LocalDate.now());
		tradeDto.setExpired(Expired.N);
		
		ArrayList<Trade> resultSet=new ArrayList<>();
		Trade trade=new Trade();
		trade.setTradeId(tradeDto.getTradeId());
		trade.setVersion(tradeDto.getVersion());
		trade.setCounterPartyId("CP-2");
		trade.setMaturityDate(tradeDto.getMaturityDate());
		trade.setCreatedDate(tradeDto.getCreatedDate());
		trade.setExpired(tradeDto.getExpired());
		resultSet.add(trade);
		trade.setCounterPartyId(tradeDto.getCounterPartyId());
		
		Mockito.when(tradeStoreRepository.findByTradeId(Mockito.anyString())).thenReturn(resultSet);
		Mockito.when(tradeStoreRepository.save(Mockito.any(Trade.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		
		Assertions.assertEquals(trade.getCounterPartyId(), tradeStoreService.processTrade(tradeDto).getCounterPartyId());
	}
	
	/** Test case for a new trade. No trade with same trade id exists in the database.
	 * Trade should be successfully added without any exceptions.
	 * 
	 * Executed on low memory implementation of trade processor.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeWithLessMemoryTestNewTrade() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2025, 5, 30));
		tradeDto.setCreatedDate(LocalDate.now());
		tradeDto.setExpired(Expired.N);
		
		Trade trade=new Trade();
		trade.setTradeId(tradeDto.getTradeId());
		trade.setVersion(tradeDto.getVersion());
		trade.setCounterPartyId(tradeDto.getCounterPartyId());
		trade.setMaturityDate(tradeDto.getMaturityDate());
		trade.setCreatedDate(tradeDto.getCreatedDate());
		trade.setExpired(tradeDto.getExpired());
		
		Mockito.when(tradeStoreRepository.findByTradeIdAndVersionGreaterThan(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<Trade>());
		Mockito.when(tradeStoreRepository.save(Mockito.any(Trade.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		
		Assertions.assertEquals(trade, tradeStoreService.processTradeWithLessMemory(tradeDto));
	}
	
	/** Test case for a trade with invalid maturity date.
	 * Trade should be rejected with a BarclaysTradeStoreException having 
	 * "TradeStoreService.INVALID_MATURITY_DATE" message.
	 * 
	 * Executed on low memory implementation of trade processor.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeWithLessMemoryTestInvalidMaturityDate() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2020, 5, 30));
		tradeDto.setCreatedDate(LocalDate.of(2019, 5, 30));
		tradeDto.setExpired(Expired.Y);
		
		BarclaysTradeStoreException ex=Assertions.assertThrows(BarclaysTradeStoreException.class, ()->tradeStoreService.processTradeWithLessMemory(tradeDto));
		Assertions.assertEquals("TradeStoreService.INVALID_MATURITY_DATE", ex.getMessage());
	}
	
	/** Test case for a trade with lower trade version.
	 * Trade should be rejected with a BarclaysTradeStoreException having 
	 * "TradeStoreService.LOWER_VERSION_TRADE" message.
	 * 
	 * Executed on low memory implementation of trade processor.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeWithLessMemoryTestLowerVersion() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2025, 5, 30));
		tradeDto.setCreatedDate(LocalDate.now());
		tradeDto.setExpired(Expired.N);
		
		Trade trade=new Trade();
		trade.setTradeId(tradeDto.getTradeId());
		trade.setVersion(tradeDto.getVersion()+1);
		trade.setCounterPartyId(tradeDto.getCounterPartyId());
		trade.setMaturityDate(tradeDto.getMaturityDate());
		trade.setCreatedDate(tradeDto.getCreatedDate());
		trade.setExpired(tradeDto.getExpired());
		ArrayList<Trade> resultSet=new ArrayList<>();
		resultSet.add(trade);
		
		Mockito.when(tradeStoreRepository.findByTradeIdAndVersionGreaterThan(tradeDto.getTradeId(), tradeDto.getVersion())).thenReturn(resultSet);
		
		BarclaysTradeStoreException ex=Assertions.assertThrows(BarclaysTradeStoreException.class, ()->tradeStoreService.processTradeWithLessMemory(tradeDto));
		Assertions.assertEquals("TradeStoreService.LOWER_VERSION_TRADE", ex.getMessage());
	}
	
	/** Test case for a trade with existing entry.
	 * Same entry should be over-written.
	 * 
	 * Executed on low memory implementation of trade processor.
	 * 
	 * 
	 * @throws BarclaysTradeStoreException -- thrown for bad requests.
	 */
	@Test
	void processTradeWithLessMemoryTestExistingVersion() throws BarclaysTradeStoreException {
		
		TradeDTO tradeDto=new TradeDTO();
		tradeDto.setTradeId("T3");
		tradeDto.setVersion(1);
		tradeDto.setCounterPartyId("CP-1");
		tradeDto.setMaturityDate(LocalDate.of(2025, 5, 30));
		tradeDto.setCreatedDate(LocalDate.now());
		tradeDto.setExpired(Expired.N);
		
		Trade trade=new Trade();
		trade.setTradeId(tradeDto.getTradeId());
		trade.setVersion(tradeDto.getVersion());
		trade.setCounterPartyId("CP-2");
		trade.setMaturityDate(tradeDto.getMaturityDate());
		trade.setCreatedDate(tradeDto.getCreatedDate());
		trade.setExpired(tradeDto.getExpired());
		
		Mockito.when(tradeStoreRepository.findByTradeIdAndVersionGreaterThan(tradeDto.getTradeId(), tradeDto.getVersion())).thenReturn(new ArrayList<>());
		Mockito.when(tradeStoreRepository.findOneByTradeIdAndVersion(tradeDto.getTradeId(), tradeDto.getVersion())).thenReturn(Optional.of(trade));
		Mockito.when(tradeStoreRepository.save(Mockito.any(Trade.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		
		Assertions.assertEquals(tradeDto.getCounterPartyId(), tradeStoreService.processTradeWithLessMemory(tradeDto).getCounterPartyId());
	}
}