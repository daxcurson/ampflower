package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
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
import ar.com.strellis.ampflower.data.dao.ArtistDao;
import ar.com.strellis.ampflower.data.model.AlbumRemoteKey;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.ArtistListResponse;
import ar.com.strellis.ampflower.data.model.ArtistRemoteKey;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
@ExperimentalPagingApi
public class ArtistRemoteMediator extends RxRemoteMediator<Integer, Artist>
{
    private final LiveData<String> query;
    private final AmpacheService ampacheService;
    private final AmpacheDatabase ampacheDatabase;
    private final ArtistDao artistDao;
    private LiveData<LoginResponse> loginResponse;

    public ArtistRemoteMediator(LiveData<String> query,AmpacheService ampacheService,AmpacheDatabase ampacheDatabase)
    {
        this.query=query;
        this.ampacheService=ampacheService;
        this.ampacheDatabase=ampacheDatabase;
        this.artistDao=ampacheDatabase.artistDao();
    }
    public void setLoginResponse(LiveData<LoginResponse> loginResponse)
    {
        this.loginResponse=loginResponse;
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, Artist> pagingState) {
        // The network load method takes an optional after=<user.id> parameter. For
        // every page after the first, pass the last user ID to let it continue from
        // where it left off. For REFRESH, pass null to load the first page.
        Integer loadKey = null;
        switch (loadType) {
            case REFRESH:
                ArtistRemoteKey remoteKey=getClosestRemoteKey(pagingState);
                if(remoteKey!=null && remoteKey.getNextKey()!=null)
                    loadKey=remoteKey.getNextKey()-1;
                else
                    loadKey=0;
                break;
            case PREPEND:
                // In this example, you never need to prepend, since REFRESH will always
                // load the first page in the list. Immediately return, reporting end of
                // pagination.
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
        return ampacheService.get_indexes_artist_rx(Objects.requireNonNull(loginResponse.getValue()).getAuth(),query.getValue(),offset,limit)
                .subscribeOn(Schedulers.io())
                .map((Function<ArtistListResponse, MediatorResult>) response -> {
                    ampacheDatabase.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            artistDao.deleteAll();
                            ampacheDatabase.artistRemoteKeyDao().clearRemoteKeys();
                        }
                        // Insert new users into database, which invalidates the current
                        // PagingData, allowing Paging to present the updates in the DB.
                        // The response may be null because the session we hold could have been expired.
                        if(response.getArtist()!=null) {
                            artistDao.insertAllArtists(response.getArtist());
                            ampacheDatabase.albumRemoteKeyDao().insertAll(response.getArtist().stream().map(
                                    album -> new AlbumRemoteKey(album.getId(), finalLoadKey - 1, finalLoadKey + 1)).collect(Collectors.toList()));
                        }
                    });

                    boolean endOfList=true;
                    if(response.getArtist()!=null)
                        endOfList=response.getArtist().isEmpty();
                    return new MediatorResult.Success(endOfList);
                })
                .doOnError(throwable -> Log.d("ArtistRemoteMediator.loadSingle","Error getting artists:"+throwable.getMessage()))
                .onErrorResumeNext(Single::error);
    }
    private ArtistRemoteKey getFirstRemoteKey(PagingState<Integer,Artist> state)
    {
        List<PagingSource.LoadResult.Page<Integer,Artist>> pageList=state.getPages();
        if(!pageList.isEmpty())
        {
            Optional<PagingSource.LoadResult.Page<Integer, Artist>> page=pageList.stream().findFirst();
            if(!page.get().getData().isEmpty())
            {
                List<Artist> artistList=page.get().getData();
                Artist a=artistList.get(0);
                return ampacheDatabase.artistRemoteKeyDao().remoteKeyArtist(a.getId());
            }
        }
        return null;
    }
    private ArtistRemoteKey getLastRemoteKey(PagingState<Integer,Artist> state)
    {
        List<PagingSource.LoadResult.Page<Integer,Artist>> pageList=state.getPages();
        if(!pageList.isEmpty())
        {
            PagingSource.LoadResult.Page<Integer, Artist> page=pageList.get(pageList.size()-1);
            if(!page.getData().isEmpty())
            {
                List<Artist> artistList=page.getData();
                Artist a=artistList.get(artistList.size()-1);
                return ampacheDatabase.artistRemoteKeyDao().remoteKeyArtist(a.getId());
            }
        }
        return null;
    }
    private ArtistRemoteKey getClosestRemoteKey(PagingState<Integer,Artist> state)
    {
        if(state.getAnchorPosition()!=null)
        {
            Integer anchor=state.getAnchorPosition();
            if(state.closestItemToPosition(anchor)!=null)
            {
                Artist remoteArtist=state.closestItemToPosition(anchor);
                if(remoteArtist!=null)
                    return ampacheDatabase.artistRemoteKeyDao().remoteKeyArtist(remoteArtist.getId());
            }
        }
        return null;
    }
}
