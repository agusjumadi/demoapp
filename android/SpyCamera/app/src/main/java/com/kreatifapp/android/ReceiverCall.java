package com.kreatifapp.android;

/**
 * Created by agusj on 25/08/19.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;

public class ReceiverCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service Stops", "Ohhhhhhh");
            String action2 = "no";
            int value = 0;
        /*Bundle bundle = intent.getExtras();
            if (bundle != null) {
                action2 = bundle.getString("action");
                value = Integer.parseInt(bundle.getString("value"));
            }*/

	String action = intent.getAction();
            MyService.appendLog("Action ... "+action);
	if(Intent.ACTION_BOOT_COMPLETED.equals(action))
        {
        	context.startService(new Intent(context, MyService.class));;
	}    
}

}
