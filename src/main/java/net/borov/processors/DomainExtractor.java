package net.borov.processors;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts domains from URL
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class DomainExtractor implements Function<String, String> {

    // http://www.ietf.org/rfc/rfc3986.txt
    private static Pattern urlPattern = Pattern.compile("^(?:(?:[^:/?#]+):)?(?://" +
            "([^/?#]*))" + // group, we are interested in
            "?(?:[^?#]*)(?:\\?([^#]*))?(?:#(?:.*))?");


    @Override
    public @Nullable String apply(@Nullable String item) {
       Matcher matcher = urlPattern.matcher(item);
        if(matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
