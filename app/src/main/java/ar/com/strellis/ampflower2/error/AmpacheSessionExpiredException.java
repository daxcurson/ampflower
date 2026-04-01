package ar.com.strellis.ampflower2.error;

public class AmpacheSessionExpiredException extends AmpacheException
{
    public AmpacheSessionExpiredException()
    {
        super("No message provided");
    }
    public AmpacheSessionExpiredException(String error) {
        super(error);
    }
}
