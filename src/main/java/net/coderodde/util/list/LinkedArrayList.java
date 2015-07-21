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
public class LinkedArrayList<E> implements ExtendedList<E>, Cloneable {

    /**
     * This enumeration is used for choosing the actual list node 
     * implementation.
     */
    public enum NodeType {
        TRIVIAL,
        ADVANCED
    }
    
    /**
     * The minimum degree of any {@code LinkedArrayList}.
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
     * Used for holding the elements while inserting a collection at random 
     * index.
     */
    private transient List<E> workList;
    
    /**
     * The modification counter.
     */
    private int modCount;
    
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
     * Constructs a new, empty list with default degree and specified node type.
     * 
     * @param nodeType the type of internal node type.
     */
    public LinkedArrayList(NodeType nodeType) {
        this(DEFAULT_DEGREE);
    }
    
    /**
     * Constructs a new, empty list with default degree and default node type.
     */
    public LinkedArrayList() {
        this(DEFAULT_DEGREE);
    }

    /**
     * Appends {@code e} to the tail of this list.
     * 
     * @param  e the element to append.
     * @return always {@code true}.
     */
    @Override
    public boolean add(E e) {
        if (tail.isFull()) {
            LinkedArrayListNode<E> newnode = tail.spawn();
            newnode.append(e);
            tail.setNextNode(newnode);
            newnode.setPreviousNode(tail);
            tail = newnode;
        } else {
            tail.append(e);
        }
        
        ++size;
        ++modCount;
        return true;
    }

    /**
     * Inserts {@code element} between the elements with indices 
     * {@code index - 1} and {@code index}.
     * 
     * @param index   the insertion index.
     * @param element the element to insert.
     */
    @Override
    public void add(int index, E element) {
        checkIndexForAddition(index);
        
        if (index == size) {
            add(element);
            return;
        }
        
        searchElement(index);
        LinkedArrayListNode<E> newnode = searchNode.insert(searchLocalIndex, 
                                                           element);
        if (newnode != null) {
            linkNode(searchNode, newnode);
        }
            
        ++size;
        ++modCount;
    }

    /**
     * Appends elements from {@code c} to the tail of this list. The elements
     * are appended in the same order as they are returned by the iterator of
     * {@code c}.
     * 
     * @param  c the collection holding the elements to append.
     * @return {@code true} if this list changed after this operation.
     */
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
                node.setNextNode(newnode);
                newnode.setPreviousNode(node);
                node = newnode;
            }
            
            node.append(element);
        }
        
        tail = node;
        size += c.size();
        modCount += c.size();
        return true;
    }

    /**
     * Inserts the elements in {@code c} in this list between elements with 
     * indices {@code index - 1} and {@code index}. The elements are inserted in
     * the same order as the iterator of {@code c} returns them.
     * 
     * @param  index the insertion index.
     * @param  c     the collection holding the elements to insert.
     * @return {@code true} if this list changed after the operation, 
     *         {@code false} otherwise, or namely, if {@code c} is empty.
     */
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
        
        size += c.size();
        modCount += c.size();
        workList.clear();
        return true;
    }

    /**
     * Makes this list empty dropping all the elements.
     */
    @Override
    public void clear() {
        head = head.spawn();
        tail = head;
        size = 0;
        ++modCount;
    }
    
    /**
     * Returns another {@code LinkedArrayList} with the same degree and node
     * type, containing the same sequence of elements as this list.
     * 
     * @return another list with the same contents.
     */
    @Override
    public Object clone() {
        final int degree = head.getDegree();
        List<E> ret = new LinkedArrayList<>(degree, nodeType);
        List<E> tmp = new ArrayList<>(degree);

        for (LinkedArrayListNode<E> node = head;
                node != null;
                node = node.getNextNode()) {
            final int nodeSize = node.size();

            for (int i = 0; i < nodeSize; ++i) {
                tmp.add(node.get(i));

                if (tmp.size() == degree) {
                    ret.addAll(tmp);
                    tmp.clear();
                }
            }
        }

        // Add the leftovers.
        ret.addAll(tmp);
        return ret;
    }
    
    /**
     * Return {@code true} if this list contains {@code o}. 
     * 
     * @param  o the object to search.
     * @return {@code true} if {@code o} appears in this list. {@code false} 
     *         otherwise.
     */
    @Override
    public boolean contains(Object o) {
        for (LinkedArrayListNode<E> node = head;
                node != null;
                node = node.getNextNode()) {
            if (node.contains(o)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns {@code true} if this list contains <b>all</b> elements in 
     * {@code c}. 
     * 
     * @param  c the collection to check for inclusion.
     * @return {@code true} if this list contains all the elements in 
     *         {@code c}, and {@code false} otherwise.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Returns {@code true} if {@code o} is a {@link java.util.List}, it has the
     * same size as this list, and its element sequence is equal to the element
     * sequence of this list.
     * 
     * @param  o the object to test for equality.
     * @return {@code true} only if this list and {@code o} are list with the 
     *         same contents.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof List)) {
            return false;
        }
        
        if (o == this) {
            return true;
        }
        
        Iterator<E> thisIter = iterator();
        Iterator<E> argIter = ((List<E>) o).iterator();
        
        while (argIter.hasNext()) {
            if (!thisIter.hasNext()) {
                return false;
            }
            
            if (!Objects.equals(argIter.next(), thisIter.next())) {
                return false;
            }
        }
        
        return !thisIter.hasNext();
    }
    
    /**
     * Returns the element at index {@code index}.
     * 
     * @param  index the index of the desired element.
     * @return the element at index {@code index}.
     * @throws IndexOutOfBoundsException if {@code index} is invalid.
     */
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
    
    /**
     * Returns the hash code of this list. This routine was copied from 
     * {@link java.util.ArrayList#hashCode()} for compatibility.
     * 
     * @return the hash code of this list.
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
     
        for (E e : this) {
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
     
        return hashCode;
    }
    
    /**
     * Returns the index of the first occurrence of {@code o}, or -1 if this
     * list does not contain it.
     * 
     * @param  o the element whose index to look for.
     * @return the index of the first occurrence of the input element, or 
     *         {@code -1} if there is no such.
     */
    @Override
    public int indexOf(Object o) {
        int index = 0;
        
        for (LinkedArrayListNode<E> node = head; 
                node != null; 
                node = node.getNextNode()) {
            final int nodeSize = node.size();
            
            for (int i = 0; i < nodeSize; ++i, ++index) {
                if (Objects.equals(o, node.get(i))) {
                    return index;
                }
            }
        }
        
        return -1;
    }

    /**
     * Checks whether this list is empty.
     * 
     * @return {@code true} if this list does not contain any elements, and
     *         {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * Checks that this list maintains the invariants of 
     * {@code LinkedArrayList}. The first invariant is that there is no empty 
     * nodes in the chain of nodes of this list. The second invariant is that
     * there is no node that wastes space, or namely, every node has values
     * {@code null} at every storage array component that does not logically 
     * hold a value. The third invariant is that the sums of node sizes equals 
     * the value of {@code size} field of this list.
     * 
     * @throws IllegalStateException if this list is not healthy.
     */
    public void checkHealth() {
        if (head == tail) {
            // Only one node in this list. It is allowed to be empty.
            if (!head.isHealthy()) {
                throw new IllegalStateException(
                        "The only node in this list is unhealthy.");
            }
            
            return;
        }
        
        int s = 0;
        
        for (LinkedArrayListNode<E> node = head;
                node != null;
                node = node.getNextNode()) {
            if (!node.isHealthy()) {
                throw new IllegalStateException("Unhealthy node encountered.");
            }
            
            s += node.size();
        }
        
        if (size != s) {
            throw new IllegalStateException("Wrong accumulated size: " + 
                    s + "; list reports containing " + size + " elements.");
        }
    }

    /**
     * Returns an {@code java.util.Iterator} over this list.
     * 
     * @return an iterator.
     */
    @Override
    public Iterator<E> iterator() {
        return new BasicLinkedArrayListIterator();
    }

    /**
     * Returns the index of the last occurrence of {@code o} in this list, or
     * {@code -1} if this list does not contain it.
     * 
     * @param  o the element whose index to search. 
     * @return the index of the last occurrence of {@code o} in this list or
     *         {@code -1}, if this list does not contain {@code o}.
     */
    @Override
    public int lastIndexOf(Object o) {
        int index = size() - 1;
        
        for (LinkedArrayListNode<E> node = tail;
                node != null;
                node = node.getPreviousNode()) {
            final int nodeSize = node.size();
            
            for (int i = nodeSize - 1; i >= 0; --i, --index) {
                if (Objects.equals(o, node.get(i))) {
                    return index;
                }
            }
        }
        
        return -1;
    }

    /**
     * Returns a {@code java.util.ListIterator} over this list. The cursor of 
     * the result iterator is placed at the beginning of this list.
     * 
     * @return a {@code ListIterator} over this list.
     */
    @Override
    public ListIterator<E> listIterator() {
        return new AdvancedLinkedArrayListIterator(0);
    }

    /**
     * Returns a {@code java.util.ListIterator} over this list. The cursor of 
     * the result iterator is placed after {@code index} elements from beginning
     * of this list.
     * 
     * @param  index the initial location of {@code ListIterator}.
     * @return a {@code ListIterator}.
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new AdvancedLinkedArrayListIterator(index);
    }

    /**
     * Removes the element at index {@code index} and returns it.
     * 
     * @param  index the index of the element to remove.
     * @return the removed element.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
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
    
    /**
     * Removes the first occurrence of {@code o} from this list.
     * 
     * @param  o the element to remove.
     * @return {@code true} if this list contained {@code o}, and {@code false}
     *         otherwise.
     */
    @Override
    public boolean remove(Object o) {
        for (LinkedArrayListNode<E> node = head; 
                node != null; 
                node = node.getNextNode()) {
            
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
    
    /**
     * Removes all elements contained in {@code c} from this list.
     * 
     * @param  c the collection of elements to remove.
     * @return {@code true} only if this list changed after the call.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return bulkContainOperation(c, true);
    }

    /**
     * Retains only those elements in this list that are contained in {@code c}.
     * The elements in this list and not contained in {@code c} will be removed.
     * 
     * @param  c the collection of elements to retain.
     * @return {@code true} only if this list changed after the call.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return bulkContainOperation(c, false);
    }

    /**
     * Sets a new element {@code element} at index {@code index}.
     * 
     * @param index   the index of element to replace.
     * @param element the new value for the element location.
     * @return        the old value at the specified index.
     */
    @Override
    public E set(int index, E element) {
        checkIndexForAccess(index);
        searchElement(index);
        E ret = searchNode.get(searchLocalIndex);
        searchNode.set(searchLocalIndex, element);
        return ret;
    }
    
    /**
     * Returns the amount of elements in this list.
     * 
     * @return the size of this list.
     */
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Returns a view over a portion of this list.
     * 
     * @param  fromIndex the inclusive index of the first element in the view. 
     * @param  toIndex   the exclusive index of the last element in the view.
     * @return a view over this list.
     */
    @Override
    public SubList<E> subList(int fromIndex, int toIndex) {
        return new SubList(this, fromIndex, toIndex);
    }

    /**
     * Creates an {@code Object} array holding the elements of this list and 
     * returns it.
     * 
     * @return an array of elements.
     */
    @Override
    public Object[] toArray() {
        Object[] ret = new Object[size];
        int index = 0;
        
        for (LinkedArrayListNode<E> node = head; 
                node != null; 
                node = node.getNextNode()) {
            final int nodeSize = node.size();
            
            for (int i = 0; i < nodeSize; ++i, ++index) {
                ret[index] = node.get(i);
            }
        }
        
        return ret;
    }

    /**
     * Returns an array holding all the elements of this list. If {@code a} can
     * accommodate all the elements, it is loaded and returned. Otherwise,
     * the method creates an array large enough, loads it and returns it.
     * 
     * @param  <T> the actual element type.
     * @param  a   the input array.
     * @return     the array containing all the elements of this list.
     */
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
    
    /**
     * Returns a textual representation of this list.
     * 
     * @return a {@code String} representing this list.
     */
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
     * Removes this list all elements with indices between {@code fromIndex}
     * (inclusive) and {@code toIndex} (exclusive).
     * 
     * @param fromIndex the starting index of the range to remove.
     * @param toIndex   the ending index of the range to remove.
     */
    public void removeRange(int fromIndex, int toIndex) {
        int rangeLength = toIndex - fromIndex;
        int left = rangeLength;
        
        searchElement(fromIndex);
        
        int elementsToRemove = Math.min(rangeLength, 
                                        searchNode.size() - searchLocalIndex);
        searchNode.removeRange(searchLocalIndex,
                               searchLocalIndex + elementsToRemove);
        
        if (searchNode.isEmpty()) {
            unlinkNode(searchNode);
        }
        
        left -= elementsToRemove;
        LinkedArrayListNode<E> currentNode = searchNode.getNextNode();
        
        while (left > 0) {
            elementsToRemove = Math.min(left, currentNode.size());
            currentNode.removeRange(0, elementsToRemove);
            left -= elementsToRemove;
            
            if (currentNode.isEmpty()) {
                unlinkNode(currentNode);
                currentNode = currentNode.getNextNode();
            }
        }
        
        size -= rangeLength;
    }
    
    /**
     * Validates the access index.
     * 
     * @param index the index to validate.
     * 
     * @throws IndexOutOfBoundsException if the index is too small or too large.
     */
    private void checkIndexForAccess(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "The index is negative: " + index);
        }
        
        if (index >= size()) {
            throw new IndexOutOfBoundsException(
                    "The index is too large: " + index + ". " +
                    "The size of this list is " + size() + ".");
        }
    }
    
    /**
     * Validates the insertion index.
     * 
     * @param index the insertion index.
     * 
     * @throws IndexOutOfBoundsException if the index is too small or too large.
     */
    private void checkIndexForAddition(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "The index is negative: " + index);
        }
        
        if (index > size()) {
            throw new IndexOutOfBoundsException(
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
                node = node.getPreviousNode()) {
            for (int i = node.size() - 1; i >= 0; --i) {
                if (set.contains(node.get(i)) == mode) {
                    modified = true;
                    node.removeAt(i);
                    --size;
                    ++modCount;
                }
            }

            if (node.isEmpty()) {
                unlinkNode(node);
            }
        }
        
        return modified;
    }
    
    /**
     * Links {@code node} between {@code predecessor} and 
     * {@code predecessor.next}.
     * 
     * @param predecessor the predecessor node.
     * @param node        the node to link.
     */
    private void linkNode(LinkedArrayListNode<E> predecessor,
                          LinkedArrayListNode<E> node) {
        node.setPreviousNode(predecessor);
        node.setNextNode(predecessor.getNextNode());
        predecessor.setNextNode(node);

        if (node.getNextNode() != null) {
            node.getNextNode().setPreviousNode(node);
        } else {
            tail = node;
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
                node = node.getNextNode();
            }

            searchNode = node;
            searchLocalIndex = index;
        } else {
            // Access starting from the tail moving to the "left".
            LinkedArrayListNode<E> node = tail;
            index = size() - index - 1;
            
            while (index >= node.size()) {
                index -= node.size();
                node = node.getPreviousNode();
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
        if (node.getPreviousNode() == null) {
            if (node.getNextNode() == null) {
                // 'node' is the only node in this list. Do not remove.
            } else {
                // 'node' is the head node and is not the only node.
                head = node.getNextNode();
                head.setPreviousNode(null);
            }
        } else {
            // Here, 'node.getPreviousNode()' is not 'null'.
            if (node.getNextNode() == null) {
                // 'node' is the tail node and is not the only node.
                tail = node.getPreviousNode();
                tail.setNextNode(null);
            } else {
                // 'node' is neither head nor tail node.
                node.getPreviousNode().setNextNode(node.getNextNode());
                node.getNextNode().setPreviousNode(node.getPreviousNode());
            }
        }
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
            checkForConcurrentModification();
            
            if (iterated == maxElements) {
                throw new NoSuchElementException("The iterator exceeded.");
            }
            
            if (lastRemoved) {
                lastRemoved = false;
                ++iterated;
                
                if (localIndex == node.size()) {
                    localIndex = 0;
                    node = node.getNextNode();
                }
                
                // No need for incrementing 'localIndex'.
                return node.get(localIndex);
            }
            
            ++iterated;
            
            if (++localIndex == node.size()) {
                localIndex = 0;
                node = node.getNextNode();
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
            
            checkForConcurrentModification();
            lastRemoved = true;
            --size;
            
            node.removeAt(localIndex);
            
            if (node.isEmpty()) {
                LinkedArrayListNode<E> next = node.getNextNode();
                unlinkNode(node);
                node = next;
                localIndex = 0;
            }
        }
     
        private void checkForConcurrentModification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    /**
     * Implements the {@code #ListIterator} of this list.
     */
    private class AdvancedLinkedArrayListIterator implements ListIterator<E> {
        
        /**
         * Caches the modification count of the owner {@code LinkedArrayList}.
         */
        private long expectedModCount;
        
        /**
         * The global cursor in the entire list.
         */
        private int globalCursor;
        
        /**
         * The local cursor in particular node.
         */
        private int localCursor;
        
        /**
         * Caches the current node being iterated.
         */
        private LinkedArrayListNode<E> currentNode;
        
        /**
         * Caches the list node whose element was returned most recently by 
         * {@code next} or {@prev}.
         */
        private LinkedArrayListNode<E> lastIteratedNode;
        
        /**
         * Caches the index of element that was most recently returned by 
         * {@code next} and {@code prev}.
         */
        private int lastNodeIndex = -1;
        
        /**
         * Caches whether the last operation was {@link #previous()} or 
         * {@link #next()}.
         */
        private boolean lastOperationWasNextOrPrev;

        /**
         * Caches whether the last operation of {@link #next()} or 
         * {@link #previous} was {@code next()}.
         */
        private boolean lastOperationWasNext;
        
        AdvancedLinkedArrayListIterator() {
            this(0);
        }
        
        AdvancedLinkedArrayListIterator(int globalCursor) {
            this.globalCursor = globalCursor;
            this.expectedModCount = modCount;
            this.currentNode = head;
            
            if (globalCursor == size) {
                currentNode = tail;
                localCursor = currentNode.size();
                return;
            }
            
            for (int i = 0; i < globalCursor; ++i) {
                if (++localCursor == currentNode.size()) {
                    currentNode = currentNode.getNextNode();
                    localCursor = 0;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return globalCursor != size;
        }

        @Override
        public E next() {
            checkForConcurrentModification();
            
            if (globalCursor == size) {
                throw new NoSuchElementException("Forward iteration exceeded.");
            }
            
            if (localCursor == currentNode.size()) {
                currentNode = currentNode.getNextNode();
                localCursor = 0;
            }
            
            lastIteratedNode = currentNode;
            lastNodeIndex = localCursor;
            
            lastOperationWasNextOrPrev = true;
            lastOperationWasNext = true;
            ++globalCursor;
            return currentNode.get(localCursor++);
        }

        @Override
        public boolean hasPrevious() {
            return globalCursor != 0;
        }

        @Override
        public E previous() {
            checkForConcurrentModification();
            
            if (globalCursor-- == 0) {
                throw new NoSuchElementException(
                        "Backward iteration exceeded.");
            }
            
            if (localCursor == 0) {
                currentNode = currentNode.getPreviousNode();
                localCursor = currentNode.size();
            }
            
            lastOperationWasNextOrPrev = true;
            lastOperationWasNext = false;
            lastNodeIndex = --localCursor;
            lastIteratedNode = currentNode;
            return currentNode.get(localCursor);
        }

        @Override
        public int nextIndex() {
            return globalCursor;
        }

        @Override
        public int previousIndex() {
            return globalCursor - 1;
        }

        @Override
        public void add(E e) {
            if (isEmpty()) {
                // Special case: the list is empty.
                head.insert(0, e);
                globalCursor = 1;
                localCursor = 1;
                expectedModCount = ++modCount;
                ++size;
                return;
            }
            
            if (localCursor == currentNode.size()) {
                if (currentNode.getNextNode() == null) {
                    LinkedArrayListNode<E> newnode = head.spawn();
                    linkNode(currentNode, newnode);
                }
                
                localCursor = 0;
                currentNode = currentNode.getNextNode();
            }
            
            LinkedArrayListNode<E> newnode = currentNode.insert(localCursor, e);
            
            if (newnode != null) {
                linkNode(currentNode, newnode);
            } 
            
            size++;
            localCursor++;
            globalCursor++;
            expectedModCount = ++modCount;
        }

        @Override
        public void set(E e) {
            if (lastIteratedNode == null) {
                throw new IllegalStateException(
                        "No current element to set: the most recent " + 
                        "operation was not next() or prev(), or these " + 
                        "methods were not called at all.");
            }
            
            checkForConcurrentModification();
            lastIteratedNode.set(lastNodeIndex, e);
        }

        @Override
        public void remove() {
            if (lastIteratedNode == null) {
                throw new IllegalStateException(
                        "Removing an element second time, or no next() or " +
                        "prev() was not called for this list iterator.");
            }
            
            if (!lastOperationWasNextOrPrev) {
                throw new IllegalStateException(
                        "The last operation was not next() or prev().");
            }
            
            checkForConcurrentModification();
            lastIteratedNode.removeAt(lastNodeIndex);
            
            if (lastIteratedNode.isEmpty() 
                    && (head != lastIteratedNode || tail != lastIteratedNode)) {
                unlinkNode(lastIteratedNode);  
                
                currentNode = lastIteratedNode.getPreviousNode() != null ?
                              lastIteratedNode.getPreviousNode() : 
                              lastIteratedNode.getNextNode();
                
                localCursor = lastIteratedNode.getPreviousNode() == null ? 
                              0 : 
                              currentNode.size();
            }
            
            if (lastOperationWasNext) {
                --globalCursor;
                
                if (!lastIteratedNode.isEmpty()) {
                    --localCursor;
                }
            }
            
            expectedModCount = modCount;
            lastIteratedNode = null;
            --size;
        }
        
        /**
         * Checks for concurrent modification.
         * 
         * @throws ConcurrentModificationException if the parent list was
         *         modified while iterating over it.
         */
        private void checkForConcurrentModification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    /**
     * This class implements a view over a list's range.
     * 
     * @param <E> the element type.
     */
    class SubList<E> implements ExtendedList<E> {

        /**
         * The parent list.
         */
        private final ExtendedList<E> parent;
        
        /**
         * The amount of elements to skip from the left of {@code parent}.
         */
        private final int offset;
        
        /**
         * The size of this sublist.
         */
        private int size;
        
        /**
         * Caches the modification count of the parent list.
         */
        private int expectedModCount;
        
        SubList(ExtendedList<E> parent, int fromIndex, int toIndex) {
            sublistRangeCheck(fromIndex, toIndex, parent.size());
            this.expectedModCount = LinkedArrayList.this.modCount;
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
        }

        @Override
        public boolean add(E e) {
            parent.add(offset + size++, e);
            return true;
        }

        @Override
        public void add(int index, E element) {
            checkInsertionIndex(index);
            ++size;
            parent.add(index + offset, element);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            size += c.size();
            return parent.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            checkInsertionIndex(index);
            size += c.size();
            return parent.addAll(offset + index, c);
        }

        @Override
        public void clear() {
            parent.removeRange(offset, offset + size());
            size = 0;
        }

        @Override
        public boolean contains(Object o) {
            ListIterator<E> iterator = parent.listIterator(offset);
            
            for (int i = 0; i < size; ++i) {
                if (Objects.equals(o, iterator.next())) {
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
        public boolean equals(Object o) {
            if (!(o instanceof List)) {
                return false;
            }
            
            if (this == o) {
                return true;
            }
            
            List<E> list = (List<E>) o;
            
            if (size() != list.size()) {
                return false;
            }
            
            for (int i = 0; i < size(); ++i) {
                if (!get(i).equals(list.get(i))) {
                    return false;
                }
            }
            
            return true;
        }

        @Override
        public E get(int index) {
            checkAccessIndex(index);
            return parent.get(index + offset);
        }

        @Override
        public int indexOf(Object o) {
            Iterator<E> iterator = iterator();
            int index = 0;
            
            while (iterator.hasNext()) {
                if (Objects.equals(o, iterator.next())) {
                    return index;
                }
                
                ++index;
            }
            
            return -1;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public Iterator<E> iterator() {
            return new BasicSubListIterator(parent.listIterator(offset), size);
        }

        @Override
        public int lastIndexOf(Object o) {
            ListIterator<E> iterator = listIterator(size());
            int index = size() - 1;
            
            while (iterator.hasPrevious()) {
                if (Objects.equals(o, iterator.previous())) {
                    return index;
                }
                
                --index;
            }
            
            return -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return new AdvancedSubListIterator(
                    parent.listIterator(offset + index), size);
        }

        @Override
        public E remove(int index) {
            checkAccessIndex(index);
            --size;
            return parent.remove(index + offset);
        }

        @Override
        public boolean remove(Object o) {
            ListIterator<E> iterator = parent.listIterator(offset);
            
            for (int i = 0; i < size; ++i) {
                if (Objects.equals(iterator.next(), o)) {
                    parent.remove(i + offset);
                    --size;
                    return true;
                }
            }
            
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean stateModified = false;
            
            for (Object o : c) {
                if (remove((E) o)) {
                    stateModified = true;
                }
            }
            
            return stateModified;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean stateModified = false;
            Iterator<E> iterator = this.iterator();
            
            while (iterator.hasNext()) {
                if (!c.contains(iterator.next())) {
                    iterator.remove();
                    --size;
                    stateModified = true;
                }
            }
            
            return stateModified;
        }
        
        @Override
        public E set(int index, E element) {
            checkAccessIndex(index);
            return parent.set(index + offset, element);
        }
        
        @Override
        public int size() {
            return size;
        }

        @Override
        public ExtendedList<E> subList(int fromIndex, int toIndex) {
            return new SubList(this, fromIndex, toIndex);
        }

        @Override
        public Object[] toArray() {
            Object[] ret = new Object[size()];
            ListIterator<E> iterator = parent.listIterator(offset);
        
            for (int i = 0; i < size; ++i) {
                ret[i] = iterator.next();
            }
            
            return ret;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            ListIterator<E> iterator = parent.listIterator(offset);
            
            if (size() <= a.length) {
                for (int i = 0; i < size; ++i) {
                    a[i] = (T) iterator.next();
                }
                
                if (size() < a.length) {
                    a[size()] = null;
                }
                
                return a;
            }
            
            T[] ret = (T[]) Arrays.copyOf(a, size(), a.getClass());
            
            for (int i = 0; i < size; ++i) {
                ret[i] = (T) iterator.next();
            }
            
            return ret;
        }

        @Override
        public void removeRange(int fromIndex, int toIndex) {
            parent.removeRange(fromIndex + offset, toIndex + offset);
        }   
        
        /**
         * This class implements an {@link java.util.Iterator} over a sublist.
         */
        private class BasicSubListIterator implements Iterator<E> {

            /**
             * The actual {@code ListIterator}.
             */
            private final ListIterator<E> listIterator;
            
            /**
             * The amount of elements in the sublist.
             */
            private final int size;
            
            /**
             * The amount of elements iterated.
             */
            private int iterated = 0;
            
            /**
             * The expected modification count. Use for <b>fail-fast</b> upon
             * concurrent modification.
             */
            private int expectedModCount;
            
            /**
             * Indicates whether the last operation was {@code remove()}.
             */
            private boolean lastElementRemoved = false;
            
            BasicSubListIterator(ListIterator<E> listIterator, int size) {
                this.listIterator = listIterator;
                this.size = size;
                this.expectedModCount = LinkedArrayList.this.modCount;
            }
            
            @Override
            public boolean hasNext() {
                return iterated < size;
            }

            @Override
            public E next() {
                if (iterated++ == size) {
                    throw new NoSuchElementException("Iterator exceeded.");
                }
                
                lastElementRemoved = false;
                return listIterator.next();
            }
            
            @Override
            public void remove() {
                if (lastElementRemoved) {
                    throw new IllegalStateException(
                            "Removing an element twice.");
                }
                
                if (iterated == 0) {
                    throw new IllegalStateException(
                            "There is no current element.");
                }
                
//                checkForConcurrentModification();
                lastElementRemoved = true;
                listIterator.remove();
            }
            
            private void checkForConcurrentModification() {
                if (this.expectedModCount != LinkedArrayList.this.modCount) {
                    throw new ConcurrentModificationException();
                }
            }
        }
        
        private class AdvancedSubListIterator implements ListIterator<E> {

            /**
             * The actual {@code ListIterator} over parent list.
             */
            private final ListIterator<E> listIterator;
            
            /**
             * The size of this {@code SubList}.
             */
            private int size;
            
            /**
             * The cursor within this sublist.
             */
            private int cursor;
            
            /**
             * Indicates whether the last operation was {@link remove}.
             */
            private boolean lastOperationWasRemove;
            
            /**
             * Indicates whether the last operation was {@link next}.
             */
            private boolean lastOperationWasNext;
            
            private int expectedModCount;
            
            /**
             * Constructs a new list iterator over a sublist.
             * 
             * @param listIterator the actual list iterator over the parent 
             *                     list's range.
             * @param size        the length of the range. 
             */
            AdvancedSubListIterator(ListIterator<E> listIterator, int size) {
                this.listIterator = listIterator;
                this.size = size;
                this.expectedModCount = LinkedArrayList.this.modCount;
            }
            
            @Override
            public boolean hasNext() {
                return cursor != size;
            }

            @Override
            public E next() {
                checkForConcurrentModification();
                
                if (!hasNext()) {
                    throw new NoSuchElementException(
                        "There is no next element in this list iterator.");
                }
                
                ++cursor;
                return listIterator.next();
            }

            @Override
            public boolean hasPrevious() {
                return cursor != 0;
            }

            @Override
            public E previous() {
                checkForConcurrentModification();
                
                if (!hasPrevious()) {
                    throw new NoSuchElementException(
                        "There is no previous element in this list iterator.");
                }
                
                --cursor;
                return listIterator.previous();
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void add(E e) {
                expectedModCount = LinkedArrayList.this.modCount;
                listIterator.add(e);
            }

            @Override
            public void remove() {
                if (lastOperationWasRemove) {
                    throw new NoSuchElementException(
                            "There is no current element to remove.");
                }
                
                lastOperationWasRemove = true;
                --size;
                listIterator.remove();
                expectedModCount = LinkedArrayList.this.modCount;
            }

            @Override
            public void set(E e) {
                listIterator.set(e);
                expectedModCount = LinkedArrayList.this.modCount;
            }
            
            private void checkForConcurrentModification() {
                if (expectedModCount != LinkedArrayList.this.modCount) {
                    throw new ConcurrentModificationException();
                }
            }
        }
        
        /**
         * Checks that addition index is within bounds.
         * 
         * @param index the index to check. 
         * @throws IndexOutOfBounds if the index is invalid for addition.
         */
        private void checkInsertionIndex(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException(
                        "The insertion index is negative: " + index);
            }
            
            if (index > size) {
                throw new IndexOutOfBoundsException(
                        "The insertion index is too large: " + index + ", " +
                        "sublist size: " + size);
            }
        }
        
        /**
         * Checks that access index is within bounds.
         * 
         * @param index the index to check.
         * @throws IndexOutOfBounds if the index is invalid for access.
         */
        private void checkAccessIndex(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException(
                        "The access index is negative: " + index);
            }
            
            if (index >= size) {
                throw new IndexOutOfBoundsException(
                        "The access index is too large: " + index + ", " +
                        "sublist size: " + size);
            }
        }
        
        /**
         * Checks that the current modification count of this sublist equals
         * that of the parent list.
         */
        private void checkForConcurrentModification() {
            if (this.expectedModCount != LinkedArrayList.this.modCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        /**
         * Checks that the range indices are valid.
         * 
         * @param fromIndex the index of the first element within the desired
         *                  range.
         * @param toIndex  the index of the element one past the last element
         *                 of the desired range.
         * @param size    the size of the parent list.
         */
        private void sublistRangeCheck(int fromIndex, int toIndex, int size) {
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException(
                        "fromIndex is negative: " + fromIndex);
            }
            
            if (toIndex > size) {
                throw new IndexOutOfBoundsException(
                        "toIndex is too large: " + toIndex + ", size: " + size);
            }
            
            if (toIndex < fromIndex) {
                throw new IllegalArgumentException(
                    "toIndex(" + toIndex + ") < fromIndex(" + fromIndex + ")");
            }
        }
    }
}
