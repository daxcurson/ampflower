package ar.com.strellis.ampflower.ui.view_playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.databinding.FragmentViewPlaylistBinding;
import ar.com.strellis.ampflower.ui.home.albums.AlbumAdapter;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;

public class ViewPlaylistFragment extends Fragment
{
    private FragmentViewPlaylistBinding binding;
    private SongsViewModel songsViewModel;
    private ViewPlaylistAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentViewPlaylistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.viewPlaylistRecycler.setLayoutManager(layoutManager);
        binding.viewPlaylistRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new ViewPlaylistAdapter(songsViewModel);
        binding.viewPlaylistRecycler.setAdapter(adapter);
        binding.viewPlaylistRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.viewPlaylistRecycler)
        {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                // Here we would send some kind of event to the Activity so it can in turn notify the MediaPlayerService about the change
                // in the list
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }
        });
    }
}
