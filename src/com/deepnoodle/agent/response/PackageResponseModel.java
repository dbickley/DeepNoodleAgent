package com.deepnoodle.agent.response;

import java.util.ArrayList;
import java.util.List;

import com.deepnoodle.agent.entity.PackageEntity;

public class PackageResponseModel extends BaseResponseModel {

	private Long parentPackageId;
	private List<PackageResponseModel> children = new ArrayList<>();
	private List<ClassResponseModel> classes;

	public Long getParentPackageId() {
		return parentPackageId;
	}

	public void setParentPackageId(Long parentPackageId) {
		this.parentPackageId = parentPackageId;
	}

	public List<ClassResponseModel> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassResponseModel> classes) {
		this.classes = classes;
	}

	public PackageResponseModel(PackageEntity entity) {
		super(entity);
		parentPackageId = entity.getParentPackageId();
	}

	public List<PackageResponseModel> getChildren() {
		return children;
	}

	public void setChildren(List<PackageResponseModel> children) {
		this.children = children;
	}

}
