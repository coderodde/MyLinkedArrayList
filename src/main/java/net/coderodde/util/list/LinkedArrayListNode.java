package net.coderodde.util.list;

import java.util.Collection;
import java.util.List;

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
    
    /**
     * Inserts the elements in <code>collection</code> starting from this node
     * at index <code>index</code>. The elements are inserted in the same order
     * as the iterator of {@code collection} returns them.
     * 
     * @param index      the insertion index.
     * @param collection the collection
     * @param workList   the auxiliary list.
     * @return           a chain of new nodes or null if {@code collection} fits
     *                   in this node.
     */
    protected abstract LinkedArrayListNode<E>
        addAll(int index, Collection<? extends E> collection, List<E> workList);
    
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
     * Returns the <code>index</code>th element in this node.
     * 
     * @param  index the index of the element to get.
     * @return the requested element.
     */
    protected abstract E get(int index);
    
    /**
     * Returns the degree of this node, which effectively is its capacity.
     * 
     * @return the degree of this node.
     */
    protected abstract int getDegree();
    
    /**
     * Inserts the {@code element} into this node before the element with index
     * {@code localIndex}. If {@code element} does not fit into this node, this
     * node is spawns a new, empty node. Then, it appends everything starting at
     * index {@code localIndex} to the spawned node, and inserts {@code element}
     * into this node.
     * 
     * @param localIndex the local index of insertion.
     * @param element    the element to insert.
     * @return the continuation node if this node is full. {@code null} 
     *         otherwise.
     */
    protected abstract LinkedArrayListNode<E> insert(int localIndex, E element);
    
    /**
     * Checks that this node is healthy. The node is considered healthy if its
     * unused array components hold value {@code null}. Basically we try to 
     * assert that the garbage collector frees the objects as early as possible.
     * Also, we try to assert that this node is not empty for the same reason.
     * 
     * @return {@code true} if this node is healthy.
     */
    protected abstract boolean isHealthy();
    
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
     * Sets the contents of this node.
     * 
     * @param list the list of elements to set.
     */
    protected void setAll(List<E> list) {
        int index = 0;
        
        for (E element : list) {
            elementArray[index++] = element;
        }
        
        size = list.size();
    }
    
    /**
     * Shifts all the bunch of elements in this node to the beginning of this
     * node.
     */
    protected abstract void shiftToBegining();
    
    /**
     * Constructs a new node with the same implementation.
     * 
     * @return a new node with the same implementation as the object of the 
     *         call.
     */
    protected abstract LinkedArrayListNode<E> spawn();
    
    /**
     * Loads into the <code>list</code> all elements from this node whose
     * indices are at least <code>splitIndex</code>.
     * 
     * @param splitIndex the split index.
     * @param list       the backup list.
     */
    protected abstract void split(int splitIndex, List<E> list);
    
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
     * Returns the amount of elements in this node.
     * 
     * @return the size of this node.
     */
    protected int size() {
        return size;
    }
}
