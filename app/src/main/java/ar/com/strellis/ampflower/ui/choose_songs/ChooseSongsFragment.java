package ar.com.strellis.ampflower.ui.choose_songs;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.data.repository.SongsRepository;
import ar.com.strellis.ampflower.databinding.FragmentChooseSongsBinding;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ChooseSongsFragment extends Fragment {
    private FragmentChooseSongsBinding binding;
    private SongsViewModel songsViewModel;
    private int numberSelected = 0;

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
                Log.d("ChooseSongsFragment","The recycler for songs is forced to update, new text: "+newText);
                albumsRecycler.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView numberSelectedSongs = view.findViewById(R.id.number_selected_songs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ChooseSongsAdapter chooseSongsAdapter = new ChooseSongsAdapter(songsViewModel);
        songsViewModel.setSongsInView(new LinkedList<>());
        binding.addToPlaylistRecycler.setLayoutManager(layoutManager);
        binding.addToPlaylistRecycler.setNestedScrollingEnabled(true);
        binding.addToPlaylistRecycler.setItemAnimator(new DefaultItemAnimator());
        binding.addToPlaylistRecycler.setAdapter(chooseSongsAdapter);
        binding.addToPlaylistRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.addToPlaylistRecycler) {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                Log.d("DEBUG", "ChooseSongsFragment.onRequestDisallowInterceptTouchEvent: " + disallowIntercept);
            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Log.d("DEBUG", "ChooseSongsFragment.onClick: asked for position " + position);
                // Let's get the song from the Model and if we already selected it, de-select it.
                // The songs are in the Adapter, not the ViewModel!!
                SelectableSong song = chooseSongsAdapter.getSongs().get(position);
                song.setSelected(!song.isSelected());
                if (song.isSelected()) {
                    numberSelected++;
                } else {
                    numberSelected--;
                }
                numberSelectedSongs.setText(getString(R.string.songs_selected, numberSelected));
                chooseSongsAdapter.notifyItemChanged(position);
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                Log.d("DEBUG", "ChooseSongsFragment.onLongClick: asked for position " + position);
                return false;
            }
        });
        // Observe the change in selectedSongs, and when that is changed, I'll observe
        // the changes in getSongsByAlbum. Sounds cumbersome, let's hope this works.
        songsViewModel.getSearchableItem().observe(getViewLifecycleOwner(),
                selectedEntity -> {
                    Log.d("DEBUG", "I have to get an album's songs!");
                    songsViewModel.getSongsRepository().getSongsByAlbum(selectedEntity.getId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(chooseSongsAdapter::submitList);
                    // now it's time to call the Repository.
            /*
            songsViewModel.getSongsByAlbum().observe(getViewLifecycleOwner(), songs -> {
                Log.d("ChooseSongsFragment","I have a list of songs, "+songs.size()+" items");
                for(AlbumWithSongs s:songs)
                {
                    Log.d("ChooseSongsFragment","The album is "+s.getAlbum().getName());
                    Log.d("ChooseSongsFragment", "It has "+s.getSongs().size()+" songs");
                }
                chooseSongsAdapter.submitList(songs);
            });*/
                });
    }
}
