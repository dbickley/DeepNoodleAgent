package com.deepnoodle.agent.response;

import java.util.List;

import com.deepnoodle.agent.entity.MethodEntity;

public class MethodResponseModel extends BaseResponseModel {
	private long classId;
	private String modifiers;
	private boolean isMethod;
	private String annotations;
	private List<HitResponseModel> hits;

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

	public List<HitResponseModel> getHits() {
		return hits;
	}

	public void setHits(List<HitResponseModel> hits) {
		this.hits = hits;
	}

	public MethodResponseModel(MethodEntity entity) {
		super(entity);
		classId = entity.getClassId();
		modifiers = entity.getModifiers();
		isMethod = entity.isMethod();
		annotations = entity.getAnnotations();
	}

}
