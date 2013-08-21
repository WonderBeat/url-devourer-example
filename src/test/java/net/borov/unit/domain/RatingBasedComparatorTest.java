package net.borov.unit.domain;

import net.borov.domain.RatedElement;
import net.borov.domain.RatingBasedComparator;
import org.junit.Assert;
import org.junit.Test;

public class RatingBasedComparatorTest {
    @Test
    public void shouldCompareElements() throws Exception {
        RatedElement element = new RatedElement("ONE", 2);
        RatedElement greaterElement= new RatedElement("TWO", 3);
        RatingBasedComparator comparator = new RatingBasedComparator();
        Assert.assertEquals(-1, comparator.compare(element, greaterElement));
        Assert.assertEquals(1, comparator.compare(greaterElement, element));
        Assert.assertEquals(0, comparator.compare(element, element));
    }
}
