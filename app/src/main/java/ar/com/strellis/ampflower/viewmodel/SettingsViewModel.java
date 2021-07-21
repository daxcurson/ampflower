package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;

public class SettingsViewModel extends ViewModel
{
    private final MutableLiveData<AmpacheSettings> ampacheSettingsMutableLiveData;

    public SettingsViewModel()
    {
        ampacheSettingsMutableLiveData=new MutableLiveData<>();
    }
    public MutableLiveData<AmpacheSettings> getAmpacheSettings()
    {
        return ampacheSettingsMutableLiveData;
    }
    public void setAmpacheSettings(AmpacheSettings settings)
    {
        ampacheSettingsMutableLiveData.postValue(settings);
    }
}
