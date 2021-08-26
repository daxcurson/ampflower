package ar.com.strellis.ampflower.ui.home.favorites;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.databinding.FragmentFavoritesBinding;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.viewmodel.FavoritesViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import io.reactivex.disposables.Disposable;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel favoritesViewModel;
    private ServerStatusViewModel serverStatusViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        favoritesViewModel=new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.recyclerViewFeatured.setLayoutManager(layoutManager);
        binding.recyclerViewFeatured.setItemAnimator(new DefaultItemAnimator());
        RandomAlbumsAdapter adapter=new RandomAlbumsAdapter();
        binding.recyclerViewFeatured.setAdapter(adapter);
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        serverStatusViewModel.getLoginResponse().observe(getViewLifecycleOwner(),receivedLogin->{
            Log.d("FavoritesFragment","Settings are not null, configuring");
            assert receivedLogin != null;
            assert settings != null;
            AmpacheService service= AmpacheUtil.getService(settings);
            favoritesViewModel.init(service,receivedLogin);
            Disposable s=favoritesViewModel.pagingDataFlowable.subscribe(albumPagingData -> {
                // submit new data to recyclerview adapter
                adapter.submitData(getLifecycle(), albumPagingData);
            });
        });

    }
}
