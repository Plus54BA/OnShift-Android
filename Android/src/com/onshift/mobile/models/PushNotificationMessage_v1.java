/*
 *	Push notification message model  
 *  (Old format)
 *
 */

package com.onshift.mobile.models;

public class PushNotificationMessage_v1 {
	private String message;
	private String message_type_id;
	private String id;
	private String badge;
	private String shift_id;

	public String get_message(){
		return message;
	}

	public void set_message(String _message){
		this.message = _message;
	}

	public String get_messagetypeid(){
		return message_type_id;
	}

	public void set_messagetypeid(String _message_type_id){
		this.message_type_id = _message_type_id;
	}

	public String get_id(){
		return id;
	}

	public void set_messageid(String _message_id){
		this.id = _message_id;
	}

	public String get_badge(){
		return badge;
	}

	public void set_badge(String _badge){
		this.badge = _badge;
	}

	public String get_shiftid(){
		return shift_id;
	}

	public void set_shiftid(String _shiftid){
		this.shift_id = _shiftid;
	}
}
