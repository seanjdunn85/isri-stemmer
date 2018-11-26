package wordi.solr.stemmer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/**
 * Created by tomkd on 02/02/2017.
 */
public class ISRIStemmerFactory extends TokenFilterFactory {

    public ISRIStemmerFactory(Map<String, String> args) {
        super(args);
    }

    public TokenStream create(TokenStream input) {
        return new ISRIStemmer(input);
    }
}
