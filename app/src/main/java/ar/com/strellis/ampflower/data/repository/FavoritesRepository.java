package ar.com.strellis.ampflower.data.repository;

import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;

public class FavoritesRepository
{
    private LoginResponse loginResponse;
    private final AmpacheSettings ampacheSettings;

    public FavoritesRepository(AmpacheSettings ampacheSettings)
    {
        this.ampacheSettings=ampacheSettings;
    }
    public void setLoginResponse(LoginResponse loginResponse)
    {
        this.loginResponse=loginResponse;
    }
}
