package ar.com.strellis.ampflower.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.databinding.FragmentHomeBinding;
import ar.com.strellis.ampflower.viewmodel.AlbumsViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HomeViewStateAdapter stateAdapter=new HomeViewStateAdapter(this);
        ViewPager2 viewPager=binding.viewPager;
        viewPager.setAdapter(stateAdapter);
        TabLayout tabLayout=binding.homeTabLayout;
        new TabLayoutMediator(tabLayout,viewPager,
                (tab,position)->
                {
                    switch(position)
                    {
                        case 0:
                            tab.setText(R.string.home);
                            break;
                        case 1:
                            tab.setText(R.string.albums);
                            break;
                        case 2:
                            tab.setText(R.string.artists);
                            break;
                        case 3:
                            tab.setText(R.string.playlists);
                    }
                }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}