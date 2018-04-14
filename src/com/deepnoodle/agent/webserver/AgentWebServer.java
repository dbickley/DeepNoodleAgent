package com.deepnoodle.agent.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.deepnoodle.agent.webserver.endpoints.ContentController;
import com.deepnoodle.agent.webserver.endpoints.HomeController;
import com.deepnoodle.agent.webserver.endpoints.RestController;
import com.deepnoodle.agent.webserver.endpoints.StaticController;
import com.deepnoodle.agent.webserver.endpoints.TimeLineController;
import com.deepnoodle.agent.webserver.endpoints.TreeMapController;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class AgentWebServer {
	private static final int NUMBER_OF_HTTP_THREADS = 10;
	private HttpServer server;

	@Override
	protected void finalize() throws Throwable {
		server.stop(0);
		super.finalize();
	}

	public AgentWebServer() throws IOException {
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_HTTP_THREADS);

		try {
			server = HttpServer.create(new InetSocketAddress(9999), 0);

			//static
			server.createContext("/files", new StaticController());

			//rest

			server.createContext("/rest", new RestController());
			server.createContext("/content", new ContentController());

			//pages
			HttpHandler homeController = new HomeController();
			server.createContext("/", homeController);
			server.createContext("/home", homeController);
			server.createContext("/treemap", new TreeMapController());
			server.createContext("/timeline", new TimeLineController());

			server.setExecutor(executor);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}