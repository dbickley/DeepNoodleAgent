package com.deepnoodle.agent.response;

import com.deepnoodle.agent.entity.BaseEntity;

public abstract class BaseResponseModel {
	protected long id;
	protected String name;
	protected long createTime;

	public BaseResponseModel(BaseEntity entity) {
		id = entity.getId();
		name = entity.getName();
		createTime = entity.getCreateTime();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
