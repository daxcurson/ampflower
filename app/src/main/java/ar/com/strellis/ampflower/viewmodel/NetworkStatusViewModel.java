package ar.com.strellis.ampflower.viewmodel;

import android.net.Network;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ar.com.strellis.ampflower.data.model.NetworkStatus;

public class NetworkStatusViewModel extends ViewModel {
    private final MutableLiveData<NetworkStatus> networkStatus;

    public NetworkStatusViewModel()
    {
        networkStatus=new MutableLiveData<>();
    }
    public MutableLiveData<NetworkStatus> getNetworkStatus()
    {
        return networkStatus;
    }
    public void setNetworkStatus(NetworkStatus status)
    {
        Log.d("NetworkStatusViewModel.setNetworkStatus","Attempting to set network status");
        this.networkStatus.postValue(status);
    }
}
