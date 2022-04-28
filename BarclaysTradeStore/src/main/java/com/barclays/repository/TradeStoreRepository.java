package com.barclays.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.barclays.dto.Expired;
import com.barclays.entity.Trade;


/** Data Access Object (DAO)/Repository interface created using Spring Data JPA.
 * 
 * This is automatically implemented by Spring via Hibernate.
 * 
 * Contains named queries for easy retrieval of data from database for various
 * type of queries.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
public interface TradeStoreRepository extends CrudRepository<Trade, Integer> {
	
	
	/** Finds all the trades that match with a specific trade id.
	 * 
	 * 
	 * @param tradeId -- string value for trade id. Like "T1".
	 * @return -- List of Trade entities that match the mentioned criteria.
	 */
	public List<Trade> findByTradeId(String tradeId);
	
	/** Finds all the trades that match a specific trade id and have
	 * version greater than the one passed to this method.
	 * 
	 * 
	 * @param tradeId -- string value for trade id. Like "T1".
	 * @param version -- integer value for trade version. Like 1.
	 * @return -- List of Trade entities that match the mentioned criteria.
	 */
	public List<Trade> findByTradeIdAndVersionGreaterThan(String tradeId, int version);
	
	/** Finds all the trades that match a specific trade id and version.
	 * 
	 * 
	 * @param tradeId -- string value for trade id. Like "T1".
	 * @param version -- integer value for trade version. Like 1.
	 * @return -- List of Trade entities that match the mentioned criteria.
	 */
	public Optional<Trade> findOneByTradeIdAndVersion(String tradeId, int version);
	
	
	public List<Trade> findByMaturityDateLessThanAndExpiredEquals(LocalDate today, Expired expired);
}
