package com.deepnoodle.agent.entity;

public class StackEntity extends BaseEntity {

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

	@Override
	public String toString() {
		return "StackEntity [threadEntityId=" + threadEntityId + ", className=" + className + ", methodName="
				+ methodName + ", lineNumber=" + lineNumber + ", fileName=" + fileName + ", id=" + id + ", name=" + name
				+ ", createTime=" + createTime + "]";
	}
}
