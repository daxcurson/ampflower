package ar.com.strellis.ampflower2.networkutils;

import ar.com.strellis.ampflower2.data.model.LoginResponse;

public interface LoginCallback {
    void loginSuccess(LoginResponse response);
    void loginFailure(String message);
}
