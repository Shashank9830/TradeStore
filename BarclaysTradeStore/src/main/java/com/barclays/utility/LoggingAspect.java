package com.barclays.utility;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Logging using Aspect Oriented Programming (AOP).
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
@Component
@Aspect
public class LoggingAspect {
	public static final Log LOGGER = LogFactory.getLog(LoggingAspect.class);

	/** Method to log exceptions thrown by the service class implementation.
	 * 
	 * 
	 * @param exception -- Exception thrown by the store service class. 
	 */
	@AfterThrowing(pointcut = "execution(* com.barclays.service.*Impl.*(..))", throwing = "exception")
	public void logServiceException(Exception exception) {
		LOGGER.error(exception.getMessage(), exception);
	}

}
