package com.deepnoodle.agent.webserver.endpoints;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.math.NumberUtils;

import com.deepnoodle.agent.dao.BaseDao;
import com.deepnoodle.agent.dao.ClassDao;
import com.deepnoodle.agent.dao.HitDao;
import com.deepnoodle.agent.dao.MethodDao;
import com.deepnoodle.agent.dao.PackageDao;
import com.deepnoodle.agent.dao.StackDao;
import com.deepnoodle.agent.dao.ThreadDao;
import com.deepnoodle.agent.entity.ClassEntity;
import com.deepnoodle.agent.entity.HitEntity;
import com.deepnoodle.agent.entity.MethodEntity;
import com.deepnoodle.agent.entity.PackageEntity;
import com.deepnoodle.agent.entity.StackEntity;
import com.deepnoodle.agent.entity.ThreadEntity;
import com.deepnoodle.agent.response.ClassResponseModel;
import com.deepnoodle.agent.response.HitResponseModel;
import com.deepnoodle.agent.response.MethodResponseModel;
import com.deepnoodle.agent.response.PackageResponseModel;
import com.deepnoodle.agent.response.StackResponseModel;
import com.deepnoodle.agent.response.ThreadResponseModel;
import com.deepnoodle.agent.util.JsonUtil;
import com.deepnoodle.agent.webserver.ThreadUtil;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class RestController extends BaseController {
	private String basePath = "/rest/";

	@Override
	protected StringBuilder getContent(HttpExchange httpExchange) throws HttpException {
		long startTime = System.nanoTime();
		StringBuilder sb = new StringBuilder();

		URI uri = httpExchange.getRequestURI();
		String path = uri.getPath();
		path = path.replace(basePath, "");
		String[] pathSplit = path.split("/");

		String query = uri.getQuery();
		Queue<String> pathQueue = new LinkedList<>();
		for (String p : pathSplit) {
			pathQueue.add(p);
		}
		List<?> modelReturn = getModels(pathQueue, query);
		if (modelReturn != null) {
			sb.append(JsonUtil.toJson(modelReturn));
		} else {
			//re-init the queue
			pathQueue = new LinkedList<>();
			for (String p : pathSplit) {
				pathQueue.add(p);
			}
			sb.append(getJson(pathQueue, query));
		}

		//if(uri.
		System.out.println("Content generated in " + (System.nanoTime() - startTime) + " nanoseconds");
		return sb;
	}

	public String getJson(Queue<String> pathQueue, String query) throws HttpException {
		String path = pathQueue.poll();
		switch (path) {

			case "all":
				return JsonUtil.toJson(all(pathQueue, query));
			case "memory":
				return JsonUtil.toJson(memory(pathQueue, query));
			case "lastHitsByThreadId":
				return JsonUtil.toJson(lastHitsByThreadId(pathQueue, query));

			default:
				return null;
		}
	}

	public List<?> getModels(Queue<String> pathQueue, String query) throws HttpException {
		String type = pathQueue.poll();
		switch (type) {

			case "classes":

				return classes(pathQueue, query);

			case "hits":
				return hits(pathQueue, query);

			case "methods":
				return methods(pathQueue, query);

			case "packages":
				return packages(pathQueue, query);

			case "stacks":
				return stacks(pathQueue, query);
			case "threads":
				return threads(pathQueue, query);

			default:
				return null;
		}
	}

	private Object all(Queue<String> pathQueue, String query) throws HttpException {
		Map<String, String> all = new HashMap<>();

		all.put("hits", JsonUtil.toJson(hits(pathQueue, query)));

		all.put("packages", JsonUtil.toJson(packages(pathQueue, query)));

		all.put("classes", JsonUtil.toJson(classes(pathQueue, query)));

		all.put("methods", JsonUtil.toJson(methods(pathQueue, query)));

		all.put("threads", JsonUtil.toJson(threads(pathQueue, query)));

		all.put("stacks", JsonUtil.toJson(stacks(pathQueue, query)));

		return new StringBuilder(JsonUtil.toJson(all));
	}

	private List<ClassResponseModel> classes(Queue<String> pathQueue, String query) throws HttpException {
		List<ClassEntity> entities = baseRestCommands(ClassDao.instance, pathQueue, query);

		List<ClassResponseModel> responseModels = new ArrayList<>();
		if (entities != null) {
			for (ClassEntity entity : entities) {
				ClassResponseModel response = new ClassResponseModel(entity);
				String path = pathQueue.poll();
				if (path != null && path.equalsIgnoreCase("methods")) {
					List<MethodResponseModel> childResponseModels = methods(pathQueue, query);
					response.setMethods(childResponseModels);
				}
				responseModels.add(response);
			}
		}
		return responseModels;
	}

	private List<HitResponseModel> hits(Queue<String> pathQueue, String query) throws HttpException {
		List<HitEntity> entities = baseRestCommands(HitDao.instance, pathQueue, query);

		List<HitResponseModel> responseModels = new ArrayList<>();
		if (entities != null) {
			for (HitEntity entity : entities) {
				HitResponseModel response = new HitResponseModel(entity);
				responseModels.add(response);
			}
		}

		//build children/ add query parameter?
		List<HitResponseModel> response = new ArrayList<>();
		Map<Long, HitResponseModel> entitiesById = new HashMap<>();
		for (HitResponseModel entity : responseModels) {
			entitiesById.put(entity.getId(), entity);
		}
		for (HitResponseModel entity : responseModels) {
			HitResponseModel parentEntity = entitiesById.get(entity.getCallingHitId());
			if (parentEntity != null) {
				parentEntity.getChildren().add(entity);
			} else {
				response.add(entity);
			}
		}
		return response;
	}

	private List<MethodResponseModel> methods(Queue<String> pathQueue, String query) throws HttpException {
		List<MethodEntity> entities = baseRestCommands(MethodDao.instance, pathQueue, query);

		List<MethodResponseModel> responseModels = new ArrayList<>();
		if (entities != null) {
			for (MethodEntity entity : entities) {
				MethodResponseModel response = new MethodResponseModel(entity);
				String path = pathQueue.poll();
				if (path != null && path.equalsIgnoreCase("hits")) {
					List<HitResponseModel> childResponseModels = hits(pathQueue, query);
					response.setHits(childResponseModels);
				}
				responseModels.add(response);
			}
		}
		return responseModels;
	}

	private List<PackageResponseModel> packages(Queue<String> pathQueue, String query) throws HttpException {
		List<PackageEntity> entities = baseRestCommands(PackageDao.instance, pathQueue, query);

		List<PackageResponseModel> responseModels = new ArrayList<>();
		if (entities != null) {
			for (PackageEntity entity : entities) {
				PackageResponseModel response = new PackageResponseModel(entity);
				String path = pathQueue.poll();
				if (path != null && path.equalsIgnoreCase("classes")) {
					List<ClassResponseModel> childResponseModels = classes(pathQueue, query);
					response.setClasses(childResponseModels);
				}

				responseModels.add(response);
			}
		}

		//build children
		Map<Long, PackageResponseModel> entitiesById = new HashMap<>();
		for (PackageResponseModel entity : responseModels) {
			entitiesById.put(entity.getId(), entity);
		}
		for (PackageResponseModel entity : responseModels) {
			PackageResponseModel parentEntity = entitiesById.get(entity.getParentPackageId());
			if (parentEntity != null) {
				parentEntity.getChildren().add(entity);
			}
		}

		return responseModels;

	}

	private List<StackResponseModel> stacks(Queue<String> pathQueue, String query) throws HttpException {
		List<StackEntity> entities = baseRestCommands(StackDao.instance, pathQueue, query);

		List<StackResponseModel> responseModels = new ArrayList<>();
		if (entities != null) {
			for (StackEntity entity : entities) {
				StackResponseModel response = new StackResponseModel(entity);
				responseModels.add(response);
			}
		}
		return responseModels;
	}

	private List<ThreadResponseModel> threads(Queue<String> pathQueue, String query) throws HttpException {
		ThreadUtil.captureThreads();
		List<ThreadEntity> entities = baseRestCommands(ThreadDao.instance, pathQueue, query);

		List<ThreadResponseModel> responseModels = new ArrayList<>();
		if (entities != null) {
			for (ThreadEntity entity : entities) {
				ThreadResponseModel response = new ThreadResponseModel(entity);
				String path = pathQueue.poll();
				if (path != null && path.equalsIgnoreCase("stacks")) {
					List<StackResponseModel> childResponseModels = stacks(pathQueue, query);
					response.setStacks(childResponseModels);
				}
				responseModels.add(response);
			}
		}
		return responseModels;
	}

	public <T> List<T> baseRestCommands(BaseDao<T> dao, Queue<String> pathQueue, String query) throws HttpException {
		List<T> entities = null;
		String path = pathQueue.peek();

		if (NumberUtils.isCreatable(path)) {
			String id = pathQueue.poll();
			entities = dao.selectById(Long.valueOf(id), query);
			if (entities == null || entities.isEmpty()) {
				throw new HttpException(404, "Item can not be found");
			}
		} else {
			entities = dao.select(1000, query);
		}
		return entities;

	}

	private String memory(Queue<String> pathQueue, String query) {
		Runtime rt = Runtime.getRuntime();
		Map<String, Object> memory = new HashMap<>();
		memory.put("total_memory", rt.totalMemory());
		memory.put("free_memory", rt.freeMemory());
		memory.put("used_memory", rt.totalMemory() - rt.freeMemory());
		memory.put("max_memory", rt.maxMemory());
		memory.put("available_processors", rt.availableProcessors());
		String key = pathQueue.poll();
		if (key != null) {
			return JsonUtil.toJson(memory.get(key));
		} else {
			return JsonUtil.toJson(memory);
		}
	}

	private StringBuilder lastHitsByThreadId(Queue<String> pathQueue, String query) {

		List<HitEntity> lastHits = HitDao.instance.select(1000);

		Map<Long, List<HitEntity>> lastHitsThreadMap = new HashMap<>();
		for (HitEntity hit : lastHits) {
			List<HitEntity> list = lastHitsThreadMap.get(hit.getThreadId());
			if (list == null) {
				list = new ArrayList<>();
				lastHitsThreadMap.put(hit.getThreadId(), list);
			}
			list.add(hit);
		}

		return new StringBuilder(JsonUtil.toJson(lastHits));
	}

	private StringBuilder lastHitsParentChild(Queue<String> pathQueue, String query) {

		List<HitEntity> lastHits = HitDao.instance.select(10000);

		Map<Long, List<HitEntity>> lastHitsThreadMap = new HashMap<>();
		Map<Long, HitEntity> lastHitsMap = new HashMap<>();

		//build indexes
		for (HitEntity hit : lastHits) {
			List<HitEntity> list = lastHitsThreadMap.get(hit.getThreadId());
			if (list == null) {
				list = new ArrayList<>();
				lastHitsThreadMap.put(hit.getThreadId(), list);
			}
			list.add(hit);

			lastHitsMap.put(hit.getId(), hit);

		}

		return new StringBuilder(JsonUtil.toJson(lastHits));
	}

}
