package com.deepnoodle.agent.response;

import java.util.List;

import com.deepnoodle.agent.entity.ThreadEntity;

public class ThreadResponseModel extends BaseResponseModel {

	private Long threadId;
	private String state;
	private String className;

	private List<StackResponseModel> stacks;

	public Long getThreadId() {
		return threadId;
	}

	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<StackResponseModel> getStacks() {
		return stacks;
	}

	public void setStacks(List<StackResponseModel> stacks) {
		this.stacks = stacks;
	}

	public ThreadResponseModel(ThreadEntity entity) {
		super(entity);
		threadId = entity.getThreadId();
		state = entity.getState();
		className = entity.getClassName();
	}
}
