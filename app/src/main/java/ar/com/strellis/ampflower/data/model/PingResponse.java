package ar.com.strellis.ampflower.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PingResponse
{
    private Date session_expire;
    private String server;
    private String version;
    private String compatible;
    public Date getSession_expire() {
        return session_expire;
    }
    public void setSession_expire(Date session_expire) {
        this.session_expire = session_expire;
    }
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getCompatible() {
        return compatible;
    }
    public void setCompatible(String compatible) {
        this.compatible = compatible;
    }
}
