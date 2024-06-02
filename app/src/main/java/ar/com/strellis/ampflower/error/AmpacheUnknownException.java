package ar.com.strellis.ampflower.error;

public class AmpacheUnknownException extends AmpacheException
{
    public AmpacheUnknownException()
    {
        super("No message provided");
    }
    public AmpacheUnknownException(String error) {
        super(error);
    }
}
