package ar.com.strellis.ampflower.ui.home.playlists;

import android.os.Bundle;
import android.util.Log;
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

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.databinding.FragmentPlaylistsBinding;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.PlaylistsViewModel;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistsFragment extends Fragment {
    private FragmentPlaylistsBinding binding;
    private PlaylistsViewModel playlistsViewModel;
    private SongsViewModel songsViewModel;
    private PlaylistAdapterRx adapter;
    private final CompositeDisposable disposable=new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        playlistsViewModel=new ViewModelProvider(requireActivity()).get(PlaylistsViewModel.class);
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.playlistsRecycler.setLayoutManager(layoutManager);
        binding.playlistsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new PlaylistAdapterRx(getContext());
        disposable.add(playlistsViewModel.getPlaylists()
                .subscribeOn(Schedulers.io())
                .doOnError(throwable-> Log.d("PlaylistFragment.onViewCreated","Error getting playlists!!! "+throwable.getMessage()))
                .subscribe(albumPagingData -> adapter.submitData(getLifecycle(),albumPagingData))
        );
        binding.playlistsRecycler.setAdapter(
                adapter.withLoadStateHeaderAndFooter(new PlaylistLoadStateAdapter(),new PlaylistLoadStateAdapter())
        );
        binding.playlistsRecycler.setAdapter(adapter);
        binding.playlistsRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.playlistsRecycler)
        {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Searchable<String> entity= adapter.peek(position);//Objects.requireNonNull(albumsViewModel.getAlbums().getValue()).get(position);
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
}