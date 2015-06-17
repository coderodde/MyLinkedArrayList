package net.coderodde.util.list;

/**
 * This interface defines the API for nodes of {@link LinkedArrayList}.
 * 
 * @author Rodion "rodde" Efremov
 */
abstract class LinkedArrayListNode<E> {
    
    /**
     * This field holds the reference to the previous node.
     */
    protected LinkedArrayListNode<E> prev;
    
    /**
     * This field holds the reference to the next node.
     */
    protected LinkedArrayListNode<E> next;
    
    /**
     * The actual storage array.
     */
    protected final Object[] elementArray;
    
    /**
     * This field caches the size of this node. The size of the node is the
     * amount of elements it stores.
     */
    protected int size;
    
    /**
     * Constructs a node with the specified degree.
     * 
     * @param degree the degree of this node.
     */
    LinkedArrayListNode(int degree) {
        this.elementArray = new Object[degree];
    }
    
    protected int size() {
        return size;
    }
    
    /**
     * Appends the element to the tail of this node.
     * 
     * @param element the element to add.
     */
    protected abstract void append(E element);
    
    /**
     * Returns <code>true</code> if this node contains <code>o</code>.
     * 
     * @param  o the object to test for containment.
     * @return <code>true</code> if this node contains <code>o</code>, 
     *         <code>false</code> otherwise.
     */
    protected abstract boolean contains(Object o);
    
    /**
     * Checks whether this node is empty.
     * 
     * @return <code>true</code> if this node is empty, <code>false</code>
     *         otherwise.
     */
    protected boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * Checks whether this node is full.
     * 
     * @return <code>true</code> if this node is full, <code>false</code>
     *         otherwise.
     */
    protected boolean isFull() {
        return size() == elementArray.length;
    }
    
    /**
     * Returns the <code>index</code>th element in this node.
     * 
     * @param  index the index of the element to get.
     * @return the requested element.
     */
    protected abstract E get(int index);
    
    /**
     * Attempts to remove <code>o</code> from this node. If this node contains
     * <code>o</code>, it removes it and returns <code>true</code>. Otherwise,
     * this node is not modified and <code>false</code> is returned.
     * 
     * @param  o the object to remove.
     * @return <code>true</code> if actual removal took place.
     */
    protected abstract boolean remove(Object o);
    
    /**
     * Removes the <code>index</code>th element from this node.
     * 
     * @param  index the index of the element to remove.
     */
    protected abstract void removeAt(int index);
    
    /**
     * Sets the <code>index</code> element of this node to <code>element</code>.
     * 
     * @param index   the index of the target element.
     * @param element the new element to set.
     */
    protected abstract void set(int index, E element);
    
    /**
     * Constructs a new node with the same implementation.
     * 
     * @return a new node with the same implementation as the object of the 
     *         call.
     */
    protected abstract LinkedArrayListNode<E> spawn();
}
