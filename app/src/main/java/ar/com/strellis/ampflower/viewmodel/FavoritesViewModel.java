package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.PagingRx;

import ar.com.strellis.ampflower.data.datasource.network.AlbumsPagingSourceRx;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import io.reactivex.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class FavoritesViewModel extends ViewModel
{
    public Flowable<PagingData<Album>> pagingDataFlowable;

    public FavoritesViewModel()
    {
    }
    public void init(AmpacheService service, LoginResponse loginResponse)
    {
        AlbumsPagingSourceRx albumsPagingSourceRx=new AlbumsPagingSourceRx(service,loginResponse);
        Pager<Integer,Album> pager=new Pager<>(
                new PagingConfig(AlbumsPagingSourceRx.PAGE_SIZE,
                        AlbumsPagingSourceRx.PAGE_SIZE
                        ),
                ()->albumsPagingSourceRx
        );
        pagingDataFlowable= PagingRx.getFlowable(pager);
        CoroutineScope coroutineScope= ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagingDataFlowable,coroutineScope);
    }
}
