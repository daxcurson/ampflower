package ar.com.strellis.ampflower.data.datasource.network;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadType;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxRemoteMediator;

import java.util.List;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AlbumListResponse;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@OptIn(markerClass = ExperimentalPagingApi.class)
public class AlbumRemoteMediator extends RxRemoteMediator<Integer, Album>
{
    private final int INITIAL_LOAD_SIZE=1;
    private final int NETWORK_PAGE_SIZE=20;
    private String query;
    private final AmpacheService ampacheService;
    private final AmpacheDatabase ampacheDatabase;
    private final AlbumDao albumDao;
    private LoginResponse loginResponse;

    public AlbumRemoteMediator(String query,AmpacheService ampacheService,AmpacheDatabase ampacheDatabase)
    {
        this.query=query;
        this.ampacheService=ampacheService;
        this.ampacheDatabase=ampacheDatabase;
        this.albumDao=ampacheDatabase.albumDao();
    }
    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, Album> pagingState) {
        // The network load method takes an optional after=<user.id> parameter. For
        // every page after the first, pass the last user ID to let it continue from
        // where it left off. For REFRESH, pass null to load the first page.
        Integer loadKey = null;
        switch (loadType) {
            case REFRESH:
                break;
            case PREPEND:
                // In this example, you never need to prepend, since REFRESH will always
                // load the first page in the list. Immediately return, reporting end of
                // pagination.
                return Single.just(new MediatorResult.Success(true));
            case APPEND:
                Album lastItem = pagingState.lastItemOrNull();

                // You must explicitly check if the last item is null when appending,
                // since passing null to networkService is only valid for initial load.
                // If lastItem is null it means no items were loaded after the initial
                // REFRESH and there are no more items to load.
                if (lastItem == null) {
                    return Single.just(new MediatorResult.Success(true));
                }

                loadKey = lastItem.getId();
                break;
        }

        int offset=1;
        int limit=pagingState.getConfig().pageSize;
        return ampacheService.get_indexes_album_rx(loginResponse.getAuth(),query,offset,limit)
                .subscribeOn(Schedulers.io())
                .map((Function<AlbumListResponse, MediatorResult>) response -> {
                    ampacheDatabase.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            albumDao.deleteAll();
                        }

                        // Insert new users into database, which invalidates the current
                        // PagingData, allowing Paging to present the updates in the DB.
                        albumDao.insertAllAlbums(response.getAlbum());
                    });

                    boolean endOfList=response.getAlbum().isEmpty();
                    return new MediatorResult.Success(endOfList);
                })
                .onErrorResumeNext(e -> {

                    return Single.error(e);
                });
    }
}
