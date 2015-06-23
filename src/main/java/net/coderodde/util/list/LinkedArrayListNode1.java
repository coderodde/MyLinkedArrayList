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
    protected LinkedArrayListNode[] 
        addAll(int localIndex, 
               Collection<? extends E> collection, 
               List<E> workList) {
        final int freeComponentAmount = getDegree() - size();
        final int collectionSize = collection.size();
        
        if (collection.size() <= freeComponentAmount) {
            // Once here, 'collection' fits in this node.
            for (int i = size() - 1; i >= localIndex; --i) {
                elementArray[i] = elementArray[i + collectionSize];
            }
            
            for (E element : collection) {
                elementArray[localIndex++] = element;
            }
            
            size += collectionSize;
            return null;
        }
        
        // Once here, 'collection' does NOT fit in this node.
        final int degree = getDegree();
        Iterator<? extends E> iterator = collection.iterator();
        
        for (int i = localIndex; i < degree; ++i) {
            workList.add((E) elementArray[i]);
            elementArray[i] = iterator.next();
        }
        
        LinkedArrayListNode<E> insertHead = spawn();
        LinkedArrayListNode<E> insertTail = insertHead;
        
        while (iterator.hasNext()) {
            insertTail.append(iterator.next());
            
            if (insertTail.isFull()) {
                LinkedArrayListNode<E> newnode = spawn();
                insertTail.next = newnode;
                newnode.prev = insertTail;
                insertTail = newnode;
            }
        }
        
        // Now, add only the elements from 'workList'.
        for (E element : workList) {
            insertTail.append(element);
            
            if (insertTail.isFull()) {
                LinkedArrayListNode<E> newnode = spawn();
                insertTail.next = newnode;
                newnode.prev = insertTail;
                insertTail = newnode;
            }
        }
        
        workList.clear();
        return new LinkedArrayListNode[]{ insertHead, insertTail };
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