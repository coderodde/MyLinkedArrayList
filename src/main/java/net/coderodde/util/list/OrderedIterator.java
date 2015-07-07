package net.coderodde.util.list;

import java.util.Iterator;

/**
 * Defines an iterator that operates over an ordered container. Subset of {@link java.util.ListIterator}.
 * <p>
 * This iterator allows both forward and reverse iteration through the container.
 *
 * @param <E> the type to iterate over
 * @since 3.0
 * @version $Id: OrderedIterator.java 1469004 2013-04-17 17:37:03Z tn $
 */
public interface OrderedIterator<E> extends Iterator<E> {

    /**
     * Checks to see if there is a previous element that can be iterated to.
     *
     * @return <code>true</code> if the iterator has a previous element
     */
    boolean hasPrevious();

    /**
     * Gets the previous element from the container.
     *
     * @return the previous element in the iteration
     * @throws java.util.NoSuchElementException if the iteration is finished
     */
    E previous();

}
