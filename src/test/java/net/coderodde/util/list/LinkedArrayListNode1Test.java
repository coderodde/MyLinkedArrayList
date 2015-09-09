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
        node = new LinkedArrayListNode1<>(9);
        
        for (int i = 0; i < 9; ++i) {
            node.append(i);
        }
    
        LinkedArrayListNode<Integer> next = node.insert(4, 1000);
        
        assertEquals(4, node.size());
        assertEquals(6, next.size());
        
        for (int i = 0; i < node.size(); ++i) {
            assertEquals(Integer.valueOf(i), node.get(i));
        }
        
        assertEquals(Integer.valueOf(1000), next.get(0));
        
        for (int i = 1; i < next.size(); ++i) {
            assertEquals(Integer.valueOf(i + node.size() - 1), next.get(i));
        }
        
        node = new LinkedArrayListNode1<>(9);
        
        for (int i = 0; i < 9; ++i) {
            node.append(i);
        }
        
        next = node.insert(3, 1000);
        
        assertEquals(5, node.size());
        assertEquals(5, next.size());
        
        for (int i = 0; i < 3; ++i) {
            assertEquals(Integer.valueOf(i), node.get(i));
        }
        
        assertEquals(Integer.valueOf(1000), node.get(3));
        assertEquals(Integer.valueOf(3), node.get(4));
        
        for (int i = 0; i < next.size(); ++i) {
            assertEquals(Integer.valueOf(4 + i), next.get(i));
        }
        
        node =  new LinkedArrayListNode1<>(3);
        
        node.insert(0, 0);
        node.insert(1, 1);
        
        assertEquals(2, node.size());
        
        node.insert(1, 2);
        
        assertEquals(3, node.size());
        
        node = new LinkedArrayListNode1<>(9);
        
        for (int i = 0; i < 9; ++i) {
            assertEquals(i, node.size());
            node.insert(node.size() / 2, i);
            assertEquals(i + 1, node.size());
        }
    }

    @Test
    public void testIsHealthy() {
        node = newNode(4);
        
        assertFalse(node.isHealthy());
        assertTrue(node.isHealthyHead());
        
        node.insert(0, 10);
        
        assertTrue(node.isHealthy());
        assertTrue(node.isHealthyHead());
    }

    @Test
    public void testRemove() {
        node = newNode(5);
        
        for (int i = 0; i < 4; ++i) {
            assertEquals(i, node.size());
            node.append(i);
            assertEquals(i + 1, node.size());
        }
        
        Integer t = Integer.valueOf(2);
        
        assertTrue(node.remove(t));
        assertFalse(node.remove(t));
        
        assertEquals(3, node.size());
        
        t = Integer.valueOf(3);
        
        assertTrue(node.remove(t));
        assertFalse(node.remove(t));
        
        assertEquals(2, node.size());
        
        t = Integer.valueOf(1);
        
        assertTrue(node.remove(t));
        assertFalse(node.remove(t));
        
        assertEquals(1, node.size());
        
        t = Integer.valueOf(0);
        
        assertTrue(node.remove(t));
        assertFalse(node.remove(t));
        
        assertEquals(0, node.size());
    }

    @Test
    public void testRemoveAt() {
        node = newNode(5);
        
        for (int i = 0; i < 5; ++i) {
            node.append(i);
        }
        
        assertEquals(Integer.valueOf(2), node.removeAt(2));
        assertEquals(4, node.size());
        
        assertEquals(Integer.valueOf(3), node.removeAt(2));
        assertEquals(3, node.size());
        
        assertEquals(Integer.valueOf(0), node.removeAt(0));
        assertEquals(2, node.size());
        
        assertEquals(Integer.valueOf(1), node.removeAt(0));
        assertEquals(1, node.size());
        
        assertEquals(Integer.valueOf(4), node.removeAt(0));
        assertEquals(0, node.size());
    }

    @Test
    public void testSet() {
        node = newNode(4);
        
        node.append(0);
        node.append(1);
        
        assertEquals(2, node.size());
        assertEquals(Integer.valueOf(0), node.get(0));
        assertEquals(Integer.valueOf(1), node.get(1));
        
        node.set(0, 10);
        node.set(1, 11);
        
        assertEquals(2, node.size());
        assertEquals(Integer.valueOf(10), node.get(0));
        assertEquals(Integer.valueOf(11), node.get(1));
    }

    @Test
    public void testSpawn() {
        node = newNode(5);
        LinkedArrayListNode<Integer> next = node.spawn();
        
        assertEquals(5, next.getDegree());
        assertTrue(next instanceof LinkedArrayListNode1);
    }

    @Test
    public void testSplit() {
    }
    
    private LinkedArrayListNode1<Integer> newNode(int degree) {
        return new LinkedArrayListNode1<>(degree);
    }
}
