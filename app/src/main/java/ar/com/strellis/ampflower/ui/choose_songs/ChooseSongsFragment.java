package ar.com.strellis.ampflower.ui.choose_songs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.databinding.FragmentChooseSongsBinding;
import ar.com.strellis.ampflower.service.MediaPlayerService;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ChooseSongsFragment extends Fragment {
    private FragmentChooseSongsBinding binding;
    private SongsViewModel songsViewModel;
    private int numberSelected = 0;
    private ChooseSongsAdapter chooseSongsAdapter;

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
        inflater.inflate(R.menu.menu_choose_songs, menu);
        // I want to hide the cloud here, to make room for the Select All
        MenuItem menuItem=menu.findItem(R.id.action_server_status);
        menuItem.setVisible(false);
        // Get the SearchView and set the searchable configuration
        RecyclerView songsRecycler=requireActivity().findViewById(R.id.add_to_playlist_recycler);
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
                songsRecycler.getAdapter().notifyDataSetChanged();
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
        chooseSongsAdapter = new ChooseSongsAdapter(songsViewModel);
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
        binding.playSelectedSongsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ChooseSongsFragment","I'm asked to play the selected songs!");
                // Let's pack an intent and send it to the service, with the list of songs
                Intent intent=new Intent(requireActivity(), MediaPlayerService.class);
                intent.setAction(MediaPlayerService.ACTION_SELECT_SONGS);
                List<Song> selectedSongs=songsViewModel.getSelectedSongs();
                Bundle bundle=new Bundle();
                bundle.putSerializable("LIST", (Serializable) selectedSongs);
                intent.putExtras(bundle);
                requireActivity().startService(intent);
                songsViewModel.setCurrentPlaylist(selectedSongs);
                // Return to home.
                NavController navController = Navigation.findNavController(requireActivity(), ChooseSongsFragment.this.getId());
                navController.navigateUp();
            }
        });
        // Observe the change in selectedSongs, and when that is changed, I'll observe
        // the changes in getSongsByAlbum. Sounds cumbersome, let's hope this works.
        songsViewModel.getSearchableItem().observe(getViewLifecycleOwner(),
                selectedEntity -> {
            if(selectedEntity instanceof Album) {
                Log.d("ChooseSongsFragment", "I have to get an album's songs!");
                songsViewModel.getSongsRepository().getSongsByAlbum((Integer) selectedEntity.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(chooseSongsAdapter::submitList);
            }
            if(selectedEntity instanceof Artist)
            {
                Log.d("ChooseSongsFragment", "I have to get an artist's songs!");
                songsViewModel.getSongsRepository().getSongsByArtist((Integer) selectedEntity.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(chooseSongsAdapter::submitList);
            }
            if(selectedEntity instanceof Playlist)
            {
                Log.d("ChooseSongsFragment", "I have to get a playlist's songs!");
                songsViewModel.getSongsRepository().getSongsByPlaylist((String) selectedEntity.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(chooseSongsAdapter::submitList);
            }
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
        hideKeyboardFrom(requireContext(),view);
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id==R.id.action_select_all)
        {
            Log.d("ChooseSongsFragment","Clicked on action_select_all: "+id);
            // Select all, by telling the SongsViewModel and forcing the recycler to refresh
            List<SelectableSong> songs=songsViewModel.getSongsInView().getValue();
            if(songs!=null)
            {
                for(SelectableSong s:songs)
                {
                    s.setSelected(!s.isSelected());
                    if (s.isSelected()) {
                        numberSelected++;
                    } else {
                        numberSelected--;
                    }
                }
                songsViewModel.setSongsInView(songs);
                chooseSongsAdapter.notifyDataSetChanged();
                binding.numberSelectedSongs.setText(getString(R.string.songs_selected, numberSelected));
            }
        }
        Log.d("ChooseSongsFragment","Clicked on item: "+id);
        return true;
    }
}
