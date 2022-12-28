package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import java.util.Objects;

import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistListResponse;
import ar.com.strellis.ampflower.error.AmpacheSessionExpiredException;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistPagingSourceRx extends RxPagingSource<Integer, Playlist> {
    public static final int PAGE_SIZE=20;
    private final AmpacheService ampacheService;
    private LiveData<LoginResponse> loginResponse;
    private final MutableLiveData<NetworkState> loading;
    private final String query;
    public PlaylistPagingSourceRx(AmpacheService ampacheService, LiveData<LoginResponse> loginResponse, MutableLiveData<NetworkState> loading,String query)
    {
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        this.loading=loading;
        this.query=query;
    }
    public void setLoginResponse(LiveData<LoginResponse> loginResponse)
    {
        this.loginResponse=loginResponse;
    }
    @NonNull
    @Override
    public Single<LoadResult<Integer, Playlist>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page=loadParams.getKey()!=null? loadParams.getKey() : 0;
        int offset=page*PAGE_SIZE;
        loading.setValue(NetworkState.LOADING);
        return ampacheService.get_indexes_playlist_rx(Objects.requireNonNull(loginResponse.getValue()).getAuth(), query,offset,PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(playlists->toLoadResult(playlists,page))
                .onErrorReturn(LoadResult.Error::new)
                .doOnSuccess(integerPlaylistLoadResult -> loading.postValue(NetworkState.LOADED));
    }

    private LoadResult<Integer, Playlist> toLoadResult(PlaylistListResponse playlists, int page) throws AmpacheSessionExpiredException {
        Integer maxPage=page<=2 ? page+1 : null;
        if(playlists.getError()!=null)
            throw new AmpacheSessionExpiredException();
        return new LoadResult.Page<>(playlists.getPlaylist(),page==0?null:page-1,maxPage);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Playlist> pagingState) {
        return pagingState.getAnchorPosition();
    }
}
