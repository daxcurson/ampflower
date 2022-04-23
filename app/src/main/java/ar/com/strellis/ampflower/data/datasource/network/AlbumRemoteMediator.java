package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadType;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumListResponse;
import ar.com.strellis.ampflower.data.model.AlbumRemoteKey;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

@OptIn(markerClass = ExperimentalPagingApi.class)
public class AlbumRemoteMediator extends RxRemoteMediator<Integer, Album>
{
    private final String query;
    private final AmpacheService ampacheService;
    private final AmpacheDatabase ampacheDatabase;
    private final AlbumDao albumDao;
    private LiveData<LoginResponse> loginResponse;

    public AlbumRemoteMediator(String query,AmpacheService ampacheService,AmpacheDatabase ampacheDatabase)
    {
        this.query=query;
        this.ampacheService=ampacheService;
        this.ampacheDatabase=ampacheDatabase;
        this.albumDao=ampacheDatabase.albumDao();
    }
    public void setLoginResponse(LiveData<LoginResponse> loginResponse)
    {
        this.loginResponse=loginResponse;
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, Album> pagingState) {
        // The network load method takes an optional after=<user.id> parameter. For
        // every page after the first, pass the last album ID to let it continue from
        // where it left off. For REFRESH, pass null to load the first page.
        Integer loadKey = null;
        switch (loadType) {
            case REFRESH:
                AlbumRemoteKey remoteKey=getClosestRemoteKey(pagingState);
                if(remoteKey!=null && remoteKey.getNextKey()!=null)
                    loadKey=remoteKey.getNextKey()-1;
                else
                    loadKey=0;
                break;
            case PREPEND:
                remoteKey=getFirstRemoteKey(pagingState);
                if(remoteKey!=null && remoteKey.getPrevKey()!=null)
                    loadKey=remoteKey.getPrevKey();
                else
                    return Single.just(new MediatorResult.Success(true));
            case APPEND:
                remoteKey=getLastRemoteKey(pagingState);
                if(remoteKey!=null && remoteKey.getNextKey()!=null)
                    loadKey=remoteKey.getNextKey();
                break;
        }
        int offset=loadKey==null?0:loadKey;
        int limit=pagingState.getConfig().pageSize;
        int finalLoadKey = loadKey==null?0:loadKey;
        return ampacheService.albums(Objects.requireNonNull(loginResponse.getValue()).getAuth(),query,offset,limit)
                .subscribeOn(Schedulers.io())
                .map((Function<AlbumListResponse, MediatorResult>) response -> {
                    ampacheDatabase.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            albumDao.deleteAll();
                            ampacheDatabase.albumRemoteKeyDao().clearRemoteKeys();
                        }
                        // Insert new users into database, which invalidates the current
                        // PagingData, allowing Paging to present the updates in the DB.
                        // The response may be null because the session we hold could have been expired.
                        if(response.getAlbum()!=null) {
                            albumDao.insertAllAlbums(response.getAlbum());
                            ampacheDatabase.albumRemoteKeyDao().insertAll(response.getAlbum().stream().map(
                                    album -> new AlbumRemoteKey(album.getId(), finalLoadKey - 1, finalLoadKey + 1)).collect(Collectors.toList()));
                        }
                    });

                    boolean endOfList=true;
                    if(response.getAlbum()!=null)
                        endOfList=response.getAlbum().isEmpty();
                    return new MediatorResult.Success(endOfList);
                })
                .doOnError(throwable -> Log.d("AlbumRemoteMediator.loadSingle","Error getting albums:"+throwable.getMessage()))
                .onErrorResumeNext(Single::error);
    }
    private AlbumRemoteKey getFirstRemoteKey(PagingState<Integer,Album> state)
    {
        List<PagingSource.LoadResult.Page<Integer,Album>> pageList=state.getPages();
        if(!pageList.isEmpty())
        {
            Optional<PagingSource.LoadResult.Page<Integer, Album>> page=pageList.stream().findFirst();
            if(!page.get().getData().isEmpty())
            {
                List<Album> albumList=page.get().getData();
                Album a=albumList.get(0);
                return ampacheDatabase.albumRemoteKeyDao().remoteKeyAlbum(a.getId());
            }
        }
        return null;
    }
    private AlbumRemoteKey getLastRemoteKey(PagingState<Integer,Album> state)
    {
        List<PagingSource.LoadResult.Page<Integer,Album>> pageList=state.getPages();
        if(!pageList.isEmpty())
        {
            PagingSource.LoadResult.Page<Integer, Album> page=pageList.get(pageList.size()-1);
            if(!page.getData().isEmpty())
            {
                List<Album> albumList=page.getData();
                Album a=albumList.get(albumList.size()-1);
                return ampacheDatabase.albumRemoteKeyDao().remoteKeyAlbum(a.getId());
            }
        }
        return null;
    }
    private AlbumRemoteKey getClosestRemoteKey(PagingState<Integer,Album> state)
    {
        if(state.getAnchorPosition()!=null)
        {
            Integer anchor=state.getAnchorPosition();
            if(state.closestItemToPosition(anchor)!=null)
            {
                Album remoteAlbum=state.closestItemToPosition(anchor);
                if(remoteAlbum!=null)
                    return ampacheDatabase.albumRemoteKeyDao().remoteKeyAlbum(remoteAlbum.getId());
            }
        }
        return null;
    }
}
