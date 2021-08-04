package ar.com.strellis.ampflower.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.databinding.FragmentAlbumsBinding;
import ar.com.strellis.ampflower.ui.home.albums.AlbumAdapter;
import ar.com.strellis.ampflower.ui.home.albums.AlbumAdapterRx;
import ar.com.strellis.ampflower.viewmodel.AlbumsViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class AlbumsFragment extends Fragment {
    private FragmentAlbumsBinding binding;
    private AlbumAdapter adapter;
    private AlbumsViewModel albumsViewModel;
    private ServerStatusViewModel serverStatusViewModel;
    private CompositeDisposable disposable=new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        albumsViewModel=new ViewModelProvider(requireActivity()).get(AlbumsViewModel.class);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.albumsRecycler.setLayoutManager(layoutManager);
        binding.albumsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new AlbumAdapter();
        albumsViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> adapter.submitList(albums));
        albumsViewModel.getNetworkState().observe(getViewLifecycleOwner(),networkState -> adapter.setNetworkState(networkState));
        binding.albumsRecycler.setAdapter(adapter);
        /*adapter=new AlbumAdapterRx();
        albumsViewModel.getAlbums().observe(getViewLifecycleOwner(),pagingData->adapter.submitData()*/
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Get the SearchView and set the searchable configuration
        RecyclerView albumsRecycler=requireActivity().findViewById(R.id.albums_recycler);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // update the albums model with the search string
                albumsViewModel.setQuery(newText);
                // Now, invalidate the paging, to force it to refresh?
                Log.d("AlbumsFragment","The recycler for albums is forced to update, new text: "+newText);
                albumsRecycler.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
    }
}
