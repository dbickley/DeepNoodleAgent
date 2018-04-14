package com.deepnoodle.agent.webserver;

import java.util.Map;
import java.util.Map.Entry;

import com.deepnoodle.agent.dao.StackDao;
import com.deepnoodle.agent.dao.ThreadDao;
import com.deepnoodle.agent.entity.StackEntity;
import com.deepnoodle.agent.entity.ThreadEntity;

public class ThreadUtil {
	public static void captureThreads() {

		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();

		for (Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
			Thread thread = entry.getKey();
			ThreadEntity threadEntity = ThreadDao.instance.findByThreadId(thread.getId());
			if (threadEntity == null) {
				threadEntity = new ThreadEntity();

				threadEntity.setThreadId(thread.getId());
				threadEntity.setName(thread.getName());
				threadEntity.setState(thread.getState().toString());
				threadEntity.setClassName(thread.getClass().getName());
				threadEntity.setCreateTime(System.nanoTime());

				ThreadDao.instance.insert(threadEntity);
				threadEntity = ThreadDao.instance.findByThreadId(thread.getId());
			}
			if (!threadEntity.getState().equals(thread.getState().toString())) {
				ThreadDao.instance.updateThreadState(threadEntity.getId(), thread.getState().toString());
			}

			StackTraceElement[] stackTraceElements = entry.getValue();
			for (StackTraceElement stackTraceElement : stackTraceElements) {
				StackEntity stackEntity = new StackEntity();
				stackEntity.setThreadEntityId(threadEntity.getId());
				stackEntity.setClassName(stackTraceElement.getClassName());
				stackEntity.setMethodName(stackTraceElement.getMethodName());
				stackEntity.setLineNumber(stackTraceElement.getLineNumber());
				stackEntity.setFileName(stackTraceElement.getFileName());
				stackEntity.setCreateTime(System.nanoTime());
				StackDao.instance.insertStackEntity(stackEntity);
			}
		}

	}
}
