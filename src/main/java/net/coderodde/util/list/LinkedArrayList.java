package net.coderodde.util.list;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This class implements a list data structure consisting of a list of arrays. 
 * One parameter of this structure is <b><i>degree</i></b> which is the length
 * of each array.
 * 
 * @author    Rodion "rodde" Efremov
 * @version   1.6
 * @param <E> the actual list element type.
 */
public class LinkedArrayList<E> implements List<E> {

    /**
     * This enumeration is used for choosing the actual list node 
     * implementation.
     */
    public enum NodeType {
        TRIVIAL,
        ADVANCED
    }
    
    /**
     * The minimum degree of any <code>LinkedArrayList</code>.
     */
    private static final int MINIMUM_DEGREE = 2;
    
    /**
     * The default degree.
     */
    private static final int DEFAULT_DEGREE = 16;
    
    /**
     * This field caches the amount of elements stored in this list.
     */
    private transient int size;
    
    /**
     * This field caches the amount of modifications made to this list.
     */
    private transient long modCount;
    
    /**
     * This field holds the head node of this list.
     */
    private LinkedArrayListNode<E> head;
    
    /**
     * This field holds the tail node of this list.
     */
    private LinkedArrayListNode<E> tail;
    
    /**
     * Constructs a new, empty list with given degree and node type.
     * 
     * @param degree   the degree of the new list.
     * @param nodeType the type of the nodes.
     */
    public LinkedArrayList(int degree, NodeType nodeType) {
        checkDegree(degree);
        
        switch (nodeType) {
            case TRIVIAL:
                this.head = new LinkedArrayListNode1<>(degree);
                break;
                
            case ADVANCED:
                this.head = new LinkedArrayListNode2<>(degree);
                break;
                
            default:
                throw new IllegalArgumentException(
                "Unsupported node type enumeration: " + nodeType);
        }
        
        this.tail = head;
    }
    
    /**
     * Constructs a new, empty list with given degree.
     * 
     * @param degree the degree of the new list.
     */
    public LinkedArrayList(int degree) {
        this(degree, NodeType.ADVANCED);
    }
    
    /**
     * Constructs a new, empty list with default degree and input node type.
     * @param nodeType 
     */
    public LinkedArrayList(NodeType nodeType) {
        this(DEFAULT_DEGREE);
    }
    
    /**
     * Constructs a new, empty list with default degree and node type.
     */
    public LinkedArrayList() {
        this(DEFAULT_DEGREE);
    }
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (LinkedArrayListNode<E> node = head;
                node != null;
                node = node.next) {
            if (node.contains(o)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedArrayListIterator();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(E e) {
        if (tail.isFull()) {
            LinkedArrayListNode<E> newnode = tail.spawn();
            newnode.append(e);
            tail.next = newnode;
            newnode.prev = tail;
            tail = newnode;
        } else {
            tail.append(e);
        }
        
        ++size;
        ++modCount;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (LinkedArrayListNode<E> node = head; 
                node != null; 
                node = node.next) {
            
            if (node.remove(o)) {
                --size;
                ++modCount;
                
                if (node.isEmpty()) {
                    unlinkNode(node);
                }
                
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        head = head.spawn();
        tail = head;
        
        size = 0;
        ++modCount;
    }

    @Override
    public E get(int index) {
        checkIndexForAccess(index);
        
        if (index < size() / 2) {
            // Access starting from the head. There is a chance that we will 
            // traverse less nodes than starting from the tail.
            LinkedArrayListNode<E> node = head;

            while (index >= node.size()) {
                index -= node.size();
                node = node.next;
            }

            return node.get(index);
        } else {
            // Access starting from the tail moving to the "left".
            LinkedArrayListNode<E> node = tail;
            index = size() - index - 1;
            
            while (index >= node.size()) {
                index -= node.size();
                node = node.prev;
            }
            
            return node.get(node.size() - index - 1);
        }
    }

    @Override
    public E set(int index, E element) {
        checkIndexForAccess(index);
        
        if (index < size() / 2) {
            LinkedArrayListNode<E> node = head;
            
            while (index >= node.size()) {
                index -= node.size();
                node = node.next;
            }
            
            E ret = node.get(index);
            node.set(index, element);
            return ret;
        } else {
            LinkedArrayListNode<E> node = tail;
            index = size() - index - 1;
            
            while (index >= node.size()) {
                index -= node.size();
                node = node.prev;
            }
            
            E ret = node.get(index);
            node.set(index, element);
            return ret;
        }
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Unlinks the node from the node chain.
     * 
     * @param node the node to unlink.
     */
    private void unlinkNode(LinkedArrayListNode<E> node) {
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node.prev == null && node.next == null) {
            // 'node' is the only node in this list. Do not remove.
        } else if (node.prev == null) {
            // 'node' is the head node and is not the only node.
            head = node.next;
            head.prev = null;
        } else {
            // 'node' is the tail node and is not the only node.
            tail = node.prev;
            tail.next = null;
        }
        
        // If no match, 'node' is the only node in this list; do not remove.
    }
    
    /**
     * Validates the degree.
     * 
     * @param degree the degree value to validate.
     * 
     * @throws IllegalArgumentException if degree is too small.
     */
    private static void checkDegree(int degree) {
        if (degree < MINIMUM_DEGREE) {
            throw new IllegalArgumentException(
            "The input degree (" + degree + ") is too small. Should be at " +
            "least " + MINIMUM_DEGREE + ".");
        }
    }
    
    /**
     * Validates the access index.
     * 
     * @param index the index to validate.
     * 
     * @throws IllegalArgumentException if the index is too small or too large.
     */
    private void checkIndexForAccess(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "The index is negative: " + index);
        }
        
        if (index >= size()) {
            throw new IllegalArgumentException(
                    "The index is too large: " + index + ". " +
                    "The size of this list is " + size() + ".");
        }
    }
    
    private class LinkedArrayListIterator implements Iterator<E> {

        /**
         * The expected mod count.
         */
        private long expectedModCount = modCount;
        
        /**
         * The current node being iterated.
         */
        private LinkedArrayListNode<E> node = head;
        
        /**
         * The next index of the element to iterate within the current node.
         * The semantics of this field is "increment first, then use".
         */
        private int localIndex = -1;
        
        /**
         * The amount of elements iterated.
         */
        private int iterated = 0;
        
        /**
         * Is set to <code>true</code> if the last element was removed. 
         */
        private boolean lastRemoved = false;
        
        /**
         * The amount of elements in the list being iterated at the moment of
         * creation of this iterator.
         */
        private final int maxElements = size;
        
        @Override
        public boolean hasNext() {
            return iterated < maxElements;
        }

        @Override
        public E next() {
            checkCoModification();
            
            if (iterated == maxElements) {
                throw new NoSuchElementException("The iterator exceeded.");
            }
            
            if (lastRemoved) {
                lastRemoved = false;
                ++iterated;
                
                if (localIndex == node.size()) {
                    localIndex = 0;
                    node = node.next;
                }
                
                // No need for incrementing 'localIndex'.
                return node.get(localIndex);
            }
            
            ++iterated;
            
            if (++localIndex == node.size()) {
                localIndex = 0;
                node = node.next;
            }
            
            return node.get(localIndex);
        }
        
        @Override
        public void remove() {
            if (lastRemoved) {
                throw new IllegalStateException(
                        "Removing the same element twice.");
            }
            
            if (iterated == 0) {
                throw new IllegalStateException("No current element.");
            }
            
            checkCoModification();
            lastRemoved = true;
            --size;
            
            node.removeAt(localIndex);
            
            if (node.isEmpty()) {
                LinkedArrayListNode<E> next = node.next;
                unlinkNode(node);
                node = next;
                localIndex = 0;
            }
        }
     
        private void checkCoModification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
