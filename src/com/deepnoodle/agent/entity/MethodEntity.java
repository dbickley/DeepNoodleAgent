package com.deepnoodle.agent.entity;

public class MethodEntity extends BaseEntity {

	private long classId;
	//break down modifiers?
	private String modifiers;
	private boolean isMethod;
	private String annotations;

	public long getClassId() {
		return classId;
	}

	public void setClassId(long classId) {
		this.classId = classId;
	}

	public String getModifiers() {
		return modifiers;
	}

	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
	}

	public boolean isMethod() {
		return isMethod;
	}

	public void setMethod(boolean isMethod) {
		this.isMethod = isMethod;
	}

	public String getAnnotations() {
		return annotations;
	}

	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}

	@Override
	public String toString() {
		return "MethodEntity [classId=" + classId + ", modifiers=" + modifiers + ", isMethod=" + isMethod
				+ ", annotations=" + annotations + ", id=" + id + ", name=" + name + ", createTime=" + createTime + "]";
	}

}
