package net.borov.processors;

import com.google.common.base.Function;
import com.google.common.net.InternetDomainName;

import javax.annotation.Nullable;

/**
 * For a given domain name extracts private domain
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class PrivateDomainExtractor implements Function<String, String> {

    @Override
    public @Nullable
    String apply(@Nullable String item) {
        if(item == null) return null;
        try {
            return tryExtractPrivateDomain(item);
        } catch (IllegalStateException ex) { // contains no private domain
            return null;
        }
    }

    private String tryExtractPrivateDomain(@Nullable String item) {
        InternetDomainName domainName = InternetDomainName.from(item);
        return domainName.topPrivateDomain().name();
    }
}
