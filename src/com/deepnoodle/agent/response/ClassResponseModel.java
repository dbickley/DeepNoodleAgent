package com.deepnoodle.agent.response;

import java.util.List;

import com.deepnoodle.agent.entity.ClassEntity;

public class ClassResponseModel extends BaseResponseModel {
	private long packageId;
	private List<MethodResponseModel> methods;

	public long getPackageId() {
		return packageId;
	}

	public void setPackageId(long packageId) {
		this.packageId = packageId;
	}

	public List<MethodResponseModel> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodResponseModel> methods) {
		this.methods = methods;
	}

	public ClassResponseModel(ClassEntity entity) {
		super(entity);
	}
}
