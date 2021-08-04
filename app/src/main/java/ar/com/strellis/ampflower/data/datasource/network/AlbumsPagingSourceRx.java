package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import java.util.List;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import kotlin.coroutines.Continuation;

public class AlbumsPagingSourceRx extends RxPagingSource<Integer, Album> {
    private AmpacheService ampacheService;
    private String query;
    private LoginResponse loginResponse;
    private Integer currentPage;
    public AlbumsPagingSourceRx(AmpacheService ampacheService, LoginResponse loginResponse, String query)
    {
        this.ampacheService=ampacheService;
        this.query=query;
        this.loginResponse=loginResponse;
    }
    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer,Album> pagingState) {
        Integer anchorPosition=pagingState.getAnchorPosition();
        LoadResult.Page<Integer,Album> anchorPage=pagingState.closestPageToPosition(anchorPosition);
        if(anchorPage==null)
            return null;
        Integer prevKey=anchorPage.getPrevKey();
        if(prevKey!=null)
            return prevKey+20;
        Integer nextKey=anchorPage.getNextKey();
        if(nextKey!=null)
            return nextKey-20;
        return null;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Album>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer currentPage=loadParams.getKey();
        if(currentPage==null)
            currentPage=1;
        return ampacheService.get_indexes_album_rx(loginResponse.getAuth(),query,currentPage,loadParams.getLoadSize())
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new);
    }
    private LoadResult<Integer,Album> toLoadResult(List<Album> response)
    {
        return new LoadResult.Page<>(
                response,
                null,
                currentPage+20,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }
}
