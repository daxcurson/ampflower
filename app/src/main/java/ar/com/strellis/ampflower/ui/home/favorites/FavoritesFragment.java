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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ar.com.strellis.ampflower.Config;
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
        StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager gridLayoutManager2=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        binding.recyclerViewRandomAlbums.setLayoutManager(gridLayoutManager);
        binding.recyclerViewRandomAlbums.addItemDecoration(new GridSpace(2,20,true));
        binding.recyclerViewRandomAlbums.setItemAnimator(new DefaultItemAnimator());

        binding.recyclerViewTopRatedArtists.setLayoutManager(gridLayoutManager2);
        binding.recyclerViewTopRatedArtists.addItemDecoration(new GridSpace(2,20,true));
        binding.recyclerViewTopRatedArtists.setItemAnimator(new DefaultItemAnimator());

        RandomAlbumsAdapter randomAlbumsAdapter=new RandomAlbumsAdapter();
        TopArtistsAdapter topArtistsAdapter=new TopArtistsAdapter();
        binding.recyclerViewRandomAlbums.setAdapter(randomAlbumsAdapter);
        binding.recyclerViewTopRatedArtists.setAdapter(topArtistsAdapter);
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        serverStatusViewModel.getLoginResponse().observe(getViewLifecycleOwner(),receivedLogin->{
            Log.d("FavoritesFragment","Settings are not null, configuring");
            assert receivedLogin != null;
            assert settings != null;
            AmpacheService service= AmpacheUtil.getService(settings);
            favoritesViewModel.init(service,receivedLogin);
            Disposable s=favoritesViewModel.pagedRandomAlbums.subscribe(albumPagingData -> {
                // submit new data to recyclerview adapter
                randomAlbumsAdapter.submitData(getLifecycle(), albumPagingData);
            });
            Disposable s2=favoritesViewModel.pagedTopRatedArtists.subscribe(artistPagingData -> {
                topArtistsAdapter.submitData(getLifecycle(),artistPagingData);
            });
        });

    }
}
