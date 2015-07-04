package net.coderodde.util.list;

import java.util.Collection;
import java.util.List;

/**
 * This class implements the advanced node for <code>LinkedArrayList</code>.
 * 
 * @author  Rodion "rodde" Efremov
 * @version 1.6
 */
class LinkedArrayListNode2<E> extends LinkedArrayListNode<E> {
    
    /**
     * Constructs a new, empty {@code LinkedArrayListNode2}.
     * 
     * @param degree the degree of the new node.
     */
    LinkedArrayListNode2(int degree) {
        super(degree);
    }

    @Override
    protected LinkedArrayListNode<E> addAll(int index, Collection<? extends E> collection, List<E> workList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void append(E element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected E get(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected LinkedArrayListNode<E> insert(int localIndex, E element) {
        return null;
    }
    
    @Override
    protected boolean isHealthy() {
        return false;
    }
    
    @Override
    protected boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeAt(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        
    }

    @Override
    protected void set(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void shiftToBegining() {
    
    }

    @Override
    protected LinkedArrayListNode<E> spawn() {
        return new LinkedArrayListNode2<>(super.elementArray.length);
    }
    
    @Override
    protected void split(int splitIndex, List<E> list) {
        
    }
}
