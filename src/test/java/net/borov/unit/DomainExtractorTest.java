package net.borov.unit;

import com.google.common.base.Function;
import net.borov.processors.DomainExtractor;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class DomainExtractorTest {

    static String[][] testDomains = {
            {"https://ya.ru", "ya.ru"},
            {"http://www.who.am.i?", "www.who.am.i"},
            {"http://www.big.white.unicorn.ru/page/2", "www.big.white.unicorn.ru"},
            {"http://www.segmento.ru/", "www.segmento.ru"},
            {"not-a-domain", null}
    };
    
    String input;
    String expect;
    
    Function<String, String> processor = new DomainExtractor();

    @Parameterized.Parameters
    public static java.util.Collection<Object[]> data() {
        return Arrays.asList((Object[][])testDomains);
    }

    public DomainExtractorTest(String input, String expect) {
        this.input = input;
        this.expect = expect;
    }

    @org.junit.Test
    public void shouldExtractDomainsFromUrl() {
        Assert.assertEquals(expect, processor.apply(input));
    }

}
