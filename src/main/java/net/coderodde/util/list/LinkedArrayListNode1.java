package net.coderodde.util.list;

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
    protected LinkedArrayListNode<E> spawn() {
        return new LinkedArrayListNode1<>(elementArray.length);
    }
}