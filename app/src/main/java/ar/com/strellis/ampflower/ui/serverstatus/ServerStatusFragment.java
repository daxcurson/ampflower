package ar.com.strellis.ampflower.ui.serverstatus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.ServerStatus;
import ar.com.strellis.ampflower.databinding.FragmentServerStatusBinding;
import ar.com.strellis.ampflower.network.AmpacheUtil;
import ar.com.strellis.ampflower.network.LoginCallback;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.SettingsViewModel;

public class ServerStatusFragment extends Fragment {
    private FragmentServerStatusBinding binding;
    private SettingsViewModel settingsViewModel;
    private AmpacheSettings settings;
    private ServerStatusViewModel serverStatusViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentServerStatusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        serverStatusViewModel=new ViewModelProvider(requireActivity()).get(ServerStatusViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsViewModel=new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        settings=settingsViewModel.getAmpacheSettings().getValue();
        if(settings!=null) {
            loadTextBoxesFromSettings(settings);
        }
        else
            settings=new AmpacheSettings();
        binding.ampacheButtonTest.setOnClickListener(event->{
            Log.d("ServerStatusFragment.onViewCreated.onClickListener for test","The test button was pressed, check if the settings are correct");
            saveSettingsFromTextBoxes();
            AmpacheUtil.loginToAmpache(settings,getCallbackFunctionForLogin(false));
        });
        binding.ampacheButtonSaveServerSettings.setOnClickListener(event->{
            Log.d("ServerStatusFragment.onViewCreated.onClickListener for save","The save button was pressed, save these settings");
            saveSettingsFromTextBoxes();
            AmpacheUtil.loginToAmpache(settings,getCallbackFunctionForLogin(true));
            // And now, in addition to trying to log in, let's save the settings.
            Navigation.findNavController(event).navigateUp();
        });
        // We check the current server status!
        configureServerStatus();
    }
    private void loadTextBoxesFromSettings(AmpacheSettings settings)
    {
        binding.ampacheUrl.setText(settings.getAmpacheUrl());
        binding.ampacheUsername.setText(settings.getAmpacheUsername());
        binding.ampachePassword.setText(settings.getAmpachePassword());
    }
    private void saveSettingsFromTextBoxes()
    {
        settings.setAmpacheUrl(binding.ampacheUrl.getText().toString());
        settings.setAmpacheUsername(binding.ampacheUsername.getText().toString());
        settings.setAmpachePassword(binding.ampachePassword.getText().toString());
    }
    private LoginCallback getCallbackFunctionForLogin(boolean saveSettings)
    {
        return new LoginCallback() {
            @Override
            public void loginSuccess(LoginResponse response) {
                // We managed to log in.
                serverStatusViewModel.setServerStatus(ServerStatus.ONLINE);
                serverStatusViewModel.setLoginResponse(response);
                if(saveSettings) {
                    settingsViewModel.setAmpacheSettings(settings);
                }
            }

            @Override
            public void loginFailure(String message) {
                serverStatusViewModel.setServerStatus(ServerStatus.LOGIN_DENIED);
            }
        };
    }
    private void configureServerStatus()
    {
        Observer<ServerStatus> serverStatusObserver=serverStatus -> {
            switch(Objects.requireNonNull(serverStatus)) {
                case UNAVAILABLE:
                    binding.ampacheServerStatusButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_cloud_unavail));
                    binding.ampacheServerStatusText.setText(R.string.unavailable);
                    break;
                case LOGIN_DENIED:
                    binding.ampacheServerStatusButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_cloud_cross));
                    binding.ampacheServerStatusText.setText(R.string.login_denied);
                    break;
                case ONLINE:
                    binding.ampacheServerStatusButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_cloud_checked));
                    binding.ampacheServerStatusText.setText(R.string.online);
                    break;
            }
        };
        serverStatusViewModel.getServerStatus().observe(requireActivity(),serverStatusObserver);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
