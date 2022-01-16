package ar.com.strellis.ampflower.data.model;

public class AmpacheError
{
    private int errorCode;
    private String errorAction;
    private String errorType;
    private String errorMessage;
    public String getErrorAction() {
        return errorAction;
    }
    public void setErrorAction(String errorAction) {
        this.errorAction = errorAction;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorType()
    {
        return this.errorType;
    }
    public void setErrorType(String errorType)
    {
        this.errorType=errorType;
    }
    public String getErrorMessage()
    {
        return this.errorMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage=errorMessage;
    }
}
