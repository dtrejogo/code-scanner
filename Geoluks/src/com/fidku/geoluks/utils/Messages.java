package com.fidku.geoluks.utils;
 
import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.widget.Toast;

public class Messages {

	public static void showToastInSecundProcess(final String msj , final Context contextfinal  ) {
        Thread th = new Thread() {
            public void run() {
                System.out.println("Start Looper...");
                // Prepare looper
                Looper.prepare();

                // Register Queue listener hook
                MessageQueue queue = Looper.myQueue();
                queue.addIdleHandler(new IdleHandler() {
                     int mReqCount = 0;

                     @Override
                     public boolean queueIdle() {
                         if (++mReqCount == 2) {
                              // Quit looper
                              Looper.myLooper().quit();
                              return false;
                         } else
                              return true;
                     }
                });

                // Show Toast- will be called when Looper.loop() starts
                Toast.makeText(contextfinal, msj,
                         Toast.LENGTH_LONG).show();
                // Start looping Message Queue- Blocking call
                Looper.loop();
                System.out.println("It appears after Looper.myLooper().quit()");
            }
       };
       th.start();
   }

	 
}
