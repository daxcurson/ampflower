package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.ServerStatus;

public class ServerStatusViewModel extends ViewModel {

    private final MutableLiveData<ServerStatus> serverStatusMutableLiveData;
    private final MutableLiveData<AmpacheSettings> ampacheSettingsMutableLiveData;

    public ServerStatusViewModel()
    {
        serverStatusMutableLiveData=new MutableLiveData<>();
        ampacheSettingsMutableLiveData=new MutableLiveData<>();
    }
    public MutableLiveData<ServerStatus> getServerStatus()
    {
        return serverStatusMutableLiveData;
    }
    public void setServerStatus(ServerStatus serverStatus)
    {
        serverStatusMutableLiveData.postValue(serverStatus);
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
