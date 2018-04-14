package com.deepnoodle.agent.webserver.endpoints;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class TreeMapController extends BaseController {
	protected String pageUrl = "/private/treemap.html";

	public void Home() {

	}

	@Override
	protected StringBuilder getContent(HttpExchange httpExchange) {
		StringBuilder sb = null;
		try {
			sb = getPage(pageUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

}
