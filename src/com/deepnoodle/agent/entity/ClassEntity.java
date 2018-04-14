package com.deepnoodle.agent.entity;

public class ClassEntity extends BaseEntity {

	private long packageId;

	public long getPackageId() {
		return packageId;
	}

	public void setPackageId(long packageId) {
		this.packageId = packageId;
	}

	@Override
	public String toString() {
		return "ClassEntity [packageId=" + packageId + ", id=" + id + ", name=" + name + ", createTime=" + createTime
				+ "]";
	}
}
