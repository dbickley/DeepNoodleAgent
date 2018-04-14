package com.deepnoodle.agent.metrics;

import java.util.ArrayList;
import java.util.List;

public class StackNode {
	String key;
	List<StackNode> childNodes = new ArrayList<>();
	List<StackNode> parentNodes = new ArrayList<>();
	List<String> threads = new ArrayList<String>();
	private String classname;
	private String methodName;
	private int lineNumber;

	public StackNode(String key) {
		this.key = key;
	}

	public StackNode(String classname, String methodName, int lineNumber) {
		this.classname = classname;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
		key = classname + "." + methodName + "." + lineNumber;
	}

	public String getKey() {
		return key;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public List<StackNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<StackNode> childNodes) {
		this.childNodes = childNodes;
	}

	public List<StackNode> getParentNodes() {
		return parentNodes;
	}

	public void setParentNodes(List<StackNode> parentNodes) {
		this.parentNodes = parentNodes;
	}

	public List<String> getThreads() {
		return threads;
	}

	public void setThreads(List<String> threads) {
		this.threads = threads;
	}

	public StackNode findChild(String key) {
		for (StackNode stackNode : childNodes) {
			if (stackNode.getKey().equals(key)) {
				return stackNode;
			}
		}
		return null;
	}

	public StackNode findParent(String key) {
		for (StackNode stackNode : parentNodes) {
			if (stackNode.getKey().equals(key)) {
				return stackNode;
			}
		}
		return null;
	}
}
