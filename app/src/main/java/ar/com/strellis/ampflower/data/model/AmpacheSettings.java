package ar.com.strellis.ampflower.data.model;

public class AmpacheSettings {
    private String ampacheUrl;
    private String ampacheUsername;
    private String ampachePassword;
    private Long sessionRenewalTime;

    public final Long DEFAULT_RENEWAL_SECONDS=100L;

    public AmpacheSettings() {
        this.sessionRenewalTime = DEFAULT_RENEWAL_SECONDS;
    }

    public String getAmpacheUrl() {
        return ampacheUrl;
    }

    public void setAmpacheUrl(String ampacheUrl) {
        this.ampacheUrl = ampacheUrl;
    }

    public String getAmpacheUsername() {
        return ampacheUsername;
    }

    public void setAmpacheUsername(String ampacheUsername) {
        this.ampacheUsername = ampacheUsername;
    }

    public String getAmpachePassword() {
        return ampachePassword;
    }

    public void setAmpachePassword(String ampachePassword) {
        this.ampachePassword = ampachePassword;
    }
    public void setSessionRenewalTime(Long time)
    {
        sessionRenewalTime=time;
    }
    public Long getSessionRenewalTime()
    {
        return sessionRenewalTime;
    }
}
