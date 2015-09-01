package com.onshift.mobile.models;

public class Callbacks {
	public interface loginInterface{
		public void success();
		public void failure();
	}

	public Callbacks() {

	}

	public void success(String json){

	}

	public void failure(String Error){

	}
}
