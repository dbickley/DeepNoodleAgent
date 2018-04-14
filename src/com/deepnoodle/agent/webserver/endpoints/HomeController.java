package com.deepnoodle.agent.webserver.endpoints;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class HomeController extends BaseController {
	protected String pageUrl = "/private/home.html";

	public void Home() {

	}

	@Override
	protected StringBuilder getContent(HttpExchange httpExchange) {
		StringBuilder sb = null;
		try {
			sb = getPage(pageUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}

}
