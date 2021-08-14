package ar.com.strellis.ampflower.ui.view_playlist;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.databinding.FragmentViewPlaylistBinding;
import ar.com.strellis.ampflower.service.MediaPlayerService;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;

public class ViewPlaylistFragment extends Fragment
{
    private FragmentViewPlaylistBinding binding;
    private SongsViewModel songsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentViewPlaylistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        setHasOptionsMenu(true);
        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.viewPlaylistRecycler.setLayoutManager(layoutManager);
        binding.viewPlaylistRecycler.setItemAnimator(new DefaultItemAnimator());
        ViewPlaylistAdapter adapter = new ViewPlaylistAdapter(songsViewModel);
        binding.viewPlaylistRecycler.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition=viewHolder.getAbsoluteAdapterPosition();
                int toPosition=target.getAbsoluteAdapterPosition();
                // Now we need to change the values, both in the songViewModel
                // and in the ExoPlayer's media list.
                swapSongsInView(fromPosition,toPosition);
                // Let's talk with the service
                swapSongsInService(fromPosition,toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAbsoluteAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        removeSongFromView(position);
                        removeSongFromService(position);
                        break;
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.viewPlaylistRecycler);
        songsViewModel.getCurrentItemInPlaylist().observe(getViewLifecycleOwner(),item->
        {
            // Tell the playlist recycler.
            Objects.requireNonNull(binding.viewPlaylistRecycler.getAdapter()).notifyDataSetChanged();
        });
        binding.viewPlaylistRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.viewPlaylistRecycler)
        {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Log.d("ViewPlaylistFragment","Received a click on item at position "+position);
                playSongInService(position);
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }
        });
    }
    private void swapSongsInView(int fromPosition,int toPosition)
    {
        List<SelectableSong> songs=songsViewModel.getCurrentPlaylist().getValue();
        assert songs != null;
        Collections.swap(songs,fromPosition,toPosition);
        songsViewModel.setCurrentPlaylist(songs);
        Objects.requireNonNull(binding.viewPlaylistRecycler.getAdapter()).notifyItemMoved(fromPosition,toPosition);
    }
    private void swapSongsInService(int fromPosition,int toPosition)
    {
        Intent intent=new Intent(requireActivity(), MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_MOVE_ITEM);
        Bundle bundle=new Bundle();
        bundle.putSerializable("fromPosition", fromPosition);
        bundle.putSerializable("toPosition",toPosition);
        intent.putExtras(bundle);
        requireActivity().startService(intent);
    }
    private void removeSongFromView(int position)
    {
        List<SelectableSong> songs=songsViewModel.getCurrentPlaylist().getValue();
        assert songs != null;
        songs.remove(position);
        Objects.requireNonNull(binding.viewPlaylistRecycler.getAdapter()).notifyItemRemoved(position);
    }
    private void removeSongFromService(int position)
    {
        Intent intent=new Intent(requireActivity(), MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_DELETE_ITEM);
        Bundle bundle=new Bundle();
        bundle.putSerializable("position",position);
        intent.putExtras(bundle);
        requireActivity().startService(intent);
    }
    private void playSongInService(int position)
    {
        Intent intent=new Intent(requireActivity(), MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_PLAY_ITEM);
        Bundle bundle=new Bundle();
        bundle.putSerializable("position",position);
        intent.putExtras(bundle);
        requireActivity().startService(intent);
    }
}
