package ar.com.strellis.ampflower.ui.home.albums;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.databinding.FragmentAlbumsBinding;
import ar.com.strellis.ampflower.event.AmpacheSessionExpiredEvent;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.AlbumsViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@ExperimentalPagingApi
public class AlbumsFragment extends Fragment {
    private FragmentAlbumsBinding binding;
    private AlbumAdapterRx adapter;
    private AlbumsViewModel albumsViewModel;
    private SongsViewModel songsViewModel;
    private ServerStatusViewModel serverStatusViewModel;
    private final CompositeDisposable disposable=new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        albumsViewModel=new ViewModelProvider(requireActivity()).get(AlbumsViewModel.class);
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.albumsRecycler.setLayoutManager(layoutManager);
        binding.albumsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new AlbumAdapterRx();
        binding.albumsRecycler.setHasFixedSize(true);
        binding.albumsRecycler.setAdapter(
                adapter.withLoadStateHeaderAndFooter(new AlbumLoadStateAdapter(),new AlbumLoadStateAdapter())
        );
        binding.albumsRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.albumsRecycler)
        {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Searchable<Integer> entity= adapter.peek(position);
                // I need to save the current page, so when the repository is re-created,
                // it goes back to the same page as before.
                if(entity!=null)
                    albumsViewModel.setCurrentPage(entity.getPage());
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
    public void onStart()
    {
        super.onStart();
        if(albumsViewModel.isReady())
            getAlbums();
        serverStatusViewModel.getLoginResponse().observe(getViewLifecycleOwner(),receivedLogin->{
            getAlbums();
        });
    }
    public void getAlbums()
    {
        disposable.add(albumsViewModel.getAlbums()
                .subscribeOn(Schedulers.io())
                .doOnError(throwable-> Log.d("AlbumsFragment.onViewCreated","Error getting albums!!! "+throwable.getMessage()))
                .retry((retryCount,error)->retryCount<3)
                .subscribe(
                        albumPagingData -> adapter.submitData(getLifecycle(),albumPagingData),
                        error-> {
                            Log.d("AlbumsFragment.onStart", "Error caught");
                            if (AmpacheUtil.isLoginExpired(Objects.requireNonNull(serverStatusViewModel.getLoginResponse().getValue()))) {
                                Log.d("AlbumsFragment", "The login is expired, must be renewed");
                                // By way of this model, I'll send a messaage to activity to require a token renewal.
                                EventBus.getDefault().post(new AmpacheSessionExpiredEvent());
                            }
                        }
                )
        );
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // If there is already a search string stored in the model, go and get it.
        String currentSearchString=albumsViewModel.getQuery().getValue();
        if(currentSearchString!=null && !currentSearchString.equals("")) {
            assert searchView != null;
            searchView.setQuery(currentSearchString,true);
        }
        // Assumes current activity is the searchable activity
        assert searchView != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                // update the albums model with the search string
                albumsViewModel.setQuery(newText);
                adapter.refresh();
                Log.d("AlbumsFragment","The recycler for albums is forced to update, new text: "+newText);
                Objects.requireNonNull(binding.albumsRecycler.getAdapter()).notifyDataSetChanged();
                return false;
            }
        });
    }
}
