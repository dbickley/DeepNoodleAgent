package com.deepnoodle.agent.response;

import com.deepnoodle.agent.entity.StackEntity;

public class StackResponseModel extends BaseResponseModel {
	private Long threadEntityId;
	private String className;
	private String methodName;
	private Integer lineNumber;
	private String fileName;

	public Long getThreadEntityId() {
		return threadEntityId;
	}

	public void setThreadEntityId(Long threadEntityId) {
		this.threadEntityId = threadEntityId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public StackResponseModel(StackEntity entity) {
		super(entity);
		threadEntityId = entity.getThreadEntityId();
		className = entity.getClassName();
		methodName = entity.getMethodName();
		lineNumber = entity.getLineNumber();
		fileName = entity.getFileName();
	}

}
