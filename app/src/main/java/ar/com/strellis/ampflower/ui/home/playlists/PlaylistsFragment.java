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
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Playlist;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.databinding.FragmentPlaylistsBinding;
import ar.com.strellis.ampflower.event.AmpacheSessionExpiredEvent;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.PlaylistsViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
@ExperimentalPagingApi
public class PlaylistsFragment extends Fragment {
    private FragmentPlaylistsBinding binding;
    private PlaylistsViewModel playlistsViewModel;
    private SongsViewModel songsViewModel;
    private PlaylistAdapterRx adapter;
    private final CompositeDisposable disposable=new CompositeDisposable();
    private ServerStatusViewModel serverStatusViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        playlistsViewModel=new ViewModelProvider(requireActivity()).get(PlaylistsViewModel.class);
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.playlistsRecycler.setLayoutManager(layoutManager);
        binding.playlistsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new PlaylistAdapterRx(getContext());
        /*
        If the application is resumed, the Activity executes its onStart method, whose first line is super.onStart(). This causes
        the rest of the views to be created, and the onViewCreated of the PlaylistsFragments requires the playlists repository to exist,
        but that repository is initialized after the start has been completed. So it could be impossible to initialize the repository at that stage!!!
         */
        serverStatusViewModel.getLoginResponse().observe(getViewLifecycleOwner(),receivedLogin->{
            disposable.add(playlistsViewModel.getPlaylists()
                    .subscribeOn(Schedulers.io())
                    .doOnError(throwable-> Log.d("PlaylistFragment.onViewCreated","Error getting playlists!!! "+throwable.getMessage()))
                    .retry((retryCount,error)->retryCount<3)
                    .subscribe(albumPagingData -> adapter.submitData(getLifecycle(),albumPagingData),
                            error->{
                                Log.d("PlaylistsFragment.onViewCreated","Error caught");
                                if (AmpacheUtil.isLoginExpired(Objects.requireNonNull(serverStatusViewModel.getLoginResponse().getValue()))) {
                                    Log.d("PlaylistsFragment", "The login is expired, must be renewed");
                                    // By way of this model, I'll send a messaage to activity to require a token renewal.
                                    EventBus.getDefault().post(new AmpacheSessionExpiredEvent());
                                }
                            })
            );
        });
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