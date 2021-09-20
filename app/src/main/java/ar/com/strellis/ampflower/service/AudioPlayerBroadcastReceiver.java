package ar.com.strellis.ampflower.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AudioPlayerBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AudioPlayerBroadcastReceiver.onReceive","Message received: "+intent.getAction());
    }
}
