/*
 *	Push notification message model  
 *  
 *
 */

package com.onshift.mobile.models;

public class PushNotificationMessage {
	private String msg;
	private String msg_type;
	private String id;
	private String badge;

	public String get_message(){
		return msg;
	}

	public void set_message(String _msg){
		this.msg = _msg;
	}

	public String get_msgtype(){
		return msg_type;
	}

	public void set_msgtype(String _msg_type){
		this.msg_type = _msg_type;
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
}
