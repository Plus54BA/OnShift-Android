package com.onshift.mobile.models;

public class Calloff {

	private String date;
	private String startTime;
	private String endTime;
	private String shiftName;
	private String responses;
	private String location;
	private String employeeLastName;
	private String employeeName;
	private String notice;
	private String reason;
	private String assigned;
	private String required;
	private Boolean isDateDivider;
	private Boolean isLoader;
	private String v2shiftId;
	private String messageId;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public String getResponses() {
		return responses;
	}

	public void setResponses(String responses) {
		this.responses = responses;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmployeeLastName() {
		return employeeLastName;
	}

	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAssigned() {
		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public Boolean getIsDateDivider() {
		return isDateDivider;
	}

	public void setIsDateDivider(Boolean isDateDivider) {
		this.isDateDivider = isDateDivider;
	}

	public Boolean getIsLoader() {
		return isLoader;
	}

	public void setIsLoader(Boolean isLoader) {
		this.isLoader = isLoader;
	}

	public String getV2shiftId() {
		return v2shiftId;
	}

	public void setV2shiftId(String v2shiftId) {
		this.v2shiftId = v2shiftId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
}