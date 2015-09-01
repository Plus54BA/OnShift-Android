package com.onshift.mobile.helpers;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebViewClient;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

public class SSLAcceptingWebViewClient extends CordovaWebViewClient {

	public SSLAcceptingWebViewClient(CordovaInterface cordova) {
		super(cordova);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}

}
