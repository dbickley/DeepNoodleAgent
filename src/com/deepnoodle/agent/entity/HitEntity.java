package com.deepnoodle.agent.entity;

public class HitEntity extends BaseEntity {
	private long packageId;
	private long classId;
	private long methodId;
	private long threadId;
	private Long callingHitId;
	private String args;
	private String returned;
	private long duration;
	private Long endTime;

	public long getClassId() {
		return classId;
	}

	public void setClassId(long classId) {
		this.classId = classId;
	}

	public long getPackageId() {
		return packageId;
	}

	public void setPackageId(long packageId) {
		this.packageId = packageId;
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

	@Override
	public String toString() {
		return "HitEntity [packageId=" + packageId + ", classId=" + classId + ", methodId=" + methodId + ", threadId="
				+ threadId + ", callingHitId=" + callingHitId + ", args=" + args + ", returned=" + returned
				+ ", duration=" + duration + ", endTime=" + endTime + ", id=" + id + ", name=" + name + ", createTime="
				+ createTime + "]";
	}

}
