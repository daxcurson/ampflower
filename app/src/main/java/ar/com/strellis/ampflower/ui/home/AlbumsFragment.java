package ar.com.strellis.ampflower.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.data.AmpacheDatabase;
import ar.com.strellis.ampflower.data.dao.AlbumDao;
import ar.com.strellis.ampflower.data.datasource.network.AlbumRemoteMediator;
import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.databinding.FragmentAlbumsBinding;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.ui.home.albums.AlbumAdapter;
import ar.com.strellis.ampflower.viewmodel.AlbumsViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;

public class AlbumsFragment extends Fragment {
    private FragmentAlbumsBinding binding;
    private AlbumAdapter adapter;
    private AlbumsViewModel albumsViewModel;
    private RecyclerView albumsRecycler;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AmpacheDatabase database=AmpacheDatabase.getDatabase(requireActivity());
        ServerStatusViewModel serverStatusViewModel=new ViewModelProvider(this).get(ServerStatusViewModel.class);
        AmpacheService networkService= AmpacheUtil.getService(Objects.requireNonNull(serverStatusViewModel.getAmpacheSettings().getValue()));
        AlbumDao albumDao= database.albumDao();
        String query="";
        Pager<Integer, Album> pager = new Pager(
                new PagingConfig(/* pageSize = */ 20),
                null, // initialKey,
                new AlbumRemoteMediator(networkService,database),
        () -> albumDao.pagingSource(query));
    }
}
