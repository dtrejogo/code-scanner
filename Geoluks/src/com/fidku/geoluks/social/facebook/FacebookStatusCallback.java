package com.fidku.geoluks.social.facebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request.GraphUserCallback;   
import com.facebook.model.GraphUser;
import com.facebook.SessionState;
import com.fidku.geoluks.LoginActivity;
import com.fidku.geoluks.parameters.App;
public class FacebookStatusCallback implements Session.StatusCallback {
	 

	private LoginActivity loginContext;

	public FacebookStatusCallback(LoginActivity loginContext) {
		this.loginContext =loginContext;
	}
 
	@Override
	public void call(Session session, SessionState state,
			Exception exception) {
		if (session.isOpened()) {

			  Request.newMeRequest(session, new GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // TODO Auto-generated method stub
                        Log.d("FacebookResponse",""+user);
                        
                        try {
                        	if(user.getProperty("email")==null){
                        		AlertDialog.Builder builder =
                                        new AlertDialog.Builder(loginContext);
                         
                                builder.setMessage("Disculpe, no hemos tenido acceso a su email, por favor revise si esta verificado o si esta disponible e intente de nuevo.")
                                       .setTitle("Informacion")
                                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int id) {
                                               dialog.cancel();
                                           }
                                       });
                         
                                 builder.create().show();
                                 return;
                        	}
                        	
                        	 
                        	loginContext.sessionIniciada(user.getProperty("email").toString(), user.getName(), user.getId(), App.SOCIA_NETWORK.FACEBOOK.value);
                             
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).executeAsync();
		}
	}

}
