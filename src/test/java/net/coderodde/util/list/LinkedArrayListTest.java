package net.coderodde.util.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class LinkedArrayListTest {
    
    private LinkedArrayList<Integer> list = 
            new LinkedArrayList<>(3, LinkedArrayList.NodeType.TRIVIAL);
    
    private List<Integer> test = new ArrayList<>();
    
    @Before
    public void before() {
        list.clear();
        test.clear();
    }
    
    @Test
    public void testSize() {
        assertTrue(list.isEmpty());
        
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        assertEquals(10, list.size());
        assertFalse(list.isEmpty());
        eq();
    }

    @Test
    public void testIsEmpty() {
        assertTrue(list.isEmpty());
        
        list.add(1);
        
        assertFalse(list.isEmpty());
        
        list.add(9);
        
        assertFalse(list.isEmpty());
        
        list.remove(new Integer(9));
        
        assertFalse(list.isEmpty());
        
        list.remove(new Integer(3));
        
        assertFalse(list.isEmpty());
        
        list.remove(new Integer(1));
        
        assertTrue(list.isEmpty());
    }

    @Test
    public void testContains() {
        list.add(1);
        list.add(9);
        list.add(9);
        list.add(3);
        list.add(7);
        
        assertEquals(5, list.size());
        assertFalse(list.isEmpty());
        
        assertTrue(list.contains(1));
        assertTrue(list.contains(3));
        assertTrue(list.contains(7));
        assertTrue(list.contains(9));
        
        assertFalse(list.contains(0));
        assertFalse(list.contains(2));
        assertFalse(list.contains(4));
        assertFalse(list.contains(5));
        assertFalse(list.contains(6));
        assertFalse(list.contains(8));
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsOnRemovalFromEmptyList() {
        list.iterator().remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testThrowsOnRemovalWithoutValidIteratorPointer() {
        list.add(0);
        list.add(1);
        list.iterator().remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testThrowsOnRemovingTheSameElementTwice() {
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        
        Iterator<Integer> iterator = list.iterator();
        
        iterator.next(); // Now iterator points to '0'.
        iterator.next(); // Now iterator points to '1'.
        
        iterator.remove(); // Remove '1'
        System.out.println("Removed the element for the first time!");
        iterator.remove(); // This must throw.
    }
    
    @Test
    public void testIterator() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Iterator itList = list.iterator();
        Iterator itTest = test.iterator();
        
        itList.next(); // '0'
        itList.next(); // '1'
        itList.next(); // '2'
        itList.next(); // '3'
        itList.remove(); 
        itList.next(); // '4'
        itList.remove(); 
        itList.next(); // '5'
        itList.remove();
        itList.next(); // '6'
        itList.next(); // '7'
        itList.remove();
        itList.next(); // '8'
        itList.next(); // '9'
        itList.remove();
        
        itTest.next(); // '0'
        itTest.next(); // '1'
        itTest.next(); // '2'
        itTest.next(); // '3'
        itTest.remove(); 
        itTest.next(); // '4'
        itTest.remove(); 
        itTest.next(); // '5'
        itTest.remove();
        itTest.next(); // '6'
        itTest.next(); // '7'
        itTest.remove();
        itTest.next(); // '8'
        itTest.next(); // '9'
        itTest.remove();
        
        eq();
    }
    
    @Test
    public void testIteratorBruteForce() {
        final long mySeed = 1434561131835L;
        final long seed = mySeed != 0L ? mySeed : System.currentTimeMillis();
        System.out.println("Seed: " + seed);
        Random random = new Random(seed);
        
        for (int i = 0; i < 1000; ++i) {
            // Get random size and degree.
            int size = random.nextInt(100) + 10;
            int degree = 2 + random.nextInt(5);
            
            // Create the lists.
            LinkedArrayList<Integer> list = 
                    new LinkedArrayList<>(degree, 
                                          LinkedArrayList.NodeType.TRIVIAL);
            ArrayList<Integer> test = new ArrayList<>();
            
            // Populate the lists.
            for (int j = 0; j < size; ++j) {
                Integer element = random.nextInt(100) - 50;
                
                list.add(element);
                test.add(element);
            }
            
            // Get the iterators.
            Iterator<Integer> listIt = list.iterator();
            Iterator<Integer> testIt = test.iterator();
            float removeFactor = random.nextFloat();
            
            int k = 0;
            
            while (testIt.hasNext()) {
                Integer listInt = listIt.next();
                Integer testInt = testIt.next();
                
                assertTrue(Objects.equals(listInt, testInt));
                
                if (random.nextFloat() < removeFactor) {
                    listIt.remove();
                    testIt.remove();
                }
                
                k++;
            }
            
            assertFalse(listIt.hasNext());
            
            eq(list, test);
        }
    }

    @Test
    public void testToArray_0args() {
    }

    @Test
    public void testToArray_GenericType() {
    }

    @Test
    public void testAdd_GenericType() {
    }

    @Test
    public void testRemove_Object() {
    }

    @Test
    public void testContainsAll() {
    }

    @Test
    public void testAddAll_Collection() {
    }

    @Test
    public void testAddAll_int_Collection() {
    }

    @Test
    public void testRemoveAll() {
    }

    @Test
    public void testRetainAll() {
    }

    @Test
    public void testClear() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void testSet() {
    }

    @Test
    public void testAdd_int_GenericType() {
    }

    @Test
    public void testRemove_int() {
    }

    @Test
    public void testIndexOf() {
    }

    @Test
    public void testLastIndexOf() {
    }

    @Test
    public void testListIterator_0args() {
    }

    @Test
    public void testListIterator_int() {
    }

    @Test
    public void testSubList() {
    }
    
    private void eq() {
        assertEquals(test.size(), list.size());
        
        for (int i = 0; i < list.size(); ++i) {
            assertTrue(Objects.equals(list.get(i), test.get(i)));
        }
        
        Iterator<Integer> itList = list.iterator();
        Iterator<Integer> itTest = test.iterator();
        
        while (itTest.hasNext()) {
            assertTrue(itList.hasNext());
            assertTrue(Objects.equals(itTest.next(), itList.next()));
        }
        
        assertFalse(itList.hasNext());
    }
    
    private void eq(LinkedArrayList<Integer> test, ArrayList<Integer> list) {
        assertEquals(test.size(), list.size());
        
        for (int i = 0; i < list.size(); ++i) {
            assertTrue(Objects.equals(list.get(i), test.get(i)));
        }
        
        Iterator<Integer> itList = list.iterator();
        Iterator<Integer> itTest = test.iterator();
        
        while (itTest.hasNext()) {
            assertTrue(itList.hasNext());
            assertTrue(Objects.equals(itTest.next(), itList.next()));
        }
        
        assertFalse(itList.hasNext());
    }
}
