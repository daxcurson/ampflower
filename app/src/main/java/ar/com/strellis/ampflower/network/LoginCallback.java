package ar.com.strellis.ampflower.network;

import ar.com.strellis.ampflower.data.model.LoginResponse;

public interface LoginCallback {
    void loginSuccess(LoginResponse response);
    void loginFailure(String message);
}
