package ar.com.strellis.ampflower.ui.home.favorites;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.databinding.FragmentFavoritesBinding;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.viewmodel.FavoritesViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import io.reactivex.disposables.Disposable;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel favoritesViewModel;
    private ServerStatusViewModel serverStatusViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        favoritesViewModel=new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RandomAlbumsAdapter randomAlbumsAdapter=new RandomAlbumsAdapter();
        TopArtistsAdapter topArtistsAdapter=new TopArtistsAdapter();
        TrendingAlbumsAdapter trendingAlbumsAdapter=new TrendingAlbumsAdapter();
        StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager gridLayoutManager2=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager gridLayoutManager3=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        binding.recyclerViewRandomAlbums.setLayoutManager(gridLayoutManager);
        binding.recyclerViewRandomAlbums.addItemDecoration(new GridSpace(1,20,true));
        binding.recyclerViewRandomAlbums.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewTopRatedArtists.setLayoutManager(gridLayoutManager2);
        binding.recyclerViewTopRatedArtists.addItemDecoration(new GridSpace(1,20,true));
        binding.recyclerViewTopRatedArtists.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewTrendingAlbums.setLayoutManager(gridLayoutManager3);
        binding.recyclerViewTrendingAlbums.addItemDecoration(new GridSpace(1,20,true));
        binding.recyclerViewTrendingAlbums.setItemAnimator(new DefaultItemAnimator());

        favoritesViewModel.loadingRandomAlbums.observe(getViewLifecycleOwner(),loading-> binding.recyclerViewRandomAlbums.toggleHoldersAdapter(loading));
        favoritesViewModel.loadingTopRatedArtists.observe(getViewLifecycleOwner(),loading-> binding.recyclerViewTopRatedArtists.toggleHoldersAdapter(loading));
        favoritesViewModel.loadingTrendingAlbums.observe(getViewLifecycleOwner(),loading-> binding.recyclerViewTrendingAlbums.toggleHoldersAdapter(loading));
        binding.recyclerViewRandomAlbums.setAdapter(randomAlbumsAdapter);
        binding.recyclerViewRandomAlbums.holdersAdapter=new AlbumsPlaceHolderAdapter();
        //binding.recyclerViewRandomAlbums.setHoldersItemDecoration(new GridSpace(2,20,true));
        binding.recyclerViewTopRatedArtists.setAdapter(topArtistsAdapter);
        binding.recyclerViewTopRatedArtists.holdersAdapter=new AlbumsPlaceHolderAdapter();
        //binding.recyclerViewTopRatedArtists.setHoldersItemDecoration(new GridSpace(2,20,true));
        binding.recyclerViewTrendingAlbums.setAdapter(trendingAlbumsAdapter);
        binding.recyclerViewTrendingAlbums.holdersAdapter=new AlbumsPlaceHolderAdapter();
        //binding.recyclerViewTrendingAlbums.setHoldersItemDecoration(new GridSpace(2,20,true));
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        serverStatusViewModel.getLoginResponse().observe(getViewLifecycleOwner(),receivedLogin->{
            Log.d("FavoritesFragment","Settings are not null, configuring");
            assert receivedLogin != null;
            assert settings != null;
            AmpacheService service= AmpacheUtil.getService(settings);
            favoritesViewModel.init(service,receivedLogin);
            Disposable s=favoritesViewModel.pagedRandomAlbums.subscribe(albumPagingData -> randomAlbumsAdapter.submitData(getLifecycle(), albumPagingData));
            Disposable s2=favoritesViewModel.pagedTopRatedArtists.subscribe(artistPagingData -> topArtistsAdapter.submitData(getLifecycle(),artistPagingData));
            Disposable s3=favoritesViewModel.pagedTrendingAlbums.subscribe(albumPagingData-> trendingAlbumsAdapter.submitData(getLifecycle(),albumPagingData));
        });

    }
}
