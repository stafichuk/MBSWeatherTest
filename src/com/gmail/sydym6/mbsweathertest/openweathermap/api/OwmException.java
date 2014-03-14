package com.gmail.sydym6.mbsweathertest.openweathermap.api;

public class OwmException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private int code;
	private String url;
	
	OwmException(int code, String message, String url){
        super(message);
        this.code = code;
        this.url = url;
    }

	public int getCode() {
		return code;
	}

	public String getUrl() {
		return url;
	}
    
}
