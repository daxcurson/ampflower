package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.ServerStatus;

public class ServerStatusViewModel extends ViewModel {

    private final MutableLiveData<ServerStatus> serverStatusMutableLiveData;
    private final MutableLiveData<AmpacheSettings> ampacheSettingsMutableLiveData;
    private final MutableLiveData<LoginResponse> loginResponseMutableLiveData;
    private final MutableLiveData<Boolean> needTokenRenewal;

    public ServerStatusViewModel()
    {
        serverStatusMutableLiveData=new MutableLiveData<>();
        ampacheSettingsMutableLiveData=new MutableLiveData<>();
        loginResponseMutableLiveData=new MutableLiveData<>();
        needTokenRenewal=new MutableLiveData<>();
    }
    public MutableLiveData<ServerStatus> getServerStatus()
    {
        return serverStatusMutableLiveData;
    }
    public void setServerStatus(ServerStatus serverStatus)
    {
        serverStatusMutableLiveData.setValue(serverStatus);
    }
    public MutableLiveData<AmpacheSettings> getAmpacheSettings()
    {
        return ampacheSettingsMutableLiveData;
    }
    public void setAmpacheSettings(AmpacheSettings settings)
    {
        ampacheSettingsMutableLiveData.setValue(settings);
    }
    public MutableLiveData<LoginResponse> getLoginResponse()
    {
        return this.loginResponseMutableLiveData;
    }
    public void setLoginResponse(LoginResponse response)
    {
        loginResponseMutableLiveData.setValue(response);
    }
    public void setNeedTokenRenewal(boolean value)
    {
        //needTokenRenewal.setValue(value);
        needTokenRenewal.postValue(value);
    }
    public MutableLiveData<Boolean> getNeedTokenRenewal() {
        return needTokenRenewal;
    }
}
