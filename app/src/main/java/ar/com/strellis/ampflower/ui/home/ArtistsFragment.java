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
import ar.com.strellis.ampflower.databinding.FragmentArtistsBinding;
import ar.com.strellis.ampflower.ui.home.artists.ArtistAdapter;
import ar.com.strellis.ampflower.viewmodel.ArtistsViewModel;

public class ArtistsFragment extends Fragment {
    private FragmentArtistsBinding binding;
    private ArtistsViewModel artistsViewModel;
    private ArtistAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentArtistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        artistsViewModel=new ViewModelProvider(requireActivity()).get(ArtistsViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView artistsRecycler = view.findViewById(R.id.artists_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        artistsRecycler.setLayoutManager(layoutManager);
        artistsRecycler.setNestedScrollingEnabled(true);
        adapter=new ArtistAdapter();
        artistsViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> adapter.submitList(artists));
        artistsViewModel.getNetworkState().observe(getViewLifecycleOwner(),networkState -> adapter.setNetworkState(networkState));
        artistsRecycler.setAdapter(adapter);
    }
}