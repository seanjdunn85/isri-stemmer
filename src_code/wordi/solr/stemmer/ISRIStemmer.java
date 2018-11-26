package wordi.solr.stemmer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Ported stemmer class of ISRI Arabic Stemmer
 * Ref: http://www.nltk.org/_modules/nltk/stem/isri.html
 */
public class ISRIStemmer extends TokenFilter {

    public final static List<String> p3 = new ArrayList<String>(
            Arrays.asList(
                    "\u0643\u0627\u0644", "\u0628\u0627\u0644",
                    "\u0648\u0644\u0644", "\u0648\u0627\u0644"
            )
    );

    public final static List<String> p2 = new ArrayList<String>(
            Arrays.asList(
                    "\u0627\u0644", "\u0644\u0644"
            )
    );

    public final static List<String> p1 = new ArrayList<String>(
            Arrays.asList(
                    "\u0644", "\u0628", "\u0641", "\u0633", "\u0648",
                    "\u064a", "\u062a", "\u0646", "\u0627"
            )
    );

    public final static List<String> s3 = new ArrayList<String>(
            Arrays.asList(
                    "\u062a\u0645\u0644", "\u0647\u0645\u0644",
                    "\u062a\u0627\u0646", "\u062a\u064a\u0646",
                    "\u0643\u0645\u0644"
            )
    );

    public final static List<String> s2 = new ArrayList<String>(
            Arrays.asList(
                    "\u0648\u0646", "\u0627\u062a", "\u0627\u0646",
                    "\u064a\u0646", "\u062a\u0646", "\u0643\u0645",
                    "\u0647\u0646", "\u0646\u0627", "\u064a\u0627",
                    "\u0647\u0627", "\u062a\u0645", "\u0643\u0646",
                    "\u0646\u064a", "\u0648\u0627", "\u0645\u0627",
                    "\u0647\u0645"
            )
    );

    public final static List<String> s1 = new ArrayList<String>(
            Arrays.asList(
                    "\u0629", "\u0647", "\u064a", "\u0643", "\u062a",
                    "\u0627", "\u0646"
            )
    );

    public final static Map<Integer, List<String>> pr4 = new HashMap<>();
    static {
        pr4.put(0, new ArrayList<>(Arrays.asList("\u0645")));
        pr4.put(1, new ArrayList<>(Arrays.asList("\u0627")));
        pr4.put(2, new ArrayList<>(Arrays.asList("\u0627", "\u0648", "\u064A")));
        pr4.put(3, new ArrayList<>(Arrays.asList("\u0629")));
    }

    public final static Map<Integer, List<String>> pr53 = new HashMap<>();
    static {
        pr53.put(0, new ArrayList<>(Arrays.asList("\u0627", "\u062a")));
        pr53.put(1, new ArrayList<>(Arrays.asList("\u0627", "\u064a", "\u0648")));
        pr53.put(2, new ArrayList<>(Arrays.asList("\u0627", "\u062a", "\u0645")));
        pr53.put(3, new ArrayList<>(Arrays.asList("\u0645", "\u064a", "\u062a")));
        pr53.put(4, new ArrayList<>(Arrays.asList("\u0645", "\u062a")));
        pr53.put(5, new ArrayList<>(Arrays.asList("\u0627", "\u0648")));
        pr53.put(6, new ArrayList<>(Arrays.asList("\u0627", "\u0645")));
    }

    public final static Pattern reShortVowels = Pattern.compile("[\u064B-\u0652]");
    public final static Pattern reHamza = Pattern.compile("[\u0621\u0624\u0626]");
    public final static Pattern reInitialHamza  = Pattern.compile("^[\u0622\u0623\u0625]");

    public final static List<String> stopWords = new ArrayList<String>(
            Arrays.asList(
                    "\u064a\u0643\u0648\u0646",
                    "\u0648\u0644\u064a\u0633",
                    "\u0648\u0643\u0627\u0646",
                    "\u0643\u0630\u0644\u0643",
                    "\u0627\u0644\u062a\u064a",
                    "\u0648\u0628\u064a\u0646",
                    "\u0639\u0644\u064a\u0647\u0627",
                    "\u0645\u0633\u0627\u0621",
                    "\u0627\u0644\u0630\u064a",
                    "\u0648\u0643\u0627\u0646\u062a",
                    "\u0648\u0644\u0643\u0646",
                    "\u0648\u0627\u0644\u062a\u064a",
                    "\u062a\u0643\u0648\u0646",
                    "\u0627\u0644\u064a\u0648\u0645",
                    "\u0627\u0644\u0644\u0630\u064a\u0646",
                    "\u0639\u0644\u064a\u0647",
                    "\u0643\u0627\u0646\u062a",
                    "\u0644\u0630\u0644\u0643",
                    "\u0623\u0645\u0627\u0645",
                    "\u0647\u0646\u0627\u0643",
                    "\u0645\u0646\u0647\u0627",
                    "\u0645\u0627\u0632\u0627\u0644",
                    "\u0644\u0627\u0632\u0627\u0644",
                    "\u0644\u0627\u064a\u0632\u0627\u0644",
                    "\u0645\u0627\u064a\u0632\u0627\u0644",
                    "\u0627\u0635\u0628\u062d",
                    "\u0623\u0635\u0628\u062d",
                    "\u0623\u0645\u0633\u0649",
                    "\u0627\u0645\u0633\u0649",
                    "\u0623\u0636\u062d\u0649",
                    "\u0627\u0636\u062d\u0649",
                    "\u0645\u0627\u0628\u0631\u062d",
                    "\u0645\u0627\u0641\u062a\u0626",
                    "\u0645\u0627\u0627\u0646\u0641\u0643",
                    "\u0644\u0627\u0633\u064a\u0645\u0627",
                    "\u0648\u0644\u0627\u064a\u0632\u0627\u0644",
                    "\u0627\u0644\u062d\u0627\u0644\u064a",
                    "\u0627\u0644\u064a\u0647\u0627",
                    "\u0627\u0644\u0630\u064a\u0646",
                    "\u0641\u0627\u0646\u0647",
                    "\u0648\u0627\u0644\u0630\u064a",
                    "\u0648\u0647\u0630\u0627",
                    "\u0644\u0647\u0630\u0627",
                    "\u0641\u0643\u0627\u0646",
                    "\u0633\u062a\u0643\u0648\u0646",
                    "\u0627\u0644\u064a\u0647",
                    "\u064a\u0645\u0643\u0646",
                    "\u0628\u0647\u0630\u0627",
                    "\u0627\u0644\u0630\u0649"
            )
    );

    protected CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

    public ISRIStemmer(TokenStream input) {
        super(input);
    }

    /**
     * Overridden abstract method that is called when a token is set to be stemmed.
     * @return True once stemming is finished
     * @throws IOException An IOException
     */
    public final boolean incrementToken() throws IOException {
        String nextToken = null;
        while (nextToken == null) {
            // Reached the end of the token stream being processed
            if ( ! this.input.incrementToken()) {
                return false;
            }
            // Get text of the current token and remove any
            // leading/trailing whitespace.
            String currentTokenInStream =
                    this.input.getAttribute(CharTermAttribute.class)
                            .toString().trim();
            // Save the token if it is not an empty string
            if (currentTokenInStream.length() > 0) {
                nextToken = stem(currentTokenInStream);
            }
        }

        // Save the current token
        this.charTermAttribute.setEmpty().append(nextToken);
        return true;
    }

    public String stem(String token) {
        token = norm(token, 1);
        if(stopWords.contains(token)) {
            return token;
        }
        token = pre32(token);
        token = suf32(token);
        token = waw(token);
        token = norm(token, 2);
        if(token.length() == 4) {
            token = proW4(token);
        }
        else if(token.length() == 5) {
            token = proW53(token);
            token = endW5(token);
        }
        else if(token.length() == 6) {
            token = proW6(token);
            token = endW6(token);
        }
        else if(token.length()==7) {
            token = suf1(token);
            if(token.length()==7) {
                token = pre1(token);
            }
            if(token.length()==6) {
                token = proW6(token);
                token = endW6(token);
            }
        }
        return token;
    }

    /**
     * norm method in ISRI stemmer
     * @param token Word to normalise
     * @return The normalised word
     */
    private String norm(String token, int num){
        switch(num) {
            case (1):
                token = reShortVowels.matcher(token).replaceAll("");
                return token;
            case (2):
                token = reInitialHamza.matcher(token).replaceAll("\u0627");
                return token;
            case (3):
                token = reShortVowels.matcher(token).replaceAll("");
                token = reInitialHamza.matcher(token).replaceAll("\u0627");
                return token;
        }
        return null;
    }

    /**
     * pre32 method in ISRI stemmer
     * @param token Token to stem
     * @return token
     */
    private String pre32(String token) {
        if(token.length() >= 6) {
            for(String pre3 : p3) {
                if(token.startsWith(pre3)) {
                    return token.substring(3);
                }
            }
        }
        if(token.length() >=5) {
            for(String pre2: p2) {
                if(token.startsWith(pre2)) {
                    return token.substring(2);
                }
            }
        }
        return token;
    }

    private String suf32(String token) {
        if(token.length() >= 6) {
            for(String suf3: s3) {
                if(token.endsWith(suf3)) {
                    return token.substring(0, token.length()-3);
                }
            }
        }
        if(token.length() >= 5) {
            for(String suf2: s2) {
                if(token.endsWith(suf2)) {
                    return token.substring(0, token.length()-2);
                }
            }
        }
        return token;
    }

    private String waw(String token) {
        if(token.length()>=4 && token.substring(0,2).equals("\u0648\u0648")) {
            token = token.substring(1);
        }
        return token;
    }

    private String proW4(String token) {
        if(pr4.get(0).contains(token.substring(0,1))) {
            token = token.substring(1);
        }
        else if(pr4.get(1).contains(token.substring(1,2))) {
            token = token.substring(0,1) + token.substring(2);
        }
        else if(pr4.get(2).contains(token.substring(2,3))) {
            token = token.substring(0,2) + token.substring(3,4);
        }
        else if(pr4.get(3).contains(token.substring(3,4))) {
            token = token.substring(0,token.length()-1);
        } else {
            token = suf1(token);
            if(token.length()==4) {
                token = pre1(token);
            }
        }
        return token;
    }

    private String proW53(String token) {
        if(pr53.get(0).contains(token.substring(2,3)) && token.substring(0,1).equals("\u0627")) {
            token = token.substring(1, 2) + token.substring(3);
        } else if(pr53.get(1).contains(token.substring(3,4)) && token.substring(0,1).equals("\u0645")) {
            token = token.substring(1,3) + token.substring(4,5);
        } else if(pr53.get(2).contains(token.substring(0,1)) && token.substring(4,5).equals("\u0629")) {
            token = token.substring(1,4);
        } else if(pr53.get(3).contains(token.substring(0,1)) && token.substring(2,3).equals("\u062a")) {
            token = token.substring(1,2) + token.substring(3);
        } else if(pr53.get(4).contains(token.substring(0,1)) && token.substring(2,3).equals("\u0627")) {
            token = token.substring(1,2) + token.substring(3);
        } else if(pr53.get(5).contains(token.substring(2,3)) && token.substring(4,5).equals("\u0629")) {
            token = token.substring(0,2) + token.substring(3,4);
        } else if(pr53.get(6).contains(token.substring(0,1)) && token.substring(1,2).equals("\u0646")) {
            token = token.substring(2);
        } else if(token.substring(3,4).equals("\u0627") && token.substring(0,1).equals("\u0627")) {
            token = token.substring(1,3) + token.substring(4,5);
        } else if(token.substring(4,5).equals("\u0646") && token.substring(3,4).equals("\u0627")) {
            token = token.substring(0,3);
        } else if(token.substring(3,4).equals("\u064a") && token.substring(0,1).equals("\u062a")) {
            token = token.substring(1,3) + token.substring(4,5);
        } else if(token.substring(3,4).equals("\u0648") && token.substring(1,2).equals("\u0627")) {
            token = token.substring(0,1) + token.substring(2,3) + token.substring(4,5);
        } else if(token.substring(2,3).equals("\u0627") && token.substring(1,2).equals("\u0648")) {
            token = token.substring(0,1) + token.substring(3);
        } else if(token.substring(3,4).equals("\u0626") && token.substring(2,3).equals("\u0627")) {
            token = token.substring(0,2) + token.substring(4,5);
        } else if(token.substring(4,5).equals("\u0629") && token.substring(1,2).equals("\u0627")) {
            token = token.substring(0,1) + token.substring(2,4);
        } else if(token.substring(4,5).equals("\u064a") && token.substring(2,3).equals("\u0627")) {
            token = token.substring(0,2) + token.substring(3,4);
        } else {
            token = suf1(token);
            if(token.length() == 5) {
                token = pre1(token);
            }
        }
        return token;
    }

    private String proW54(String token) {
        if(pr53.get(2).contains(token.substring(0,1))) {
            token = token.substring(1);
        } else if(token.substring(4,5).equals("\u0629")) {
            token = token.substring(0,4);
        } else if(token.substring(2,3).equals("\u0627")) {
            token = token.substring(0,2) + token.substring(3);
        }
        return token;
    }

    private String endW5(String token) {
        if(token.length() == 4) {
            token = proW4(token);
        } else if(token.length() == 5) {
            token = proW54(token);
        }
        return token;
    }

    private String proW6(String token) {
        if(token.startsWith("\u0627\u0633\u062a") || token.startsWith("\u0645\u0633\u062a")) {
            token = token.substring(3);
        } else if(token.substring(0,1).equals("\u0645") && token.substring(3,4).equals("\u0627") && token.substring(5,6).equals("\u0629")) {
            token = token.substring(1,3) + token.substring(4,5);
        } else if(token.substring(0,1).equals("\u0627") && token.substring(2,3).equals("\u062a") && token.substring(4,5).equals("\u0627")) {
            token = token.substring(1,2) + token.substring(3,4) + token.substring(5,6);
        } else if(token.substring(0,1).equals("\u0627") && token.substring(3,4).equals("\u0648") && token.substring(2,3).equals(token.substring(4,5))) {
            token = token.substring(1,2) + token.substring(4);
        } else if(token.substring(0,1).equals("\u062a") && token.substring(2,3).equals("\u0627") && token.substring(4,5).equals("\u064a")) {
            token = token.substring(1,2) + token.substring(3,4) + token.substring(5,6);
        } else {
            token = suf1(token);
            if(token.length() == 6) {
                token = pre1(token);
            }
        }
        return token;
    }

    private String proW64(String token) {
        if(token.substring(0,1).equals("\u0627") && token.substring(4,5).equals("\u0627")) {
            token = token.substring(1,4) + token.substring(5,6);
        } else if(token.startsWith("\u0645\u062a")) {
            token = token.substring(2);
        }
        return token;
    }

    private String endW6(String token) {
        if(token.length()==5) {
            token = proW53(token);
            token = endW5(token);
        } else if (token.length() == 6) {
            token = proW64(token);
        }
        return token;
    }

    private String suf1(String token) {
        for(String sf1 : s1) {
            if(token.endsWith(sf1)) {
                return token.substring(0, token.length()-1);
            }
        }
        return token;
    }

    private String pre1(String token) {
        for(String sp1 : p1) {
            if(token.startsWith(sp1)) {
                return token.substring(1);
            }
        }
        return token;
    }

    public static void main(String[] args) {
        String t = "Hello world";
        System.out.println(t.substring(0,1));
    }
}
