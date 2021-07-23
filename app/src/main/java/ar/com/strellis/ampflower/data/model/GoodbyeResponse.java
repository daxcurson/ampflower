package ar.com.strellis.ampflower.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodbyeResponse
{
    private String success;
    private AmpacheError error;
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }
    public AmpacheError getError() {
        return error;
    }
    public void setError(AmpacheError error) {
        this.error = error;
    }
}
