package com.deepnoodle.agent.webserver;

import java.util.List;
import java.util.Map;

import com.deepnoodle.agent.entity.HitEntity;
import com.deepnoodle.agent.entity.ThreadEntity;

public class CurrentStatus {

	private Long totalMemory;
	private Long freeMemory;
	private Long usedMemory;
	private Long maxMemory;
	private int availableProcessors;

	private Map<Long, ThreadEntity> threads;
	private Map<Long, List<HitEntity>> lastHits;
	//	private List<TopHit> longestHits;
	//	private List<TopHit> mostHits;

	public Long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(Long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public Long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(Long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public Long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(Long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public Long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(Long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public int getAvailableProcessors() {
		return availableProcessors;
	}

	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public Map<Long, ThreadEntity> getThreads() {
		return threads;
	}

	public void setThreads(Map<Long, ThreadEntity> threads) {
		this.threads = threads;
	}

	public Map<Long, List<HitEntity>> getLastHits() {
		return lastHits;
	}

	public void setLastHits(Map<Long, List<HitEntity>> lastHits) {
		this.lastHits = lastHits;
	}

}
