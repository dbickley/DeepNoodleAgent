package com.deepnoodle.agent.webserver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.deepnoodle.agent.dao.HitDao;
import com.deepnoodle.agent.dao.ThreadDao;
import com.deepnoodle.agent.entity.HitEntity;
import com.deepnoodle.agent.entity.ThreadEntity;

public class CurrentStatusBuilder {

	public static CurrentStatus getCurrentStatus() throws SQLException {
		CurrentStatus currentStatus = new CurrentStatus();

		StringBuffer content = new StringBuffer();
		Runtime rt = Runtime.getRuntime();
		currentStatus.setTotalMemory(rt.totalMemory());
		currentStatus.setFreeMemory(rt.freeMemory());
		currentStatus.setUsedMemory(rt.totalMemory() - rt.freeMemory());
		currentStatus.setMaxMemory(rt.maxMemory());
		currentStatus.setAvailableProcessors(rt.availableProcessors());

		List<ThreadEntity> threads = ThreadDao.instance.select(1000);
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

		currentStatus.setLastHits(lastHitsThreadMap);

		//		List<TopHit> longestHits = Database.selectLongestHits(100);
		//		currentStatus.setLongestHits(longestHits);
		//		List<TopHit> mostHits = Database.selectMostHits(100);
		//		currentStatus.setMostHits(mostHits);

		return currentStatus;
	}

}
