package com.deepnoodle.agent.webserver.endpoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public abstract class BaseController implements HttpHandler {
	protected String pageUrl;

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			StringBuilder sb = new StringBuilder();
			int responseCode = 200;
			try {
				sb = getContent(httpExchange);
				if (sb == null) {
					throw new HttpException(500, "There was an error retriving content");
				}

			} catch (HttpException e) {
				sb.append(e.getMessage());
				responseCode = e.getResponseCode();
			} catch (Exception e) {
				e.printStackTrace();
				sb.append(e.getMessage());
				responseCode = 500;
			} finally {
				String response = sb.toString();
				byte[] responseBytes = response.getBytes();
				httpExchange.sendResponseHeaders(responseCode, responseBytes.length);
				outputStream.write(responseBytes);
			}
		}

	}

	protected abstract StringBuilder getContent(HttpExchange httpExchange) throws HttpException;

	protected StringBuilder getPage(String file) throws IOException {
		StringBuilder page = new StringBuilder();
		InputStream is = getClass().getResourceAsStream(file);
		if (is == null) {
			System.out.println("file not found: " + file);

			return page;
		}
		String line = null;
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			while ((line = buffer.readLine()) != null) {
				page.append(line);
				page.append(System.getProperty("line.separator"));
			}
			return page;
		}

	}

	protected StringBuffer setContent(StringBuffer sb, String variable, String content) {
		int start = sb.indexOf(variable);
		if (start >= 0) {
			return sb.replace(start, variable.length() + start, content);
		}
		return sb;

	}

}
