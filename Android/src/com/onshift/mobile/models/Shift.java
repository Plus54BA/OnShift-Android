package com.onshift.mobile.models;


public class Shift {


	private String date;
	private String startTime;
	private String endTime;
	private String shiftName;
	private String employeeName;
	private Boolean isDividerCell;
	private Boolean isLoader;
	private String location;
	private String id;
	private Boolean isDateDivider;
	private String assigned;
	private String required;

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
	
	public Boolean getIsDateDivider() {
		return isDateDivider;
	}

	public void setIsDateDivider(Boolean isDateDivider) {
		this.isDateDivider = isDateDivider;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}


	public Boolean getIsDividerCell() {
		return isDividerCell;
	}

	public void setIsDividerCell(Boolean isDividerCell) {
		this.isDividerCell = isDividerCell;
	}

	public Boolean getIsLoader() {
		return isLoader;
	}

	public void setIsLoader(Boolean isLoader) {
		this.isLoader = isLoader;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}