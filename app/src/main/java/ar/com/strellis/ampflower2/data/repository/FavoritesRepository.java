package ar.com.strellis.ampflower2.data.repository;

import ar.com.strellis.ampflower2.data.model.AmpacheSettings;
import ar.com.strellis.ampflower2.data.model.LoginResponse;

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
