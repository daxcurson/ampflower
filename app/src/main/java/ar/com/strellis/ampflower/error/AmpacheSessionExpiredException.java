package ar.com.strellis.ampflower.error;

import ar.com.strellis.ampflower.data.model.AmpacheError;

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
