package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistListResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.SearchType;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistsPagingSourceRx extends RxPagingSource<Integer, Artist> {
    public static final int PAGE_SIZE=4;
    private final AmpacheService ampacheService;
    private LoginResponse loginResponse;
    private final SearchType searchType;
    private MutableLiveData<Boolean> loading;
    public ArtistsPagingSourceRx(AmpacheService ampacheService, LoginResponse loginResponse, SearchType searchType,MutableLiveData<Boolean> loading)
    {
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        this.searchType=searchType;
        this.loading=loading;
    }
    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }
    @NonNull
    @Override
    public Single<LoadResult<Integer, Artist>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page=loadParams.getKey()!=null? loadParams.getKey() : 0;
        int offset=page*PAGE_SIZE;
        loading.setValue(true);
        return ampacheService.artist_stats(loginResponse.getAuth(),searchType.getSearchType(),offset,PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(artists->toLoadResult(artists,page))
                .onErrorReturn(LoadResult.Error::new)
                .doOnSuccess(integerArtistLoadResult -> {
                    loading.postValue(false);
                });
    }

    private LoadResult<Integer,Artist> toLoadResult(ArtistListResponse artists, int page)
    {
        Integer maxPage=page<=2 ? page+1 : null;
        return new LoadResult.Page<>(artists.getArtist(),page==0?null:page-1,maxPage);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Artist> pagingState) {
        return pagingState.getAnchorPosition();
    }
}
