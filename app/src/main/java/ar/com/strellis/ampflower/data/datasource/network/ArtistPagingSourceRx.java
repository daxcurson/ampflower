package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import java.util.Objects;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistListResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.error.AmpacheSessionExpiredException;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistPagingSourceRx extends RxPagingSource<Integer, Artist> {
    public static final int PAGE_SIZE=20;
    private final AmpacheService ampacheService;
    private final LiveData<LoginResponse> loginResponse;
    private final MutableLiveData<NetworkState> loading;
    private final LiveData<String> query;

    public ArtistPagingSourceRx(AmpacheService ampacheService, LiveData<LoginResponse> loginResponse, MutableLiveData<NetworkState> loading,LiveData<String> query)
    {
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        this.loading=loading;
        this.query=query;
    }
    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Artist> pagingState) {
        return pagingState.getAnchorPosition();
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Artist>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page=loadParams.getKey()!=null? loadParams.getKey() : 0;
        int offset=page*PAGE_SIZE;
        loading.setValue(NetworkState.LOADING);
        return ampacheService.get_indexes_artist_rx(Objects.requireNonNull(loginResponse.getValue()).getAuth(), query.getValue(),offset,PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(artists->toLoadResult(artists,page))
                .onErrorReturn(LoadResult.Error::new)
                .doOnSuccess(integerAlbumLoadResult -> loading.postValue(NetworkState.LOADED));
    }
    private LoadResult<Integer,Artist> toLoadResult(ArtistListResponse artists, int page) throws AmpacheSessionExpiredException {
        // If the list of albums is empty, this means that for the specified offset
        // in the call to the network there are no more albums.
        //Integer maxPage=page<=2 ? page+1 : null;
        Integer maxPage=artists.getArtist().isEmpty()?null:page+1;
        if(artists.getError()!=null)
            throw new AmpacheSessionExpiredException();
        return new LoadResult.Page<>(artists.getArtist(),page==0?null:page-1,maxPage);
    }
}
