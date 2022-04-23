package ar.com.strellis.ampflower;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AmpflowerApplication extends Application
{
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
