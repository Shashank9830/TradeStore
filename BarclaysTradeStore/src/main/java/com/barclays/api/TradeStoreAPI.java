package com.barclays.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.dto.TradeDTO;
import com.barclays.exception.BarclaysTradeStoreException;
import com.barclays.service.TradeStoreService;


/** REST API to serve store services over HTTP.
 * This API is meant purely for transmission of trade data.
 * It doesn't have any logic for validation or processing.
 * 
 * This is done on purpose keeping the problem statement in mind.
 * Transmission medium could be anything, hence writing business logic 
 * here doesn't make any sense.
 * 
 * Base URL is "/api/v1/barclays/store" for all endpoints.
 * Allows requests from any origin to avoid CORS errors.
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 * 
 */
@RestController
@RequestMapping(value="/api/v1/barclays/store")
@CrossOrigin
public class TradeStoreAPI {
	
	@Autowired
	private TradeStoreService tradeStoreService;
	
	@Autowired
	private Environment environment;
	
	
	/** REST endpoint to receive trade information over HTTP POST.
	 * Trade information received here is passed to the service layer.
	 * 
	 * @param receivedTrade -- payload containing trade information.
	 * @return -- returns HTTP response with status code 200 when a trade is successful.
	 * @throws BarclaysTradeStoreException -- thrown from the service class for bad requests.
	 */
	@PostMapping(value="/trades")
	public ResponseEntity<String> processTrade(@RequestBody TradeDTO receivedTrade) throws BarclaysTradeStoreException {
		
		//tradeStoreService.processTradeWithLessMemory(receivedTrade);
		tradeStoreService.processTrade(receivedTrade);
		String successMessage = environment.getProperty("API.TRADE_SUCCESS");
		return new ResponseEntity<>(successMessage, HttpStatus.OK);
	}
}
