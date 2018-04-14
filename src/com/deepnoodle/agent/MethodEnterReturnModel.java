package com.deepnoodle.agent;

import com.deepnoodle.agent.entity.HitEntity;

public class MethodEnterReturnModel {
	private HitEntity hit;
	private long startTime;
	private Object[] args;

	public MethodEnterReturnModel(long startTime, HitEntity hit, Object[] args) {
		this.hit = hit;
		this.startTime = startTime;
		this.args = args;
	}

	public HitEntity getHit() {
		return hit;
	}

	public void setHit(HitEntity hit) {
		this.hit = hit;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

}
