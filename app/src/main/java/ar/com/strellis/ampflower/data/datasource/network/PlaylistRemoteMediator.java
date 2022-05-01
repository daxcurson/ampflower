package ar.com.strellis.ampflower.data.datasource.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.LoadType;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.PlaylistDao;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.PlaylistListResponse;
import ar.com.strellis.ampflower.data.model.PlaylistRemoteKey;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistRemoteMediator extends RxRemoteMediator<Integer, Playlist>
{
    private final String query;
    private final AmpacheService ampacheService;
    private final AmpacheDatabase ampacheDatabase;
    private final PlaylistDao playlistDao;
    private LiveData<LoginResponse> loginResponse;

    public PlaylistRemoteMediator(String query,AmpacheService ampacheService,AmpacheDatabase ampacheDatabase)
    {
        this.query=query;
        this.ampacheService=ampacheService;
        this.ampacheDatabase=ampacheDatabase;
        this.playlistDao=ampacheDatabase.playlistDao();
    }
    public void setLoginResponse(LiveData<LoginResponse> loginResponse)
    {
        this.loginResponse=loginResponse;
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, Playlist> pagingState) {
        // The network load method takes an optional after=<user.id> parameter. For
        // every page after the first, pass the last user ID to let it continue from
        // where it left off. For REFRESH, pass null to load the first page.
        Integer loadKey = null;
        switch (loadType) {
            case REFRESH:
                PlaylistRemoteKey remoteKey=getClosestRemoteKey(pagingState);
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
                break;
            case APPEND:
                remoteKey=getLastRemoteKey(pagingState);
                if(remoteKey!=null && remoteKey.getNextKey()!=null)
                    loadKey=remoteKey.getNextKey();
                break;
        }
        int offset=loadKey==null?0:loadKey;
        int limit=pagingState.getConfig().pageSize;
        int finalLoadKey = loadKey==null?0:loadKey;
        return ampacheService.get_indexes_playlist_rx(Objects.requireNonNull(loginResponse.getValue()).getAuth(),query,offset,limit)
                .subscribeOn(Schedulers.io())
                .map((Function<PlaylistListResponse, MediatorResult>) response -> {
                    ampacheDatabase.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            playlistDao.deleteAll();
                            ampacheDatabase.playlistRemoteKeyDao().clearRemoteKeys();
                        }
                        // Insert new users into database, which invalidates the current
                        // PagingData, allowing Paging to present the updates in the DB.
                        // The response may be null because the session we hold could have been expired.
                        if(response.getPlaylist()!=null) {
                            playlistDao.insertAllPlaylists(response.getPlaylist());
                            ampacheDatabase.playlistRemoteKeyDao().insertAll(response.getPlaylist().stream().map(
                                    playlist -> new PlaylistRemoteKey(playlist.getId(), finalLoadKey - 1, finalLoadKey + 1)).collect(Collectors.toList()));
                        }
                    });

                    boolean endOfList=true;
                    if(response.getPlaylist()!=null)
                        endOfList=response.getPlaylist().isEmpty();
                    return new MediatorResult.Success(endOfList);
                })
                .doOnError(throwable -> Log.d("PlaylistRemoteMediator.loadSingle","Error getting playlists:"+throwable.getMessage()))
                .onErrorResumeNext(Single::error);
    }
    private PlaylistRemoteKey getFirstRemoteKey(PagingState<Integer,Playlist> state)
    {
        List<PagingSource.LoadResult.Page<Integer,Playlist>> pageList=state.getPages();
        if(!pageList.isEmpty())
        {
            Optional<PagingSource.LoadResult.Page<Integer, Playlist>> page=pageList.stream().findFirst();
            if(!page.get().getData().isEmpty())
            {
                List<Playlist> playlistList=page.get().getData();
                Playlist a=playlistList.get(0);
                return ampacheDatabase.playlistRemoteKeyDao().remoteKeyPlaylist(a.getId());
            }
        }
        return null;
    }
    private PlaylistRemoteKey getLastRemoteKey(PagingState<Integer,Playlist> state)
    {
        List<PagingSource.LoadResult.Page<Integer,Playlist>> pageList=state.getPages();
        if(!pageList.isEmpty())
        {
            PagingSource.LoadResult.Page<Integer, Playlist> page=pageList.get(pageList.size()-1);
            if(!page.getData().isEmpty())
            {
                List<Playlist> playlistList=page.getData();
                Playlist a=playlistList.get(playlistList.size()-1);
                return ampacheDatabase.playlistRemoteKeyDao().remoteKeyPlaylist(a.getId());
            }
        }
        return null;
    }
    private PlaylistRemoteKey getClosestRemoteKey(PagingState<Integer,Playlist> state)
    {
        if(state.getAnchorPosition()!=null)
        {
            Integer anchor=state.getAnchorPosition();
            if(state.closestItemToPosition(anchor)!=null)
            {
                Playlist remotePlaylist=state.closestItemToPosition(anchor);
                if(remotePlaylist!=null)
                    return ampacheDatabase.playlistRemoteKeyDao().remoteKeyPlaylist(remotePlaylist.getId());
            }
        }
        return null;
    }
}
