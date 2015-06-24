package net.coderodde.util.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This is a <b>trivial</b> node class. All operations are implemented as easily
 * as possible.
 * 
 * @author Rodion "rodde" Efremov
 * @param <E> the actual list element type.
 */
class LinkedArrayListNode1<E> extends LinkedArrayListNode<E> {

    /**
     * Constructs a new, empty <code>LinkedArrayListNode1</code> with given 
     * degree.
     * 
     * @param degree the degree of the new node.
     */
    LinkedArrayListNode1(int degree) {
        super(degree);
    }
    
    @Override
    protected LinkedArrayListNode<E> 
        addAll(int localIndex, 
               Collection<? extends E> collection, 
               List<E> workList) {
        final LinkedArrayListNode<E> nextNode = next;
        final int degree = getDegree();
        
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
                newnode.prev = chainTail;
                chainTail.next = newnode;
                chainTail = newnode;
            }
            
            chainTail.append(iterator.next());
        }
        
        iterator = workList.iterator();
        
        while (iterator.hasNext()) {
            if (chainTail.isFull()) {
                LinkedArrayListNode<E> newnode = spawn();
                newnode.prev = chainTail;
                chainTail.next = newnode;
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
        
        chainTail.next = nextNode;
        
        if (nextNode != null) {
            nextNode.prev = chainTail;
            return null;
        }
        
        return chainTail;
    }
    
    @Override
    protected void append(E element) {
        elementArray[size()] = element;
        ++size;
    }

    @Override
    protected boolean contains(Object o) {
        for (int i = 0; i < size(); ++i) {
            if (Objects.equals(o, elementArray[i])) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    protected E get(int index) {
        return (E) elementArray[index];
    }
    
    @Override
    protected int getDegree() {
        return elementArray.length;
    }

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

    @Override
    protected void removeAt(int index) {
        for (int i = index + 1; i < size(); ++i) {
            elementArray[i - 1] = elementArray[i];
        }
        
        elementArray[--size] = null;
    }

    @Override
    protected void set(int index, E element) {
        elementArray[index] = element;
    }

    @Override
    protected void shiftToBegining() {
        // This node type already aligns all the elements to the beginning of
        // the storage array.
    }
    
    @Override
    protected LinkedArrayListNode<E> spawn() {
        return new LinkedArrayListNode1<>(elementArray.length);
    }
    
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