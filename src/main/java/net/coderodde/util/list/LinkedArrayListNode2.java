package net.coderodde.util.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This class implements the advanced node for <code>LinkedArrayList</code>.
 * 
 * @author  Rodion "rodde" Efremov
 * @version 1.6
 */
class LinkedArrayListNode2<E> extends LinkedArrayListNode<E> {
    
    private final int mask;
    private int size;
    private int head;
    
    /**
     * Constructs a new, empty {@code LinkedArrayListNode2}.
     * 
     * @param degree the degree of the new node.
     */
    LinkedArrayListNode2(int degree) {
        this.elementArray = 
                new Object[fixDegree(
                        Math.max(degree, 
                                 LinkedArrayList.MINIMUM_DEGREE))];
        System.out.println("node degree: " + this.elementArray.length);
        this.mask = this.elementArray.length - 1;
    }

    @Override
    protected LinkedArrayListNode<E> addAll(int localIndex, 
                                            Collection<? extends E> collection, 
                                            List<E> workList) {
        LinkedArrayListNode<E> nextNode = getNextNode();
        
        // Collect everything that is to be moved in this node.
        for (int i = localIndex; i < size; ++i) {
            workList.add((E) elementArray[(head + i) & mask]);
        }
        
        size = localIndex;
        
        Iterator<? extends E> iterator = collection.iterator();
        LinkedArrayListNode<E> chainHead = this;
        LinkedArrayListNode<E> chainTail = this;
        
        while (iterator.hasNext()) {
            if (chainTail.isFull()) {
                LinkedArrayListNode<E> newnode = spawn();
                newnode.setPreviousNode(chainTail);
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
                chainTail = newnode;
            }
            
            chainTail.append(iterator.next());
        }
        
        // 'workList' is cleared in the calling method.
        
        if (chainHead == chainTail) {
            return null;
        }
        
        chainTail.setNextNode(nextNode);
        
        if (nextNode != null) {
            nextNode.setPreviousNode(chainTail);
            return null;
        }
        
        return chainTail;
    }

    @Override
    protected void append(E element) {
        elementArray[(head + size++) & mask] = element;
    }

    @Override
    protected boolean contains(Object o) {
        for (int i = 0; i < size; ++i) {
            if (Objects.equals(o, elementArray[(head + i) & mask])) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    protected E get(int index) {
        return (E) elementArray[(head + index) & mask];
    }

    @Override
    protected int getDegree() {
        return elementArray.length;
    }
    
    @Override
    protected LinkedArrayListNode<E> insert(int localIndex, E element) {
        if (isFull()) {
            LinkedArrayListNode<E> newnode = spawn();
            
            for (int i = localIndex; i < size; ++i) {
                int index = (head + i) & mask;
                newnode.append((E) elementArray[index]);
                elementArray[index] = null;
            }
            
            elementArray[localIndex] = element;
            size = localIndex + 1;
            return newnode;
        } 
        
        int leftComponents = localIndex;
        int rightComponents = size - localIndex;

        if (leftComponents < rightComponents) {
            // Move the components on the left one position to the left.
            for (int i = 0; i < leftComponents; ++i) {
                elementArray[(head + i - 1) & mask] = 
                elementArray[(head + i) & mask];
            }
        } else {
            // Move the components on the right one position to the right.
            for (int i = 0, j = size - 1; i < rightComponents; ++i, --j) {
                elementArray[(head + j) & mask] =
                elementArray[(head + j + 1) & mask];
            }
        }

        elementArray[(head + localIndex) & mask] = element;
        ++size;
        return null;
    }
    
    @Override
    protected boolean isHealthy() {
        if (size == 0) {
            // The empty nodes should not be kept around.
            return false;
        }
        
        int emptySlots = getDegree() - size();
        
        for (int i = (head + size) & mask; emptySlots > 0; --emptySlots) {
            if (elementArray[i] != null) {
                return false;
            }
            
            i = (i + 1) & mask;
        }
        
        return true;
    }

    @Override
    protected boolean isHealthyHead() {
        int emptySlots = getDegree() - size();
        
        for (int i = (head + size) & mask; emptySlots > 0; --emptySlots) {
            if (elementArray[i] != null) {
                return false;
            }
            
            i = (i + 1) & mask;
        }
        
        return true;
    }
    
    @Override
    protected boolean remove(Object o) {
        int localIndex = -1;
        
        for (int i = 0; i < size; ++i) {
            if (Objects.equals(o, elementArray[i])) {
                localIndex = i;
                break;
            }
        }
        
        if (localIndex == - 1) {
            return false;
        }
        
        int leftComponents = localIndex;
        int rightComponents = size - localIndex;
        
        if (leftComponents < rightComponents) {
            for (int i = localIndex - 1; i >= 0; --i) {
                elementArray[(head + i + 1) & mask] =
                        elementArray[(head + i) & mask];
            }
            
            elementArray[head & mask] = null;
        } else {
            for (int i = localIndex + 1; i < size; ++i) {
                elementArray[(head + i - 1) & mask] =
                        elementArray[(head + i) & mask];
            }
            
            elementArray[(head + size) & mask] = null;
        }
        
        --size;
        return true;
    }

    @Override
    protected E removeAt(int index) {
        E ret = (E) elementArray[(head + index) & mask];
        
        int leftComponents = index;
        int rightComponents = size - index;
        
        if (leftComponents < rightComponents) {
            for (int i = index - 1; i >= 0; --i) {
                elementArray[(head + i + 1) & mask] =
                        elementArray[(head + i) & mask];
            }
            
            elementArray[head & mask] = null;
        } else {
            for (int i = index + 1; i < size; ++i) {
                elementArray[(head + i - 1) & mask] =
                        elementArray[(head + i) & mask];
            }
            
            elementArray[(head + size) & mask] = null;
        }
        
        --size;
        return ret;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        int leftComponents = fromIndex;
        int rightComponents = size - toIndex;
        int rangeLength = toIndex - fromIndex;
        
        if (leftComponents < rightComponents) {
            for (int i = leftComponents - 1; i >= 0; --i) {
                elementArray[(head + rangeLength + i) & mask] = 
                        elementArray[(head + i) & mask];
            }
            
            for (int i = 0; i < rangeLength; ++i) {
                elementArray[(head + i) & mask] = null;
            }
        } else {
            for (int i = 0; i < rightComponents; ++i) {
                elementArray[(head + fromIndex + i) & mask] =
                        elementArray[(head + toIndex + i) & mask];
            }
            
            for (int i = 0; i < rangeLength; ++i) {
                elementArray[(head + toIndex + i) & mask] = null;
            }
        }
        
        size -= rangeLength;
    }

    @Override
    protected void set(int index, E element) {
        elementArray[(head + index) & mask] = element;
    }

    @Override
    protected LinkedArrayListNode<E> spawn() {
        return new LinkedArrayListNode2<>(super.elementArray.length);
    }
    
    @Override
    protected void split(int splitIndex, List<E> list) {
        for (int i = splitIndex; i < size; ++i) {
            list.add((E) elementArray[(head + i) & mask]);
        }
        
        size = splitIndex;
    }
    
    /**
     * Makes sure that the return value is a power of two no less than
     * {@code degree}.
     * 
     * @param degree the degree to fix.
     * @return a power of two.
     */
    private int fixDegree(int degree) {
        int ret = Integer.highestOneBit(degree);
        return ret != degree ? ret << 1 : ret;
    }
}
