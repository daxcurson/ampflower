package ar.com.strellis.ampflower.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.repository.AlbumsRepository;
import ar.com.strellis.ampflower.databinding.FragmentAlbumsBinding;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.ui.home.albums.AlbumAdapter;
import ar.com.strellis.ampflower.viewmodel.AlbumsViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class AlbumsFragment extends Fragment {
    private FragmentAlbumsBinding binding;
    private AlbumAdapter adapter;
    private AlbumsViewModel albumsViewModel;
    private ServerStatusViewModel serverStatusViewModel;
    private CompositeDisposable disposable=new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        albumsViewModel=new ViewModelProvider(requireActivity()).get(AlbumsViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AmpacheDatabase database=AmpacheDatabase.getDatabase(requireActivity());
        AmpacheService networkService= AmpacheUtil.getService(Objects.requireNonNull(serverStatusViewModel.getAmpacheSettings().getValue()));
        AlbumDao albumDao= database.albumDao();
        String query="";
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        AlbumsRepository albumRepository = AlbumsRepository.getInstance(requireActivity(),networkService, settings,loginResponse);
        albumsViewModel.setAlbumsRepository(albumRepository);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.albumsRecycler.setLayoutManager(layoutManager);
        binding.albumsRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter=new AlbumAdapter();
        albumsViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> adapter.submitList(albums));
        albumsViewModel.getNetworkState().observe(getViewLifecycleOwner(),networkState -> adapter.setNetworkState(networkState));
        binding.albumsRecycler.setAdapter(adapter);
    }
}
