package com.onshift.mobile.models;

import java.util.ArrayList;


public class Employee {


	private String hours;
	private String hoursIncludingShift;
	private String userImage;
	private String shiftName;
	private Boolean overtime;
	private Boolean overtimeThisShift;
	private String employeeLastName;
	private String employeeFirstName;
	private Boolean isDividerCell;
	private Boolean isLoader;
	private String threshold;
	private String thresholdThisShift;
	private String date; // This value comes from Calloff
	private String location; // This value comes from Calloff
	private String id;
	private Boolean showCheckBox = false;
	private boolean checked;
	private String primaryRoleType;
	private String yearCalloffCount;
	private Boolean boolResponse;
	private String hireDate;
	private String messageId;
	private String messageDate;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}
	
	public String getHireDate() {
		return hireDate;
	}

	public void setHireDate(String hireDate) {
		this.hireDate = hireDate;
	}

	public Boolean getBoolResponse() {
		return boolResponse;
	}

	public void setBoolResponse(Boolean boolResponse) {
		this.boolResponse = boolResponse;
	}
	
	public String getYearCalloffCount() {
		return yearCalloffCount;
	}

	public void setYearCalloffCount(String yearCalloffCount) {
		this.yearCalloffCount = yearCalloffCount;
	}
	
	public String getPrimaryRoleType() {
		return primaryRoleType;
	}

	public void setPrimaryRoleType(String primaryRoleType) {
		this.primaryRoleType = primaryRoleType;
	}
	
	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getHoursIncludingShift() {
		return hoursIncludingShift;
	}

	public void setHoursIncludingShift(String hoursIncludingShift) {
		this.hoursIncludingShift = hoursIncludingShift;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public Boolean getOvertime() {
		return overtime;
	}

	public void setOvertime(Boolean overtime) {
		this.overtime = overtime;
	}

	public Boolean getOvertimeThisShift() {
		return overtimeThisShift;
	}

	public void setOvertimeThisShift(Boolean overtimeThisShift) {
		this.overtimeThisShift = overtimeThisShift;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getThresholdThisShift() {
		return thresholdThisShift;
	}

	public void setThresholdThisShift(String thresholdThisShift) {
		this.thresholdThisShift = thresholdThisShift;
	}

	public String getEmployeeLastName() {
		return employeeLastName;
	}

	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	public String getEmployeeFirstName() {
		return employeeFirstName;
	}

	public void setEmployeeFirstName(String employeeName) {
		this.employeeFirstName = employeeName;
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

	public Boolean getShowCheckBox() {
		return showCheckBox;
	}

	public void setShowCheckBox(Boolean showCheckBox) {
		this.showCheckBox = showCheckBox;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
}