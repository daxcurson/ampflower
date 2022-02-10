package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistListResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetArtistsPageKeyedDataSource extends PageKeyedDataSource<String, Artist> {
    private static final String TAG = NetArtistsPageKeyedDataSource.class.getSimpleName();
    private final AmpacheService ampacheService;
    private final MutableLiveData<NetworkState> networkState;
    private final ReplaySubject<Artist> artistsObservable;
    private final LoginResponse loginResponse;
    private final LiveData<String> query;

    public NetArtistsPageKeyedDataSource(AmpacheService service, LoginResponse login,LiveData<String> query) {
        ampacheService=service;
        networkState = new MutableLiveData<>();
        artistsObservable = ReplaySubject.create();
        loginResponse=login;
        this.query=query;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public ReplaySubject<Artist> getArtists() {
        return artistsObservable;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, Artist> callback) {
        Log.i(TAG, "Loading Initial Range of Artists, Count " + params.requestedLoadSize);

        int limit=params.requestedLoadSize;
        networkState.postValue(NetworkState.LOADING);
        int offset=0;
        String filterQuery=this.query.getValue();
        Log.d(TAG,"Attempting to connect, auth: "+loginResponse.getAuth());
        Call<ArtistListResponse> callBack = ampacheService.get_indexes_artist(loginResponse.getAuth(),filterQuery,offset,limit);
        callBack.enqueue(new Callback<ArtistListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ArtistListResponse> call, @NonNull Response<ArtistListResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG,"We have a successful answer! We queried with: >"+filterQuery+"<");
                    assert response.body() != null;
                    if(response.body().getArtist()!=null) {
                        callback.onResult(response.body().getArtist(), null, "1");
                        networkState.postValue(NetworkState.LOADED);
                        List<Artist> results = response.body().getArtist();
                        Log.d(TAG, "There are " + results.size() + " artists retrieved");
                        results.forEach(artistsObservable::onNext);
                        Log.d(TAG, "Done loading artists");
                    }
                    else
                    {
                        // There is an error.
                        if(response.body().getError()!=null)
                        {
                            Log.e("NetArtistsPageKeyedDataSource","Error retrieving albums: "+response.body().getError().getErrorMessage()+"("+response.body().getError().getErrorCode()+")");
                        }
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED,response.message()));
                    }
                } else {
                    Log.e("NetArtistsPageKeyedDataSource", response.message());
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArtistListResponse> call, @NonNull Throwable t) {
                String errorMessage;
                if (t.getMessage() == null) {
                    errorMessage = "unknown error";
                } else {
                    errorMessage = t.getMessage();
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                callback.onResult(new ArrayList<>(), null, "1");
            }
        });
    }



    @Override
    public void loadAfter(@NonNull LoadParams<String> params, final @NonNull LoadCallback<String, Artist> callback) {
        Log.i(TAG, "Loading page " + params.key );
        networkState.postValue(NetworkState.LOADING);
        final AtomicInteger page = new AtomicInteger(0);
        try {
            page.set(Integer.parseInt(params.key));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        int limit=params.requestedLoadSize;
        int offset=(page.get())*params.requestedLoadSize;
        String filterQuery=this.query.getValue();
        Log.d(TAG,"Page="+page.get()+", offset="+offset+", limit="+limit);
        Log.d(TAG,"Attempting to connect, auth: "+loginResponse.getAuth());
        Call<ArtistListResponse> callBack = ampacheService.get_indexes_artist(loginResponse.getAuth(),filterQuery,offset,limit);
        callBack.enqueue(new Callback<ArtistListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ArtistListResponse> call, @NonNull Response<ArtistListResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG,"We have a successful answer! We queried with: >"+filterQuery+"<");
                    assert response.body() != null;
                    callback.onResult(response.body().getArtist(),Integer.toString(page.get()+1));
                    networkState.postValue(NetworkState.LOADED);
                    response.body().getArtist().forEach(artistsObservable::onNext);
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    Log.e("API CALL", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArtistListResponse> call, @NonNull Throwable t) {
                String errorMessage;
                if (t.getMessage() == null) {
                    errorMessage = "unknown error";
                } else {
                    errorMessage = t.getMessage();
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                callback.onResult(new ArrayList<>(),Integer.toString(page.get()));
            }
        });
    }


    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Artist> callback) {

    }
}
