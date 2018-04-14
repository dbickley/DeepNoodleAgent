package com.deepnoodle.agent.webserver.endpoints;

import java.sql.SQLException;

import com.deepnoodle.agent.util.JsonUtil;
import com.deepnoodle.agent.webserver.CurrentStatusBuilder;
import com.sun.net.httpserver.HttpExchange;

public class ContentController extends BaseController {

	@Override
	protected StringBuilder getContent(HttpExchange httpExchange) {
		try {
			return new StringBuilder(JsonUtil.toJson(CurrentStatusBuilder.getCurrentStatus()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
