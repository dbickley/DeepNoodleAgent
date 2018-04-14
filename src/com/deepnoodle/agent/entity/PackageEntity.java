package com.deepnoodle.agent.entity;

public class PackageEntity extends BaseEntity {

	private Long parentPackageId;

	public Long getParentPackageId() {
		return parentPackageId;
	}

	public void setParentPackageId(Long parentPackageId) {
		this.parentPackageId = parentPackageId;
	}

	@Override
	public String toString() {
		return "PackageEntity [parentPackageId=" + parentPackageId + ", id=" + id + ", name=" + name + ", createTime="
				+ createTime + "]";
	}

}
