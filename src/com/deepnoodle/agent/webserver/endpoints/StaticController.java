package com.deepnoodle.agent.webserver.endpoints;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class StaticController extends BaseController {
	private static final Map<String, String> MIME_MAP = new HashMap<>();
	static {
		MIME_MAP.put("appcache", "text/cache-manifest");
		MIME_MAP.put("css", "text/css");
		MIME_MAP.put("gif", "image/gif");
		MIME_MAP.put("html", "text/html");
		MIME_MAP.put("js", "application/javascript");
		MIME_MAP.put("json", "application/json");
		MIME_MAP.put("jpg", "image/jpeg");
		MIME_MAP.put("jpeg", "image/jpeg");
		MIME_MAP.put("mp4", "video/mp4");
		MIME_MAP.put("pdf", "application/pdf");
		MIME_MAP.put("png", "image/png");
		MIME_MAP.put("svg", "image/svg+xml");
		MIME_MAP.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		MIME_MAP.put("xml", "application/xml");
		MIME_MAP.put("zip", "application/zip");
		MIME_MAP.put("md", "text/plain");
		MIME_MAP.put("txt", "text/plain");
		MIME_MAP.put("php", "text/plain");
	};

	private String urlPrefix = "/files/";
	private String filePrefix = "/public/";
	private String directoryIndex = "index.html";
	private boolean shortpath = true;

	/**
	 * @param urlPrefix The prefix of all URLs.
	 *                   This is the first argument to createContext. Must start and end in a slash.
	 * @param filesystemRoot The root directory in the filesystem.
	 *                       Only files under this directory will be served to the client.
	 *                       For instance "./staticfiles".
	 * @param directoryIndex File to show when a directory is requested, e.g. "index.html".
	 */

	@SuppressWarnings("restriction")
	@Override
	protected StringBuilder getContent(HttpExchange httpExchange) {
		//		String method = httpExchange.getRequestMethod();

		String wholeUrlPath = httpExchange.getRequestURI().getPath();
		if (wholeUrlPath.endsWith("/")) {
			wholeUrlPath += directoryIndex;
		}
		String urlPath = wholeUrlPath.substring(urlPrefix.length());

		String mimeType = lookupMime(urlPath);
		httpExchange.getResponseHeaders().set("Content-Type", mimeType);

		StringBuilder page = null;
		String path = filePrefix + urlPath;
		System.out.println("loading path:" + path);
		try {
			page = super.getPage(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return page;
	}

	//	@Override
	//	public void handle(HttpExchange he) throws IOException {
	//
	//		String method = he.getRequestMethod();
	//		if (!("HEAD".equals(method) || "GET".equals(method))) {
	//			sendError(he, 501, "Unsupported HTTP method");
	//			return;
	//		}
	//
	//		String wholeUrlPath = he.getRequestURI().getPath();
	//		if (wholeUrlPath.endsWith("/")) {
	//			wholeUrlPath += directoryIndex;
	//		}
	//		String urlPath = wholeUrlPath.substring(urlPrefix.length());
	//		if (shortpath) {
	//			return super.getPage(filePrefix + urlPath);
	//		}
	//		File f = new File(filePrefix + urlPath);
	//
	//		File canonicalFile;
	//		try {
	//			canonicalFile = f.getCanonicalFile();
	//		} catch (IOException e) {
	//			// This may be more benign (i.e. not an attack, just a 403),
	//			// but we don't want the attacker to be able to discern the difference.
	//			reportPathTraversal(he);
	//			return;
	//		}
	//
	//		FileInputStream fis;
	//		try {
	//			fis = new FileInputStream(canonicalFile);
	//		} catch (FileNotFoundException e) {
	//			// The file may also be forbidden to us instead of missing, but we're leaking less information this way 
	//			sendError(he, 404, "File not found");
	//			return;
	//		}
	//
	//		String mimeType = lookupMime(urlPath);
	//		he.getResponseHeaders().set("Content-Type", mimeType);
	//		if ("GET".equals(method)) {
	//			he.sendResponseHeaders(200, canonicalFile.length());
	//			OutputStream os = he.getResponseBody();
	//			copyStream(fis, os);
	//			os.close();
	//		} else {
	//			assert ("HEAD".equals(method));
	//			he.sendResponseHeaders(200, -1);
	//		}
	//		fis.close();
	//	}

	//	private void sendError(HttpExchange he, int rCode, String description) throws IOException {
	//		String message = "HTTP error " + rCode + ": " + description;
	//		byte[] messageBytes = message.getBytes("UTF-8");
	//
	//		he.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
	//		he.sendResponseHeaders(rCode, messageBytes.length);
	//		OutputStream os = he.getResponseBody();
	//		os.write(messageBytes);
	//		os.close();
	//	}
	//
	//	// This is one function to avoid giving away where we failed 
	//	private void reportPathTraversal(HttpExchange he) throws IOException {
	//		sendError(he, 400, "Path traversal attempt detected");
	//	}

	private static String getExt(String path) {
		int slashIndex = path.lastIndexOf('/');
		String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

		int dotIndex = basename.lastIndexOf('.');
		if (dotIndex >= 0) {
			return basename.substring(dotIndex + 1);
		} else {
			return "";
		}
	}

	private static String lookupMime(String path) {
		String ext = getExt(path).toLowerCase();
		return MIME_MAP.getOrDefault(ext, "application/octet-stream");
	}

}
