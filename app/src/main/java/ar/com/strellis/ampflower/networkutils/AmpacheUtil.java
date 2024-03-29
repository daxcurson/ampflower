package ar.com.strellis.ampflower.networkutils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import ar.com.strellis.ampflower.data.model.AmpacheAuth;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.ServerStatus;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
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
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        return retrofit.create(AmpacheService.class);
    }

    /**
     * Creates an URL to download an image using the get_art method
     * @param id the id of the entity
     * @param kind what entity I want the art for - artist, album, song
     * @param ampacheSettings The settings of the ampache server
     * @return a string of the url to retrieve the image.
     */
    public static String getImageUrl(int id,String kind,AmpacheSettings ampacheSettings,LoginResponse loginResponse)
    {
        // With the ampache settings, I build a URL
        String url= ampacheSettings.getAmpacheUrl();
        if(!url.endsWith("/"))
            url+="/";
        return url+"server/json.server.php?action=get_art&auth="+loginResponse.getAuth()+"&type="+kind+"&id="+id;
    }
    public static synchronized void loginToAmpache(ServerStatusViewModel serverStatusViewModel,AmpacheSettings settings, LoginCallback callback)
    {
        Log.d("AmpacheUtil.loginToAmpache","Attempting to log in");
        serverStatusViewModel.setServerStatus(ServerStatus.CONNECTING);
        try {
            if (settings != null
                    && settings.getAmpacheUrl() != null
                    && !settings.getAmpacheUrl().equals("")) {
                AmpacheService ampacheService = AmpacheUtil.getService(settings);
                String user = settings.getAmpacheUsername();
                String password = settings.getAmpachePassword();
                // We attempt to log in only if we actually have data in those fields
                if (user != null && !user.equals("") && password != null && !password.equals("")) {
                    AmpacheAuth auth;
                    try {
                        Log.d("AmpacheUtil.loginToAmpache", "I have settings, trying to connect to " + settings.getAmpacheUrl() + " with username " + settings.getAmpacheUsername() + " and password " + settings.getAmpachePassword());
                        auth = AmpacheUtil.getAmpacheAuth(password);
                        Call<LoginResponse> call = ampacheService.handshake(auth.getAuthToSend(), user, auth.getTimestamp());
                        call.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                                Log.d("AmpacheUtil.loginToAmpache.onResponse", "Success, let's see the response");
                                if (response.body() != null) {
                                    Log.d("AmpacheUtil.loginToAmpache.onResponse", "We have a body!!!");
                                    // We may have received an error instead of an auth...
                                    if (response.body().getAuth() != null) {
                                        Log.d("AmpacheUtil.loginToAmpache.onResponse", "We have an auth code!!!");
                                        callback.loginSuccess(response.body());
                                    } else {
                                        // Error!!
                                        Log.d("AmpacheUtil.loginToAmpache.onResponse", "Unable to log in, the auth is null. ");
                                        String error = "";
                                        try(ResponseBody errorBody = response.errorBody()) {
                                            if (errorBody != null)
                                                error = errorBody.toString();
                                            callback.loginFailure(error);
                                        }
                                        catch(Exception e)
                                        {
                                            Log.d("AmpacheUtil.loginToAmpache.onResponse","Caught an exception when trying to authenticate");
                                            callback.loginFailure(e.getMessage());
                                        }
                                    }
                                } else {
                                    Log.d("AmpacheUtil.loginToAmpache.onResponse", "Unable to log in, the body is null. ");
                                    try(ResponseBody errorBody = response.errorBody()) {
                                        String error = "";
                                        if (errorBody != null) {
                                            Log.d("AmpacheUtil.loginToAmpache.onResponse", "Error body present: "+errorBody.string()+", code: "+response.code());
                                            error = errorBody.toString();
                                        }
                                        callback.loginFailure(error);
                                    }
                                    catch(Exception e)
                                    {
                                        Log.d("AmpacheUtil.loginToAmpache.onResponse","Caught an exception");
                                        callback.loginFailure(e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                                Log.d("AmpacheUtil.loginToAmpache.onFailure", "Failed to log in, failure contacting the server: " + t.getMessage());
                                callback.loginFailure(t.getMessage());
                            }
                        });
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        callback.loginFailure(e.getMessage());
                    }
                } else {
                    Log.d("AmpacheUtil.loginToAmpache", "Either username, password or url are blank, not doing anything");
                    serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
                    // The callback should include a method for failing the process altogether!
                }
            } else {
                Log.d("AmpacheUtil.loginToAmpache", "Either the settings are null or the url is null, not doing anything");
                // Now, server status is obviously unavailable!!
                serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
            }
        }
        catch(IllegalArgumentException e)
        {
            Log.d("AmpacheUtil.loginToAmpache", "Illegal arguments, possibly syntax error");
            serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
        }
    }

    /**
     * If the response we are given is expired, return true
     * @param response The response to check
     * @return true if the LoginResponse is expired.
     */
    public static boolean isLoginExpired(LoginResponse response)
    {
        Log.d("AmpacheUtil", "The expiration of this login response is: "+response.getSession_expire());
        Date now=new Date();
        Log.d("AmpacheUtil", "Now is: "+now);
        return response.getSession_expire().before(now);
    }
}
