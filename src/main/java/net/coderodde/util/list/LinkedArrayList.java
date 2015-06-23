package net.coderodde.util.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a list data structure consisting of a list of arrays. 
 * One parameter of this structure is <b><i>degree</i></b> which is the length
 * of each array.
 * 
 * @author    Rodion "rodde" Efremov
 * @version   1.6
 * @param <E> the actual list element type.
 */
public class LinkedArrayList<E> implements List<E>, Cloneable {

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
     * The node type of this list.
     */
    private NodeType nodeType;
    
    /**
     * This field caches the amount of elements stored in this list.
     */
    private int size;
    
    /**
     * This field caches the amount of modifications made to this list.
     */
    private transient long modCount;
    
    /**
     * This field holds the head node of this list.
     */
    private transient LinkedArrayListNode<E> head;
    
    /**
     * This field holds the tail node of this list.
     */
    private transient LinkedArrayListNode<E> tail;
    
    /**
     * Used for searching an element.
     */
    private transient LinkedArrayListNode<E> searchNode;
    
    /**
     * Used for searching an element.
     */
    private transient int searchLocalIndex;
    
    /**
     * Used for more efficient bulk addition.
     */
    private transient List<E> bulkLoadList;
    
    /**
     * Used for more efficient bulk addition.
     */
    private transient List<E> nodeLeftoverList;
    
    /**
     * Used for holding the elements while inserting a collection at random 
     * index.
     */
    private transient List<E> workList;
    
    /**
     * Constructs a new, empty list with given degree and node type.
     * 
     * @param degree   the degree of the new list.
     * @param nodeType the type of the nodes.
     */
    public LinkedArrayList(int degree, NodeType nodeType) {
        checkDegree(degree);
        this.nodeType = nodeType;
        
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
        this.workList = new ArrayList<>(getDegree());
        this.bulkLoadList = new ArrayList<>(degree);
        this.nodeLeftoverList = new ArrayList<>(degree);
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
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            // List not changed, return 'false'.
            return false;
        }
        
        Iterator<? extends E> iter = c.iterator();
        LinkedArrayListNode<E> node = tail;
        
        while (iter.hasNext()) {
            E element = iter.next();
            
            if (node.isFull()) {
                LinkedArrayListNode<E> newnode = node.spawn();
                node.next = newnode;
                newnode.prev = node;
                node = newnode;
            }
            
            node.append(element);
        }
        
        tail = node;
        size += c.size();
        modCount += c.size();
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkIndexForAddition(index);
        
        if (index == size()) {
            return addAll(c);
        }
        
        if (c.isEmpty()) {
            return false;
        }
        
        searchElement(index);
        LinkedArrayListNode<E> newTail = searchNode.addAll(searchLocalIndex, 
                                                           c, 
                                                           workList);
        if (newTail != null) {
            tail = newTail;
        }
        
//        // Find the node containing the insert location.
//        searchElement(index);
//        final Iterator<? extends E> iterator = c.iterator();
//        
//        searchNode.split(searchLocalIndex, nodeLeftoverList);
//        
//        while (!searchNode.isFull() && iterator.hasNext()) {
//            searchNode.append(iterator.next());
//        }
//        
//        if (iterator.hasNext()) {
//            LinkedArrayListNode<E> insertHead = head.spawn();
//            LinkedArrayListNode<E> insertTail = insertHead;
//            
//            while (iterator.hasNext()) {
//                bulkLoadList.add(iterator.next());
//                
//                if (bulkLoadList.size() == getDegree()) {
//                    insertTail.setAll(bulkLoadList);
//                    bulkLoadList.clear();
//                    
//                    if (iterator.hasNext()) {
//                        LinkedArrayListNode<E> newnode = head.spawn();
//                        insertTail.next = newnode;
//                        newnode.prev = insertTail;
//                        insertTail = newnode;
//                    }
//                }
//            }
//            
//            if (!bulkLoadList.isEmpty()) {
//                insertTail.setAll(bulkLoadList);
//                bulkLoadList.clear();
//            }
//            
//            linkChain(searchNode, insertHead, insertTail);
//        }
//        
//        if (!nodeLeftoverList.isEmpty()) {
//            
//        }
        
        size += c.size();
        modCount += c.size();
        bulkLoadList.clear();
        nodeLeftoverList.clear();
        workList.clear();
        return true;
    }

    @Override
    public void clear() {
        head = head.spawn();
        tail = head;
        size = 0;
        ++modCount;
    }
    
    @Override
    public Object clone() {
        final int degree = head.getDegree();
        List<E> ret = new LinkedArrayList<>(degree, nodeType);
        List<E> tmp = new ArrayList<>(degree);

        for (LinkedArrayListNode<E> node = head;
                node != null;
                node = node.next) {
            final int nodeSize = node.size();

            for (int i = 0; i < nodeSize; ++i) {
                tmp.add(node.get(i));

                if (tmp.size() == degree) {
                    ret.addAll(tmp);
                    tmp.clear();
                }
            }
        }

        if (!tmp.isEmpty()) {
            System.out.println("Yoo! ^^");
            ret.addAll(tmp);
        }

        return ret;
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
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public E get(int index) {
        checkIndexForAccess(index);
        searchElement(index);
        return searchNode.get(searchLocalIndex);
    }
    
    /**
     * Returns the degree of this list.
     * 
     * @return the degree.
     */
    public int getDegree() {
        return head.getDegree();
    }
    
    @Override
    public int indexOf(Object o) {
        int index = 0;
        
        for (LinkedArrayListNode<E> node = head; 
                node != null; 
                node = node.next) {
            final int nodeSize = node.size();
            
            for (int i = 0; i < nodeSize; ++i, ++index) {
                if (Objects.equals(o, node.get(i))) {
                    return index;
                }
            }
        }
        
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new BasicLinkedArrayListIterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size() - 1;
        
        for (LinkedArrayListNode<E> node = tail;
                node != null;
                node = node.prev) {
            final int nodeSize = node.size();
            
            for (int i = nodeSize - 1; i >= 0; --i, --index) {
                if (Objects.equals(o, node.get(i))) {
                    return index;
                }
            }
        }
        
        return -1;
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
    public E remove(int index) {
        checkIndexForAccess(index);
        searchElement(index);
        E ret = searchNode.get(searchLocalIndex);
        searchNode.removeAt(searchLocalIndex);
        
        if (searchNode.isEmpty()) {
            unlinkNode(searchNode);
        }
        
        ++modCount;
        --size;
        return ret;
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
    public boolean removeAll(Collection<?> c) {
        return bulkContainOperation(c, true);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return bulkContainOperation(c, false);
    }

    @Override
    public E set(int index, E element) {
        checkIndexForAccess(index);
        searchElement(index);
        E ret = searchNode.get(searchLocalIndex);
        searchNode.set(searchLocalIndex, element);
        return ret;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        Object[] ret = new Object[size];
        int index = 0;
        
        for (LinkedArrayListNode<E> node = head; 
                node != null; 
                node = node.next) {
            final int nodeSize = node.size();
            
            for (int i = 0; i < nodeSize; ++i, ++index) {
                ret[index] = node.get(i);
            }
        }
        
        return ret;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Object[] arr = toArray();
        
        if (a.length < size) {
            return (T[]) Arrays.copyOf(arr, size, a.getClass());
        }
        
        System.arraycopy(arr, 0, a, 0, size);
        
        if (a.length > size) {
            a[size] = null;
        }
        
        return a;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        int index = 0;
        
        for (E element : this) {
            sb.append(element);
            
            if (index < size() - 1) {
                sb.append(", ");
            }
            
            ++index;
        }
        
        return sb.append("]").toString();
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
    
    private void checkIndexForAddition(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "The index is negative: " + index);
        }
        
        if (index > size()) {
            throw new IllegalArgumentException(
                    "The index is tool large: " + index + ". " +
                    "The size of this list is " + size() + ".");
        }
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
     * Removes from this list all elements <code>e</code> for which 
     * <code>col.contains(e)</code> is <code>mode</code>.
     * 
     * @param col  the collection of elements to consider.
     * @param mode the mode.
     * @return <code>true</code> only if this list has changed during this
     *         operation.
     */
    private boolean bulkContainOperation(Collection<?> col, boolean mode) {
        Set<E> set = null;
        
        if (col instanceof HashSet) {
            set = (HashSet<E>) col;
        } else {
            set = new HashSet<>((Collection<E>) col);
        }
        
        boolean modified = false;
        
        for (LinkedArrayListNode<E> node = tail; 
                node != null;
                node = node.prev) {
            for (int i = node.size() - 1; i >= 0; --i) {
                if (set.contains(node.get(i)) == mode) {
                    modified = true;
                    node.removeAt(i);
                    --size;
                    ++modCount;
                    
                    if (node.isEmpty()) {
                        unlinkNode(node);
                    }
                }
            }
        }
        
        return modified;
    }
    
    /**
     * Links the chain of nodes between <code>chainBegin</code> and 
     * <code>chainEnd</code> between <code>predecessor</code> and
     * <code>predecessor.next</code>.
     * 
     * @param predecessor the predecessor node of the entire chain.
     * @param chainBegin  the first node of the chain to insert.
     * @param chainEnd    the last node of the chain to insert.
     */
    private void linkChain(LinkedArrayListNode<E> predecessor,
                           LinkedArrayListNode<E> chainBegin,
                           LinkedArrayListNode<E> chainEnd) {
        chainEnd.next = predecessor.next;
        predecessor.next.prev = chainEnd;
        predecessor.next = chainBegin;
        chainBegin.prev = predecessor;
        
        if (chainEnd.next == null) {
            tail = chainEnd;
        }
    }
    /**
     * Loads the node and local index of the element at global index 
     * <code>index</code>.
     * 
     * @param index the global index of the element to search.
     */
    private void searchElement(int index) {
        if (index < size() / 2) {
            // Access starting from the head. There is a chance that we will 
            // traverse less nodes than starting from the tail.
            LinkedArrayListNode<E> node = head;

            while (index >= node.size()) {
                index -= node.size();
                node = node.next;
            }

            searchNode = node;
            searchLocalIndex = index;
        } else {
            // Access starting from the tail moving to the "left".
            LinkedArrayListNode<E> node = tail;
            index = size() - index - 1;
            
            while (index >= node.size()) {
                index -= node.size();
                node = node.prev;
            }
            
            searchNode = node;
            searchLocalIndex = node.size() - index - 1;
        }
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
     * Implements a basic iterator of type <code>Iterator</code>.
     */
    private class BasicLinkedArrayListIterator implements Iterator<E> {

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
