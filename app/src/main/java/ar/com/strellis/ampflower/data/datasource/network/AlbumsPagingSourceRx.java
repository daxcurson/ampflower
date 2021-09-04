package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import java.util.List;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.SearchType;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AlbumsPagingSourceRx extends RxPagingSource<Integer, Album> {
    public static final int PAGE_SIZE=4;
    private final AmpacheService ampacheService;
    private LoginResponse loginResponse;
    private final SearchType searchType;
    private MutableLiveData<Boolean> loading;
    public AlbumsPagingSourceRx(AmpacheService ampacheService, LoginResponse loginResponse, SearchType searchType,MutableLiveData<Boolean> loading)
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
    public Single<LoadResult<Integer, Album>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page=loadParams.getKey()!=null? loadParams.getKey() : 0;
        int offset=page*PAGE_SIZE;
        loading.setValue(true);
        return ampacheService.album_stats(loginResponse.getAuth(),searchType.getSearchType(),offset,PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(albums->toLoadResult(albums,page))
                .onErrorReturn(LoadResult.Error::new)
                .doOnSuccess(integerAlbumLoadResult -> loading.postValue(false));
    }

    private LoadResult<Integer,Album> toLoadResult(List<Album> albums,int page)
    {
        Integer maxPage=page<=2 ? page+1 : null;
        return new LoadResult.Page<>(albums,page==0?null:page-1,maxPage);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Album> pagingState) {
        return pagingState.getAnchorPosition();
    }
}
