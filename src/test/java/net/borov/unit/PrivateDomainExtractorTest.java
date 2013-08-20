package net.borov.unit;

import com.google.common.base.Function;
import net.borov.processors.PrivateDomainExtractor;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class PrivateDomainExtractorTest {


    static String[][] testDomains = {
            {"ya.spb.ru", "ya.spb.ru"},
            {"cats.com", "cats.com"},
            {"keyboard.cat.com", "cat.com"},
            {"no.uk", null} // no private domain at all
    };

    String input;
    String expect;

    Function<String, String> processor = new PrivateDomainExtractor();

    @Parameterized.Parameters
    public static java.util.Collection<Object[]> data() {
        return Arrays.asList((Object[][]) testDomains);
    }

    public PrivateDomainExtractorTest(String input, String expect) {
        this.input = input;
        this.expect = expect;
    }

    @org.junit.Test
    public void shouldExtractDomainsFromUrl() {
        Assert.assertEquals(expect, processor.apply(input));
    }


}
