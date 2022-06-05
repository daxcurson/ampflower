package ar.com.strellis.ampflower.error;

public class AmpacheExceptionFactory
{
    public static AmpacheException getException(int errorCode)
    {
        AmpacheException e;
        switch(errorCode)
        {
            case 4701:
                e=new AmpacheSessionExpiredException();
                return e;
            default:
                return new AmpacheUnknownException();
        }
    }
}
