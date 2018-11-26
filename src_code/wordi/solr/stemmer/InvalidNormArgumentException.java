package wordi.solr.stemmer;

/**
 * Created by tomkd on 02/02/2017.
 */
public class InvalidNormArgumentException extends Exception {

    public InvalidNormArgumentException() {
        super("Argument 'num' in method norm, must be 1, 2, or 3");
    }
}
