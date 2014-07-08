package com.fidku.geoluks.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;

public class Request {
	 
	
/*	public static void LoginRequest(){
		 URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token); 
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        int sc = con.getResponseCode();
	        if (sc == 200) {
	          InputStream is = con.getInputStream();
	         String result=readResponse(is);
	         JSONObject res = new JSONObject(result);
	         
	         System.out.println(result);
	          String name = getFirstName(result);
	         // mActivity.show(mEmail+" - "+ res.getString("name")+" - "+ res.getString("id")+" - "+ "Google+");
	          
	        
	      
	          is.close();
	          mActivity.sessionIniciada(mEmail, res.getString("name"), res.getString("id"), "Google+");
	          return;
	        } else if (sc == 401) {
	            GoogleAuthUtil.invalidateToken(mActivity, token);
	            onError("Server auth error, please try again.", null);
	            Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
	            return;
	        } else {
	          onError("Server returned the following error code: " + sc, null);
	          return;
	        }
	}
	 *//**
     * Reads the response from the input stream and returns it as a string.
     *//*
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
    
    protected void onError(String msg, Exception e) {
        if (e != null) {
          Log.e(TAG, "Exception: ", e);
        }
        mActivity.show(msg);  // will be run in UI thread
    }*/

}
