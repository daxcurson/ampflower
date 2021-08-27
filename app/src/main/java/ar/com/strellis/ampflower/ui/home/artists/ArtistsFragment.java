package ar.com.strellis.ampflower.ui.home.artists;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.databinding.FragmentArtistsBinding;
import ar.com.strellis.ampflower.ui.home.artists.ArtistAdapter;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.ArtistsViewModel;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;

public class ArtistsFragment extends Fragment {
    private FragmentArtistsBinding binding;
    private ArtistsViewModel artistsViewModel;
    private SongsViewModel songsViewModel;
    private ArtistAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentArtistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        artistsViewModel=new ViewModelProvider(requireActivity()).get(ArtistsViewModel.class);
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.artistsRecycler.setLayoutManager(layoutManager);
        binding.artistsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new ArtistAdapter(getContext());
        artistsViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> adapter.submitList(artists));
        artistsViewModel.getNetworkState().observe(getViewLifecycleOwner(),networkState -> adapter.setNetworkState(networkState));
        binding.artistsRecycler.setAdapter(adapter);
        binding.artistsRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.artistsRecycler)
        {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Artist entity= Objects.requireNonNull(artistsViewModel.getArtists().getValue()).get(position);
                songsViewModel.setSearchableItem(entity);
                Navigation.findNavController(view).navigate(R.id.nav_choose_songs);
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Get the SearchView and set the searchable configuration
        RecyclerView artistsRecycler=requireActivity().findViewById(R.id.artists_recycler);
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
                artistsViewModel.setQuery(newText);
                // Now, invalidate the paging, to force it to refresh?
                Log.d("ArtistsFragment","The recycler for artists is forced to update, new text: "+newText);
                artistsRecycler.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
    }
}