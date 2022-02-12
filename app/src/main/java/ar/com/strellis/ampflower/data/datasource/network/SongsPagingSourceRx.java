package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.SearchType;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.data.model.SongListResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SongsPagingSourceRx extends RxPagingSource<Integer, Song> {
    public static final int PAGE_SIZE=4;
    private final AmpacheService ampacheService;
    private LoginResponse loginResponse;
    private final SearchType searchType;
    public SongsPagingSourceRx(AmpacheService ampacheService, LoginResponse loginResponse, SearchType searchType)
    {
        this.ampacheService=ampacheService;
        this.loginResponse=loginResponse;
        this.searchType=searchType;
    }
    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }
    @NonNull
    @Override
    public Single<LoadResult<Integer, Song>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page=loadParams.getKey()!=null? loadParams.getKey() : 0;
        int offset=page*PAGE_SIZE;
        return ampacheService.song_stats(loginResponse.getAuth(),searchType.getSearchType(),offset,PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(songs->toLoadResult(songs,page))
                .onErrorReturn(LoadResult.Error::new);
    }

    private LoadResult<Integer,Song> toLoadResult(SongListResponse songs, int page)
    {
        Integer maxPage=page<=2 ? page+1 : null;
        return new LoadResult.Page<>(songs.getSong(),page==0?null:page-1,maxPage);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Song> pagingState) {
        return pagingState.getAnchorPosition();
    }
}
