package ar.com.strellis.ampflower.data.datasource.network;

import androidx.paging.LoadType;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxRemoteMediator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.dao.AlbumRemoteKeyDao;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumRemoteKey;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class AlbumRemoteMediator extends RxRemoteMediator<Integer, Album>
{
    private String query;
    private AmpacheService ampacheService;
    private AmpacheDatabase ampacheDatabase;
    private AlbumDao albumDao;
    private AlbumRemoteKeyDao albumRemoteKeyDao;
    private LoginResponse loginResponse;

    public AlbumRemoteMediator(AmpacheService ampacheService,AmpacheDatabase ampacheDatabase)
    {
        this.ampacheDatabase=ampacheDatabase;
        this.ampacheService=ampacheService;
        this.albumDao=ampacheDatabase.albumDao();
        this.albumRemoteKeyDao=ampacheDatabase.albumRemoteKeyDao();
    }
    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }
    @Override
    public @NotNull Single<MediatorResult> loadSingle(@NotNull LoadType loadType, @NotNull PagingState<Integer, Album> pagingState) {
        // The network load method takes an optional String parameter. For every page
        // after the first, pass the String token returned from the previous page to
        // let it continue from where it left off. For REFRESH, pass null to load the
        // first page.
        Single<AlbumRemoteKey> remoteKeySingle = null;
        switch (loadType) {
            case REFRESH:
                // Initial load should use null as the page key, so you can return null
                // directly.
                remoteKeySingle = Single.just(new AlbumRemoteKey(query, null));
                break;
            case PREPEND:
                // In this example, you never need to prepend, since REFRESH will always
                // load the first page in the list. Immediately return, reporting end of
                // pagination.
                return Single.just(new MediatorResult.Success(true));
            case APPEND:
                // Query remoteKeyDao for the next RemoteKey.
                remoteKeySingle = albumRemoteKeyDao.remoteKeyByQuerySingle(query);
                break;
        }

        return remoteKeySingle
                .subscribeOn(Schedulers.io())
                .flatMap((Function<AlbumRemoteKey, Single<MediatorResult>>) remoteKey -> {
                    // You must explicitly check if the page key is null when appending,
                    // since null is only valid for initial load. If you receive null
                    // for APPEND, that means you have reached the end of pagination and
                    // there are no more items to load.
                    if (loadType != LoadType.REFRESH && remoteKey.getNextKey() == null) {
                        return Single.just(new MediatorResult.Success(true));
                    }

                    int offset=0;
                    if(remoteKey.getNextKey()!=null)
                        offset=Integer.parseInt(remoteKey.getNextKey());
                    int finalOffset = offset;
                    return ampacheService.get_indexes_album_rx(loginResponse.getAuth(),"",offset,pagingState.getConfig().pageSize)
                            //.searchUsers(query, remoteKey.getNextKey())
                            .map(response -> {
                                ampacheDatabase.runInTransaction(() -> {
                                    if (loadType == LoadType.REFRESH) {
                                        albumDao.deleteByQuery(query);
                                        albumRemoteKeyDao.deleteByQuery(query);
                                    }

                                    // Update RemoteKey for this query.
                                    int newoffset= finalOffset +pagingState.getConfig().pageSize;
                                    String nextKey=String.valueOf(newoffset);
                                    albumRemoteKeyDao.insertOrReplace(new AlbumRemoteKey(query, nextKey));

                                    // Insert new users into database, which invalidates the current
                                    // PagingData, allowing Paging to present the updates in the DB.
                                    albumDao.insertAllAlbums(response);
                                });
                                int itemsReturned=response.size();
                                String nextKey=null;
                                if(itemsReturned<pagingState.getConfig().pageSize)
                                    nextKey=String.valueOf(finalOffset+pagingState.getConfig().pageSize);
                                return new MediatorResult.Success(nextKey==null);
                            });
                })
                .onErrorResumeNext(e -> {
                    if (e instanceof IOException || e instanceof HttpException) {
                        return Single.just(new MediatorResult.Error(e));
                    }

                    return Single.error(e);
                });
    }
}
