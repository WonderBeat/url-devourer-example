package net.borov.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Contains string element and it's numerical rating
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class RatedElement {

    private String element;

    private long rating;

    public RatedElement(String element, long rating) {
        this.rating = rating;
        this.element = element;
    }

    public RatedElement() {
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof RatedElement)) return false;
        RatedElement otherElement = (RatedElement)other;
        return new EqualsBuilder().append(element, otherElement.element).append(rating, otherElement.rating).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(element).append(rating).toHashCode();
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
