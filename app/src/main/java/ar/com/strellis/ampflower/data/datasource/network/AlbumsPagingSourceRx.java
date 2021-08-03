package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import ar.com.strellis.ampflower.data.model.Album;
import kotlin.coroutines.Continuation;

public class AlbumsPagingSourceRx extends PagingSource<Integer, Album> {
    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer,Album> pagingState) {
        return null;
    }

    @Nullable
    @Override
    public Object load(@NonNull LoadParams<Integer> loadParams, @NonNull Continuation<? super LoadResult<Integer, Album>> continuation) {
        return null;
    }
}
