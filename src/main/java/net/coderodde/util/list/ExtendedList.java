package net.coderodde.util.list;

import java.util.List;

/**
 * This class adds the {@code removeRange(int, int)} method to the 
 * {@link java.util.List}  interface.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6
 * @param <E> the actual element type.
 */
public interface ExtendedList<E> extends List<E> {
    
    /**
     * Removes the elements with indices {@code fromIndex, fromIndex + 1, ...,
     * toIndex - 2, toIndex - 1}.
     * 
     * @param fromIndex the starting (inclusive) index.
     * @param toIndex   the ending (exclusive) index.
     */
    public void removeRange(int fromIndex, int toIndex);
}
