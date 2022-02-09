package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumListResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.SearchType;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AlbumPagingSourceRx extends RxPagingSource<Integer, Album>
{
    public static final int PAGE_SIZE=20;
    private final AmpacheService ampacheService;
    private final LoginResponse loginResponse;
    private final MutableLiveData<Boolean> loading;

    public AlbumPagingSourceRx(AmpacheService ampacheService, LoginResponse loginResponse, MutableLiveData<Boolean> loading)
    {
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        this.loading=loading;
    }
    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Album> pagingState) {
        return pagingState.getAnchorPosition();
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Album>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page=loadParams.getKey()!=null? loadParams.getKey() : 0;
        int offset=page*PAGE_SIZE;
        loading.setValue(true);
        String filter="";
        return ampacheService.get_indexes_album_rx(loginResponse.getAuth(),filter,offset,PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(albums->toLoadResult(albums,page))
                .onErrorReturn(LoadResult.Error::new)
                .doOnSuccess(integerAlbumLoadResult -> loading.postValue(false));
    }
    private LoadResult<Integer,Album> toLoadResult(AlbumListResponse albums, int page)
    {
        Integer maxPage=page<=2 ? page+1 : null;
        return new LoadResult.Page<>(albums.getAlbum(),page==0?null:page-1,maxPage);
    }
}
