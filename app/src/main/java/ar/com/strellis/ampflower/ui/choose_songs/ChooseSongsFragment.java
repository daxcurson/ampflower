package ar.com.strellis.ampflower.ui.choose_songs;

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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.databinding.FragmentChooseSongsBinding;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;

public class ChooseSongsFragment extends Fragment {
    private FragmentChooseSongsBinding binding;
    private SongsViewModel songsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentChooseSongsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        setHasOptionsMenu(true);
        return root;
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
                songsViewModel.setQuery(newText);
                // Now, invalidate the paging, to force it to refresh?
                Log.d("AlbumsFragment","The recycler for albums is forced to update, new text: "+newText);
                albumsRecycler.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
    }

}
