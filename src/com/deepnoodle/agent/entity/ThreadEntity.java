package com.deepnoodle.agent.entity;

public class ThreadEntity extends BaseEntity {

	private Long threadId;
	private String state;
	private String className;

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

	@Override
	public String toString() {
		return "ThreadEntity [threadId=" + threadId + ", state=" + state + ", className=" + className + ", id=" + id
				+ ", name=" + name + ", createTime=" + createTime + "]";
	}
}
