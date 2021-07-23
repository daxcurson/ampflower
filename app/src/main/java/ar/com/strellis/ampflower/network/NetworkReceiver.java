package ar.com.strellis.ampflower.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import androidx.annotation.NonNull;

import ar.com.strellis.ampflower.data.model.NetworkStatus;
import ar.com.strellis.ampflower.viewmodel.NetworkStatusViewModel;

public class NetworkReceiver {

    private final Context context;
    private final NetworkStatusViewModel viewModel;

    public NetworkReceiver(Context context,NetworkStatusViewModel viewModel)
    {
        this.context=context;
        this.viewModel=viewModel;
    }
    public void registerNetworkCallback() {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkRequest.Builder builder=new NetworkRequest.Builder();
        assert conn != null;
        conn.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                viewModel.setNetworkStatus(NetworkStatus.ONLINE);
                Log.d("NetworkReceiver.registerNetworkCallback.onAvailable","Network online");
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                viewModel.setNetworkStatus(NetworkStatus.OFFLINE);
                Log.d("NetworkReceiver.registerNetworkCallback.onLost","Network offline");
            }
        });
    }
}