package com.deepnoodle.agent.webserver.endpoints;

public class HttpException extends Exception {

	private int responseCode;
	private String message;

	public HttpException(int responseCode, String message) {
		this.responseCode = responseCode;
		this.message = message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
