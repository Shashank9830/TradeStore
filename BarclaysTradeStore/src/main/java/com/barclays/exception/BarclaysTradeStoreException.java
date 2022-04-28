package com.barclays.exception;

/** Custom exception class to properly define Store related exceptions.
 * 
 * Validation errors like invalid maturity date or low version trade lead
 * to this exception being thrown.
 * 
 * @author Shashank Singh
 *
 */
public class BarclaysTradeStoreException extends Exception {

	private static final long serialVersionUID = 1L;

	public BarclaysTradeStoreException(String message) {
		super(message);
	}

}
