package ar.com.strellis.ampflower.ui.home.artists;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Searchable;
import ar.com.strellis.ampflower.databinding.FragmentArtistsBinding;
import ar.com.strellis.ampflower.ui.utils.ClickItemTouchListener;
import ar.com.strellis.ampflower.viewmodel.ArtistsViewModel;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistsFragment extends Fragment {
    private FragmentArtistsBinding binding;
    private ArtistsViewModel artistsViewModel;
    private SongsViewModel songsViewModel;
    private ArtistAdapterRx adapter;
    private final CompositeDisposable disposable=new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentArtistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        artistsViewModel=new ViewModelProvider(requireActivity()).get(ArtistsViewModel.class);
        songsViewModel=new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.artistsRecycler.setLayoutManager(layoutManager);
        binding.artistsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new ArtistAdapterRx(getContext());
        disposable.add(artistsViewModel.getArtists()
                .subscribeOn(Schedulers.io())
                .doOnError(throwable-> Log.d("ArtistsFragment.onViewCreated","Error getting artists!!! "+throwable.getMessage()))
                .subscribe(artistPagingData -> adapter.submitData(getLifecycle(),artistPagingData))
        );
        binding.artistsRecycler.setAdapter(
                adapter.withLoadStateHeaderAndFooter(new ArtistLoadStateAdapter(),new ArtistLoadStateAdapter())
        );
        binding.artistsRecycler.addOnItemTouchListener(new ClickItemTouchListener(binding.artistsRecycler)
        {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Searchable<Integer> entity= adapter.peek(position);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // If there is already a search string stored in the model, go and get it.
        String currentSearchString=artistsViewModel.getQuery().getValue();
        if(currentSearchString!=null && !currentSearchString.equals(""))
            searchView.setQuery(currentSearchString,true);
        // Assumes current activity is the searchable activity
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
                artistsViewModel.setQuery(newText);
                adapter.refresh();
                // Now, invalidate the paging, to force it to refresh?
                Log.d("ArtistsFragment","The recycler for artists is forced to update, new text: "+newText);
                if(binding.artistsRecycler.getAdapter()!=null)
                    binding.artistsRecycler.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
    }
}