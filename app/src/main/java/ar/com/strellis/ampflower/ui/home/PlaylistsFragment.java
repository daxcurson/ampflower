package ar.com.strellis.ampflower.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.databinding.FragmentPlaylistsBinding;
import ar.com.strellis.ampflower.ui.home.playlists.PlaylistAdapter;
import ar.com.strellis.ampflower.viewmodel.PlaylistsViewModel;

public class PlaylistsFragment extends Fragment {
    private FragmentPlaylistsBinding binding;
    private PlaylistsViewModel playlistsViewModel;
    private PlaylistAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        playlistsViewModel=new ViewModelProvider(requireActivity()).get(PlaylistsViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView playlistsRecycler = view.findViewById(R.id.playlists_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        playlistsRecycler.setLayoutManager(layoutManager);
        playlistsRecycler.setNestedScrollingEnabled(true);
        adapter=new PlaylistAdapter();
        playlistsViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> adapter.submitList(playlists));
        playlistsViewModel.getNetworkState().observe(getViewLifecycleOwner(),networkState -> adapter.setNetworkState(networkState));
        playlistsRecycler.setAdapter(adapter);
    }
}