package ar.com.strellis.ampflower.data.model;

public class AmpacheAuth
{
    private String authToSend;
    private String timestamp;

    public AmpacheAuth(String authToSend,String timestamp)
    {
        this.authToSend=authToSend;
        this.timestamp=timestamp;
    }
    public String getAuthToSend() {
        return authToSend;
    }

    public void setAuthToSend(String authToSend) {
        this.authToSend = authToSend;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
