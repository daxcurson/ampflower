package ar.com.strellis.ampflower.ui.serverstatus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.databinding.FragmentServerStatusBinding;
import ar.com.strellis.ampflower.viewmodel.SettingsViewModel;

public class ServerStatusFragment extends Fragment {
    private FragmentServerStatusBinding binding;
    private SettingsViewModel settingsViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentServerStatusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsViewModel=new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        AmpacheSettings settings=settingsViewModel.getAmpacheSettings().getValue();
        if(settings!=null) {
            binding.ampacheUrl.setText(settings.getAmpacheUrl());
            binding.ampacheUsername.setText(settings.getAmpacheUsername());
            binding.ampachePassword.setText(settings.getAmpachePassword());
        }
        binding.ampacheButtonTest.setOnClickListener(event->{
            Log.d("ServerStatusFragment.onViewCreated.onClickListener for test","The test button was pressed, check if the settings are correct");
        });
        binding.ampacheButtonSaveServerSettings.setOnClickListener(event->{
            Log.d("ServerStatusFragment.onViewCreated.onClickListener for save","The save button was pressed, save these settings");
            Navigation.findNavController(event).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
