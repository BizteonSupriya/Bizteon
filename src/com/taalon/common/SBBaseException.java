package com.taalon.common;

public class SBBaseException extends Exception {
	private static final long serialVersionUID = 1997753363232807009L;
	
	public SBBaseException() {
		// Do nothing
	}
	
	public SBBaseException(String message) {
		super(message);
		System.out.println("MESSAGE - " + message);
	}
	
	public SBBaseException(Throwable cause) {
		super(cause);
	}
}
