package com.log;

public class LoggerFactory {
	
	public static com.log.Logger getLogger(Class clazz) {
		return new Logger();
	}

}
