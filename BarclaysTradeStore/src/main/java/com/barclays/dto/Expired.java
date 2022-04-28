package com.barclays.dto;

/** Enum used to check whether a trade has matured or not.
 * Marked as 'Y' for trades where maturity date is a past date.
 * Marked as 'N' for trades where maturity date is a present or future date.
 * 
 * @author Shashank Singh
 *
 */
public enum Expired {
	N, Y;
}
