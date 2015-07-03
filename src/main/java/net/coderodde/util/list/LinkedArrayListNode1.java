package net.coderodde.util.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This is a <b>trivial</b> node class. All operations are implemented as easily
 * as possible.
 * 
 * @author    Rodion "rodde" Efremov
 * @version   1.6
 * @param <E> the actual list element type.
 */
class LinkedArrayListNode1<E> extends LinkedArrayListNode<E> {

    /**
     * Constructs a new, empty {@code LinkedArrayListNode1} with given degree.
     * 
     * @param degree the degree of the new node.
     */
    LinkedArrayListNode1(int degree) {
        super(degree);
    }
    
    /**
     * Inserts elements in {@code collection} (in the order they are returned by 
     * the iterator of {@code collection}) between elements at indices 
     * {@code localIndex - 1} and {@code localIndex}. If the collection does not
     * fit in this node, it creates a chain of nodes appending the rest of 
     * elements to the nodes of the chain.
     * 
     * @param localIndex the index of element before which to insert.
     * @param collection the collection of elements to insert.
     * @param workList   the auxiliary list for holding the elements in this
     *                   node that are to be moved in order to make room for 
     *                   inserted elements.
     * @return           if all the elements fit in this node, {@code null} is
     *                   returned. Otherwise, the rightmost node is returned.
     */
    @Override
    protected LinkedArrayListNode<E> 
        addAll(int localIndex, 
               Collection<? extends E> collection, 
               List<E> workList) {
        final LinkedArrayListNode<E> nextNode = getNextNode();
        
        // Collect everything that is to be moved in this node.
        for (int i = localIndex; i < size; ++i) {
            workList.add((E) elementArray[i]);
        }
        
        // Update the size field.
        size = localIndex;
            
        Iterator<? extends E> iterator = collection.iterator();
        LinkedArrayListNode<E> chainHead = this;
        LinkedArrayListNode<E> chainTail = this;
        
        while (iterator.hasNext()) {
            if (chainTail.isFull()) {
                LinkedArrayListNode<E> newnode = spawn();
                newnode.setPreviousNode(chainTail);
//                newnode.prev = chainTail;
//                chainTail.next = newnode;
                chainTail.setNextNode(newnode);
                chainTail = newnode;
            }
            
            chainTail.append(iterator.next());
        }
        
        iterator = workList.iterator();
        
        while (iterator.hasNext()) {
            if (chainTail.isFull()) {
                LinkedArrayListNode<E> newnode = spawn();
                newnode.setPreviousNode(chainTail);
                chainTail.setNextNode(newnode);
//                newnode.prev = chainTail;
//                chainTail.next = newnode;
                chainTail = newnode;
            }
            
            chainTail.append(iterator.next());
        }
        
        // 'workList' is cleared in the calling method.
        
        if (chainHead == chainTail) {
            // The added elements fit entirely in this node. No relinking,
            // return null.
            return null;
        }
        
//        chainTail.next = nextNode;
        chainTail.setNextNode(nextNode);
        
        if (nextNode != null) {
//            nextNode.prev = chainTail;
            nextNode.setPreviousNode(chainTail);
            return null;
        }
        
        return chainTail;
    }
    
    /**
     * Appends {@code element} to the tail of this node.
     * 
     * @param element the element to add.
     */
    @Override
    protected void append(E element) {
        elementArray[size()] = element;
        ++size;
    }

    /**
     * Returns {@code true} if this node contains {@code o}.
     * 
     * @param  o the object to search for.
     * @return {@code true} if {@code o} is included in this node, {@code false}
     *         otherwise.
     */
    @Override
    protected boolean contains(Object o) {
        for (int i = 0; i < size(); ++i) {
            if (Objects.equals(o, elementArray[i])) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns the element whose (local) index is {@code index}.
     * 
     * @param  index the index of the element to return. 0 is for the head
     *         element, 1 is for second, and so on.
     * @return the element at given index.
     */
    @Override
    protected E get(int index) {
        return (E) elementArray[index];
    }
    
    /**
     * Returns the degree of this node, which is essentially the capacity of
     * underlying array.
     * 
     * @return the degree of this node.
     */
    @Override
    protected int getDegree() {
        return elementArray.length;
    }

    /**
     * Inserts {@code element} in this node between elements with indices
     * {@code localIndex - 1} and {@code localIndex}. If this node is full, a 
     * new node is created and all the elements in this node starting at index
     * {@code localIndex} are moved to the new node and {@code element} is
     * inserted (or at that stage, appended) in this node.
     * 
     * @param localIndex the index of the element in front of which to insert.
     * @param element    the element to insert.
     * @return           if {@code element} fits in this node, {@code null} is
     *                   returned. Otherwise if this node is full prior to call
     *                   to this routine, the new spawned node is returned so
     *                   that the owner list can link it to its node chain.
     */
    @Override
    protected LinkedArrayListNode<E> insert(int localIndex, E element) {
        if (!isFull()) {
            for (int i = size; i > localIndex; --i) {
                elementArray[i] = elementArray[i - 1];
            }

            elementArray[localIndex] = element;
            ++size;
            return null;
        } else {
            LinkedArrayListNode<E> newnode = spawn();
            
            for (int i = localIndex; i < size; ++i) {
                newnode.append((E) elementArray[i]);
                elementArray[i] = null;
            }
            
            elementArray[localIndex] = element;
            size = localIndex + 1;
            return newnode;
        }
    }
    
    /**
     * Checks whether this node obeys the <b>node invariant</b>. The node 
     * invariant is that this node may not be empty, as the owner list is not
     * supposed to keep empty nodes. Also, all those array components that do 
     * not logically store an element should hold the {@code null} value (we 
     * want make sure that garbage collector can do its job).
     * 
     * @return {@code true} only if this node obeys the node invariant.
     */
    @Override
    protected boolean isHealthy() {
        if (size == 0) {
            // The empty nodes should not be kept around.
            return false;
        }
        
        final int degree = getDegree();
        
        for (int i = size; i < degree; ++i) {
            if (elementArray[i] != null) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks whether this node obeys the <b>head node invariant</b>. Basically,
     * this routine makes the same check as {@link #isHealthy()}, but it allows
     * the node to be empty. This is done this way so that we consider an empty
     * list, holding only one (also empty) node, to be healthy.
     * 
     * @return {@code true} if this head node obeys the head node invariant.
     */
    protected boolean isHealthyHead() {
        final int degree = getDegree();
        
        for (int i = size; i < degree; ++i) {
            if (elementArray[i] != null) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Removes the leftmost occurrence of {@code o} from this node, only if it 
     * is in this node.
     * 
     * @param  o the object to remove.
     * @return {@code true} if this node contained the requested object and,
     *         thus, the state of this node changed.
     */
    @Override
    protected boolean remove(Object o) {
        for (int i = 0; i < size(); ++i) {
            if (Objects.equals(o, elementArray[i])) {
                for (int j = i + 1; j < size(); ++j) {
                    elementArray[j - 1] = elementArray[j];
                }
                
                --size;
                elementArray[size] = null;
                return true;
            }
        }
        
        return false;
    }

    /**
     * Removes the element from this node whose local index is {@code index}
     * @param index 
     */
    @Override
    protected void removeAt(int index) {
        for (int i = index + 1; i < size(); ++i) {
            elementArray[i - 1] = elementArray[i];
        }
        
        elementArray[--size] = null;
    }

    /**
     * Sets the new value for element at local index {@code index}.
     * 
     * @param index   the local index of the element to set.
     * @param element the new value to set.
     */
    @Override
    protected void set(int index, E element) {
        elementArray[index] = element;
    }

    /**
     * Shifts all the elements in this node to the left such, that the head
     * element is at index 0, the second at index 1, and so on.
     */
    @Override
    protected void shiftToBegining() {
        // This node type already aligns all the elements to the beginning of
        // the storage array.
    }
    
    /**
     * Creates and returns an empty node of the same type as this node.
     * 
     * @return a new node.
     */
    @Override
    protected LinkedArrayListNode<E> spawn() {
        return new LinkedArrayListNode1<>(getDegree());
    }
    
    /**
     * Splits this node starting from element at index {@code splitIndex} and 
     * dumps all the elements on the right to the list {@code list}.
     * 
     * @param splitIndex the split index.
     * @param list       the list to accommodate the rightmost elements.
     */
    @Override
    protected void split(int splitIndex, List<E> list) {
        final int nodeSize = size;
        
        for (int i = splitIndex; i < nodeSize; ++i) {
            list.add(get(i));
            set(i, null);
        }
        
        size = splitIndex;
    }
}