package net.borov.domain;

import java.util.Comparator;

/**
 * Comparator, based on rating
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class RatingBasedComparator implements Comparator<RatedElement> {

    @Override
    public int compare(RatedElement one, RatedElement another) {
        long distance = one.getRating() - another.getRating();
        if(distance == 0) {
            return 0;
        } else if(distance < 0) {
            return -1;
        }
        return 1;
    }
}
