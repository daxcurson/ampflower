package ar.com.strellis.ampflower.networkutils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import ar.com.strellis.ampflower.data.model.AmpacheAuth;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Utility for Ampache. It will contain a few helper functions
 */
public class AmpacheUtil
{
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    public static AmpacheAuth getAmpacheAuth(String password) throws NoSuchAlgorithmException
    {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        MessageDigest crypt = MessageDigest.getInstance("SHA-256");
        crypt.reset();
        crypt.update(password.getBytes());
        String key=byteToHex(crypt.digest());
        String auth =  timestamp+key;
        crypt.reset();
        crypt.update(auth.getBytes());
        String authToSend=byteToHex(crypt.digest());
        return new AmpacheAuth(authToSend,timestamp);
    }
    public static AmpacheService getService(AmpacheSettings settings)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        String url = settings.getAmpacheUrl();
        if(!url.endsWith("/"))
            url+="/";
        //.addConverterFactory(new ErrorConverterFactory())
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                //.addConverterFactory(new ErrorConverterFactory())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(AmpacheService.class);
    }
    public static synchronized void loginToAmpache(AmpacheSettings settings,LoginCallback callback)
    {
        Log.d("AmpacheUtil.loginToAmpache","Attempting to log in");
        if(settings!=null
                && settings.getAmpacheUrl()!=null
                && !settings.getAmpacheUrl().equals("")) {
            AmpacheService ampacheService= AmpacheUtil.getService(settings);
            String user = settings.getAmpacheUsername();
            String password = settings.getAmpachePassword();
            // We attempt to log in only if we actually have data in those fields
            if(user!=null && !user.equals("") && password!=null && !password.equals("")) {
                AmpacheAuth auth;
                try {
                    Log.d("AmpacheUtil.loginToAmpache","I have settings, trying to connect to "+settings.getAmpacheUrl()+" with username "+settings.getAmpacheUsername()+" and password "+settings.getAmpachePassword());
                    auth = AmpacheUtil.getAmpacheAuth(password);
                    Call<LoginResponse> call = ampacheService.handshake(auth.getAuthToSend(), user, auth.getTimestamp());
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                            Log.d("AmpacheUtil.loginToAmpache.onResponse","Success, let's see the response");
                            if (response.body() != null) {
                                Log.d("AmpacheUtil.loginToAmpache.onResponse","We have a body!!!");
                                callback.loginSuccess(response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                            Log.d("AmpacheUtil.loginToAmpache.onFailure","Failed to log in: "+t.getMessage());
                            callback.loginFailure(t.getMessage());
                        }
                    });
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.d("AmpacheUtil.loginToAmpache","Either username, password or url are blank, not doing anything");
            }
        }
        else
        {
            Log.d("AmpacheUtil.loginToAmpache","Either the settings are null or the url is null, not doing anything");
        }
    }
}
