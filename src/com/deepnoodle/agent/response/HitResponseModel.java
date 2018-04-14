package com.deepnoodle.agent.response;

import java.util.ArrayList;
import java.util.List;

import com.deepnoodle.agent.entity.HitEntity;

public class HitResponseModel extends BaseResponseModel {
	private long methodId;
	private long threadId;
	private Long callingHitId;
	private String args;
	private String returned;
	private long duration;
	private Long endTime;

	private List<HitResponseModel> children = new ArrayList<>();
	private List<ThreadResponseModel> threads;

	public HitResponseModel(HitEntity entity) {
		super(entity);
		methodId = entity.getMethodId();
		threadId = entity.getThreadId();
		callingHitId = entity.getCallingHitId();
		args = entity.getArgs();
		returned = entity.getReturned();
		duration = entity.getDuration();
		endTime = entity.getEndTime();
	}

	public long getMethodId() {
		return methodId;
	}

	public void setMethodId(long methodId) {
		this.methodId = methodId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public Long getCallingHitId() {
		return callingHitId;
	}

	public void setCallingHitId(Long callingHitId) {
		this.callingHitId = callingHitId;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getReturned() {
		return returned;
	}

	public void setReturned(String returned) {
		this.returned = returned;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public List<ThreadResponseModel> getThreads() {
		return threads;
	}

	public void setThreads(List<ThreadResponseModel> threads) {
		this.threads = threads;
	}

	public List<HitResponseModel> getChildren() {
		return children;
	}

	public void setChildren(List<HitResponseModel> children) {
		this.children = children;
	}

}
