package net.coderodde.util.list;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinkedArrayListNode1Test {
    
    private LinkedArrayListNode1<Integer> node;
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAddAllThrowsOnOverflow() {
        node = newNode(5);
        
        for (int i = 0; i < 6; ++i) {
            node.append(i);
        }
        
        assertTrue(node.isHealthy());
    }
    
    @Test
    public void testAddAll() {
        node = newNode(5);
        
        for (int i = 0; i < 5; ++i) {
            node.append(i);
        }
        
        assertTrue(node.isHealthy());
    }

    @Test
    public void testAppend() {
        node = new LinkedArrayListNode1<>(5);
    
        for (int i = 0; i < 5; ++i) {
            assertEquals(i, node.size());
            node.append(5 + i);
            assertEquals(i + 1, node.size());
        }
        
        for (int i = 0; i < 5; ++i) {
            assertEquals(Integer.valueOf(5 + i), node.get(i));
        }
    }

    @Test
    public void testContains() {
        node = new LinkedArrayListNode1<>(7);
        
        for (int i = 0; i < 6; ++i) {
            node.append(i + 5);
        }
        
        for (int i = 0; i < 5; ++i) {
            assertTrue(node.contains(i + 5));
        }
        
        assertTrue(node.contains(10));
        
        for (int i = 4; i > -10; --i) {
            assertFalse(node.contains(i));
        }
        
        for (int i = 11; i < 16; ++i) {
            assertFalse(node.contains(i));
        }
    }

    @Test
    public void testGet() {
        node = new LinkedArrayListNode1<>(10);
        
        for (int i = 0; i < 10; ++i) {
            node.append(i + 1);
        }
        
        for (int i = 0; i < 10; ++i) {
            assertEquals(Integer.valueOf(i + 1), node.get(i));
        }
    }
    
    @Test
    public void testGetThrowsOnTooSmallIndex() {
        node = new LinkedArrayListNode1<>(5);
        
        for (int i = 0; i < 4; ++i) {
            node.append(i + 1);
        }
        
        for (int i = 0; i < 4; ++i) {
            assertEquals(Integer.valueOf(i + 1), node.get(i));
        }
    }
    
    @Test
    public void testGetDegree() {
        for (int d = 2; d < 10; ++d) {
            assertEquals(d, new LinkedArrayListNode1<Integer>(d).getDegree());
        }
    }

    @Test
    public void testInsert() {
        node = new LinkedArrayListNode1<>(8);
        
        for (int i = 0; i < 5; ++i) {
            node.append(i);
        }
        
        node.insert(0, 10);
        
        assertEquals(Integer.valueOf(10), node.get(0));
        
        for (int i = 0; i < 5; ++i) {
            assertEquals(Integer.valueOf(i), node.get(i + 1));
        }
        
        node.insert(node.size(), 1000);
        
        assertEquals(Integer.valueOf(1000), node.get(6));
        
        node.insert(3, Integer.valueOf(135));
        
        assertEquals(Integer.valueOf(135), node.get(3));
        
        LinkedArrayListNode<Integer> newnode = node.insert(4, 1000);
    
        assertEquals(5, node.size());
        assertEquals(4, newnode.size());
        
        node = new LinkedArrayListNode1<>(7);
        
        for (int i = 0; i < 7; ++i) {
            node.append(i);
        }
        
        newnode = node.insert(6, 100);
        
//        assertEquals(3, node.size());
//        assertEquals(6, newnode.size());
    }

    @Test
    public void testIsHealthy() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testRemoveAt() {
    }

    @Test
    public void testSet() {
    }

    @Test
    public void testShiftToBegining() {
    }

    @Test
    public void testSpawn() {
    }

    @Test
    public void testSplit() {
    }
    
    private LinkedArrayListNode1<Integer> newNode(int degree) {
        return new LinkedArrayListNode1<>(degree);
    }
}
