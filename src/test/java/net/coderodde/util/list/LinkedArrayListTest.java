package net.coderodde.util.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
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
    public void testIteratorThrowsOnRemovalFromEmptyList() {
        list.iterator().remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testIteratorThrowsOnRemovalWithoutValidIteratorPointer() {
        list.add(0);
        list.add(1);
        list.iterator().remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testIteratorThrowsOnRemovingTheSameElementTwice() {
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        
        Iterator<Integer> iterator = list.iterator();
        
        iterator.next(); // Now iterator points to '0'.
        iterator.next(); // Now iterator points to '1'.
        
        iterator.remove(); // Remove '1'
        System.out.println("Removed the element for the first time! If you " +
                           "see this message, everything up till now is o.k.");
        iterator.remove(); // This must throw.
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsOnConcurrentModification_next() {
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        
        Iterator<Integer> iter = list.iterator();
        
        iter.next();
        
        list.add(4);
        
        iter.next();
        
        fail("Iterator should have detected the modification!");
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorThrowsOnConcurrentModification_remove() {
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        
        Iterator<Integer> iter = list.iterator();
        
        iter.next();
        
        list.add(4);
        
        iter.remove();
        
        fail("Iterator should have detected the modification!");
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
        final long mySeed = 1434648057381L;
        final long seed = mySeed != 0L ? mySeed : System.currentTimeMillis();
        System.out.println("testIteratorBruteForce: seed = " + seed);
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
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        Object[] arr = list.toArray();
        assertEquals(20, arr.length);
        
        for (int i = 0; i < 20; ++i) {
            Object o = arr[i];
            assertEquals(i, o);
        }
    }

    @Test
    public void testToArray_GenericType() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        Integer[] smallArray = new Integer[10];
        Integer[] retArray = list.toArray(smallArray);
        
        assertTrue(smallArray != retArray);
        assertEquals(list.size(), retArray.length);
        
        for (int i = 0; i < list.size(); ++i) {
            assertEquals(new Integer(i), list.get(i));
            assertEquals(new Integer(i), retArray[i]);
        }
        
        Integer[] retArray2 = list.toArray(retArray);
        assertTrue(retArray2 == retArray);
        
        Integer[] largeArray = new Integer[list.size() + 5];
        retArray = list.toArray(largeArray);
        
        assertTrue(retArray == largeArray);
        
        for (int i = 0; i < list.size(); ++i) {
            assertEquals(new Integer(i), largeArray[i]);
        }
        
        assertNull(largeArray[list.size()]);
    }

    @Test
    public void testAdd_GenericType() {
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        
        list.add(10);
        
        assertEquals(1, list.size());
        
        list.add(11);
        
        assertEquals(2, list.size());
        
        list.add(12);
        
        assertEquals(3, list.size());
        
        list.add(13);
        
        assertEquals(4, list.size());
        assertFalse(list.isEmpty());
        
        assertEquals(new Integer(10), list.get(0));
        assertEquals(new Integer(11), list.get(1));
        assertEquals(new Integer(12), list.get(2));
        assertEquals(new Integer(13), list.get(3));
    }

    @Test
    public void testRemove_Object() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        eq();
        
        list.remove(new Integer(3));
        test.remove(new Integer(3));
        
        list.remove(new Integer(3));
        test.remove(new Integer(3));
        
        list.remove(new Integer(3));
        test.remove(new Integer(3));
        
        eq();
        
        list.remove(new Integer(7));
        test.remove(new Integer(7));
        
        eq();
    }

    @Test
    public void testContainsAll() {
        Collection<Integer> col = new HashSet<>();
        
        col.add(1);
        col.add(3);
        col.add(7);
        col.add(9);
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        assertTrue(list.containsAll(col));
        
        col.add(-1);
        
        assertFalse(list.containsAll(col));
        
        col.remove(-1);
        
        assertTrue(list.containsAll(col));
        
        col.add(20);
        
        assertFalse(list.containsAll(col));
        
        col.clear();
        
        assertTrue(list.containsAll(col));
        assertTrue(test.containsAll(col));
    }

    @Test
    public void testClone() {
        for (int i = 0; i < 100; ++i) {
            list.add(i);
        }
        
        Random random = new Random();
        float removeFactor = random.nextFloat();
        Iterator<Integer> iter = list.iterator();
        
        while (iter.hasNext()) {
            iter.next();
            
            if (random.nextFloat() < removeFactor) {
                iter.remove();
            }
        }
        
        LinkedArrayList<Integer> clone = 
                (LinkedArrayList<Integer>) list.clone();
        
        eq(list, clone);
    }
    
    @Test
    public void testAddAll_Collection() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        eq();
        
        List<Integer> addList = new ArrayList<>();
        
        for (int i = 20; i < 30; ++i) {
            addList.add(i);
        }
        
        assertTrue(list.addAll(addList));
        assertTrue(test.addAll(addList));
        
        eq();
        
        addList.clear();
        
        assertFalse(list.addAll(addList));
        assertFalse(test.addAll(addList));
    }

    @Test
    public void testAddAll_int_Collection() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> toadd = new ArrayList<>();
        
        for (int i = -10; i < -5; ++i) {
            toadd.add(i);
        }
        
        assertTrue(list.addAll(18, toadd));
        assertTrue(test.addAll(18, toadd));
        
        eq();
        
        toadd.clear();
        
        assertFalse(list.addAll(18, toadd));
        assertFalse(test.addAll(18, toadd));
    }
    
    @Test
    public void testAddAll_int_Collection_brute() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        List<Integer> addList = new ArrayList<>();
        
        System.out.println("testAddAll_int_Collection_brute: seed = " + seed);
        
        for (int run = 0; run < 100; ++run) {
            list.clear();
            test.clear();
            
            final int initialSize = random.nextInt(217);
            
            for (int i = 0; i < initialSize; ++i) {
                int num = random.nextInt();
                list.add(num);
                test.add(num);
            }
            
            eq();
            
            int additions = random.nextInt(30);
            
            for (int addition = 0; addition < additions; ++addition) {
                int additionLength = random.nextInt(30);
                
                for (int i = 0; i < additionLength; ++i) {
                    addList.add(random.nextInt());
                }
            
                int insertIndex = random.nextInt(list.size() + 1);
                list.addAll(insertIndex, addList);
                test.addAll(insertIndex, addList);
                
                //System.out.println("run: " + run + ", addition: " + addition);
                
                eq();
                addList.clear();
            }
        }
    }

    @Test
    public void testEquals() {
        assertTrue(test.equals(list));
        assertTrue(list.equals(test));
        
        for (int i = 0; i < 40; ++i) {
            list.add(i);
            
            assertFalse(test.equals(list));
            assertFalse(list.equals(test));
            
            test.add(i);
            
            assertTrue(test.equals(list));
            assertTrue(list.equals(test));
        }
        
        test.remove(11);
        
        assertFalse(test.equals(list));
        
        list.remove(11);
        
        assertTrue(test.equals(list));
    }
    
    @Test
    public void testHashCode() {
        assertEquals(test.hashCode(), list.hashCode());
        
        for (int i = 0; i < 40; ++i) {
            list.add(i);
            
            assertFalse(list.hashCode() == test.hashCode());
            
            test.add(i);
            
            assertTrue(list.hashCode() == test.hashCode());
        }
        
        list.remove(new Integer(14));
        
        assertFalse(list.hashCode() == test.hashCode());
        
        test.remove(new Integer(15));
        
        assertFalse(list.hashCode() == test.hashCode());
        
        list.remove(new Integer(15));
        
        assertFalse(list.hashCode() == test.hashCode());
        
        test.remove(new Integer(14));
        
        assertEquals(test.hashCode(), list.hashCode());
    }
    
    @Test
    public void testRemoveAll() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Collection<Integer> col1 = new HashSet<>();
       
        col1.add(2);
        col1.add(3);
        col1.add(4);
        col1.add(5);
        
        eq();
        
        assertTrue(list.removeAll(col1));
        assertTrue(test.removeAll(col1));
        
        eq();
        
        col1.clear();
        
        assertFalse(list.removeAll(col1));
        assertFalse(test.removeAll(col1));
        
        eq();
        
        col1.add(10);
        col1.add(10);
        col1.add(11);
        col1.add(11);
        
        assertTrue(list.removeAll(col1));
        assertTrue(test.removeAll(col1));
        
        eq();
        
        col1 = new ArrayList<>();
        
        col1.add(15);
        col1.add(15);
        col1.add(15);
        col1.add(16);
        
        assertTrue(list.removeAll(col1));
        assertTrue(test.removeAll(col1));
        
        eq();
    }

    @Test
    public void testRetainAll() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Collection<Integer> col = new ArrayList<>();
        
        col.add(3);
        col.add(4);
        col.add(4);
        col.add(4);
        col.add(5);
        col.add(5);
        
        col.add(7);
        col.add(8);
        
        col.add(1);
        col.add(2);
        
        assertTrue(list.retainAll(col));
        assertTrue(test.retainAll(col));
        
        eq();
        
        col.clear();
        
        assertTrue(list.retainAll(col));
        assertTrue(test.retainAll(col));
        
        assertTrue(list.isEmpty());
        assertTrue(test.isEmpty());
        
        eq();
        
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        col = new HashSet<>();
        
        for (int i = -10; i < 20; ++i) {
            col.add(i);
            col.add(i);
        }
        
        assertFalse(list.retainAll(col));
        assertFalse(test.retainAll(col));
        
        eq();
        
        col.remove(3);
        col.remove(4);
        col.remove(5);
        
        assertTrue(list.retainAll(col));
        assertTrue(test.retainAll(col));
    }

    @Test
    public void testClear() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        assertFalse(list.isEmpty());
        assertEquals(20, list.size());
        
        list.clear();
        
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertFalse(list.iterator().hasNext());
    }

    @Test
    public void testGet() {
        Integer[] data = new Integer[20];
        for (int i = 0; i < 20; ++i) {
            data[i] = i;
            list.add(data[i]);
            test.add(data[i]);
        }
        
        assertEquals(test.size(), list.size());
        
        for (int i = 0; i < 20; ++i) {
            assertEquals(data[i], list.get(i));
        }
        
        eq();
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBadIndexThrows1() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
        }
        
        list.get(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBadIndexThrows2() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
        }
        
        list.get(list.size());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddBadIndexThrows1() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
        }
        
        list.get(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddBadIndexThrows2() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
        }
        
        list.get(list.size() + 1);
    }
    
    @Test
    public void testSet() {
        Integer[] data = new Integer[20];
        for (int i = 0; i < 20; ++i) {
            data[i] = i;
            list.add(data[i]);
            test.add(data[i]);
        }
        
        assertEquals(test.size(), list.size());
        
        for (int i = 0; i < 20; ++i) {
            assertEquals(data[i], list.get(i));
        }
        
        eq();
        
        list.set(3, -3);
        list.set(4, -4);
        list.set(5, -5);
        list.set(10, -10);
        
        test.set(3, -3);
        test.set(4, -4);
        test.set(5, -5);
        test.set(10, -10);
        
        eq();
    }

    @Test
    public void testAdd_int_GenericType() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.add(list.size(), 100);
        test.add(test.size(), 100);
        
        eq();
        
        list.add(list.size(), 101);
        test.add(test.size(), 101);
        
        eq();
        
        list.add(list.size() - 1, 102);
        test.add(test.size() - 1, 102);
        
        eq();
        
        list.add(10, 103);
        test.add(10, 103);
        
        eq();
    }
    
    @Test 
    public void testAdd_int_GenericType_brute() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        final long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        System.out.println("testAdd_int_GenericType_brute: seed = " + seed);
        
        int n = random.nextInt(200) + 100;
        
        for (int i = 0; i < n; ++i) {
            Integer num = random.nextInt();
            int index = random.nextInt(list.size() + 1);
            
            list.add(index, num);
            test.add(index, num);
            
            eq();
        }
    }

    @Test
    public void testRemove_int() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        assertEquals(test.size(), list.size());
        
        list.remove(10);
        test.remove(10);
        eq();
        
        list.remove(5);
        test.remove(5);
        eq();
        
        list.remove(4);
        test.remove(4);
        eq();
        
        list.remove(3);
        test.remove(3);
        eq();
        
        // Unlinks the head node.
        list.remove(0);
        test.remove(0);
        eq();
        
        list.remove(0);
        test.remove(0);
        eq();
        
        list.remove(0);
        test.remove(0);
        eq();
        
        // Unlinks the tail node.
        list.remove(list.size() - 1);
        test.remove(test.size() - 1);
        eq();
        
        list.remove(list.size() - 1);
        test.remove(test.size() - 1);
        eq();
        
        list.remove(list.size() - 1);
        test.remove(test.size() - 1);
        eq();
    }

    @Test
    public void testIndexOf() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        list.add(20);
        list.add(20);
        list.add(20); 
        list.add(21);
        list.add(21);
        
        for (int i = 0; i < 20; ++i) {
            assertEquals(i, list.indexOf(i));
        }
        
        assertEquals(-1, list.indexOf(-1));
        assertEquals(-1, list.indexOf(-2));
        assertEquals(-1, list.indexOf(-3));
        
        assertEquals(20, list.indexOf(20));
        assertEquals(23, list.indexOf(21));
    }

    @Test
    public void testLastIndexOf() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        list.add(20);
        list.add(20);
        list.add(20); 
        list.add(21);
        list.add(21);
        
        for (int i = 0; i < 20; ++i) {
            assertEquals(i, list.lastIndexOf(i));
        }
        
        assertEquals(-1, list.lastIndexOf(-1));
        assertEquals(-1, list.lastIndexOf(-2));
        assertEquals(-1, list.lastIndexOf(-3));
        
        assertEquals(22, list.lastIndexOf(20));
        assertEquals(24, list.lastIndexOf(21));
    }

    @Test
    public void testListIterator_0args() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator();
        ListIterator<Integer> testIter = test.listIterator();
        
        while (testIter.hasNext()) {
            assertTrue(listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            Integer listInt = listIter.next();
            Integer testInt = testIter.next();
            
            assertEquals(testInt, listInt);
        }
        
        assertFalse(listIter.hasNext());
        
        while (testIter.hasPrevious()) {
            assertTrue(listIter.hasPrevious());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            Integer listInt = listIter.previous();
            Integer testInt = testIter.previous();
            
            assertEquals(testInt, listInt);
        }
        
        assertFalse(listIter.hasPrevious());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testListIteratorThrowsOnIterationOverEmptyListBackward() {
        ListIterator<Integer> iter = list.listIterator();
        
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());
        
        assertFalse(iter.hasPrevious());
        assertFalse(iter.hasNext());
        
        iter.previous();
        
        fail("The list iterator should have thrown an exception.");
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testListIteratorThrowsOnIterationOverEmptyListForward() {
        ListIterator<Integer> iter = list.listIterator();
        
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());
        
        assertFalse(iter.hasPrevious());
        assertFalse(iter.hasNext());
        
        iter.next();
        
        fail("The list iterator should have thrown an exception.");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testListIteratorThrowsOnRemovingSameElementTwice() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iterList = list.listIterator(3);
        ListIterator<Integer> iterTest = test.listIterator(3);
        
        iterList.next();
        iterTest.next();
        
        iterTest.remove();
        iterList.remove();
        
        eq();
        
        try {
            iterTest.remove();
            fail("ArrayList did not throw no removing an element twice.");
        } catch (IllegalStateException ex) {
            
        }
        
        iterTest.remove();
    }
    
    @Test
    public void testListIterator_brute() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        System.out.println("testListIterator_brute: seed = " + seed);
        
        for (int runId = 0; runId < 100; ++runId) {
            for (int i = 0; i < 10; ++i) {
                list.add(i);
                test.add(i);
            }

            int operationAmount = random.nextInt(80) + 20;
            int startIndex = random.nextInt(list.size() + 1);
            
            ListIterator<Integer> listIterator = list.listIterator(startIndex);
            ListIterator<Integer> testIterator = test.listIterator(startIndex);
            
            assertEquals(listIterator.next(), testIterator.next());
            
            boolean lastAdd = false;
            boolean lastRemove = false;
            
            for (int operation = 0; operation < operationAmount; ++operation) {
                assertEquals(listIterator.previousIndex(), 
                             testIterator.previousIndex());
                
                assertEquals(listIterator.nextIndex(),
                             testIterator.nextIndex());
                
                assertEquals(listIterator.hasPrevious(), 
                             testIterator.hasPrevious());
                
                assertEquals(listIterator.hasNext(), testIterator.hasNext());
                
                float chance = random.nextFloat();
                
                if (chance < 0.1f) {
                    if (!lastRemove) {
                        lastRemove = true;
                        listIterator.remove();
                        testIterator.remove();
                    }
                } else if (chance < 0.27f) {
                    
                }
                
                eq();
            }
            
            list.clear();
            test.clear();
        }
    }
    
    @Test
    public void testListIteratorValuesAfterAdd() {
        for (int i = 0; i < 5; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator();
        ListIterator<Integer> testIter = test.listIterator();
        
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.next(), testIter.next());
        
        listIter.add(29);
        testIter.add(29);
        
        assertEquals(new Integer(3), listIter.next());
        assertEquals(new Integer(3), testIter.next());
        
        assertEquals(new Integer(3), listIter.previous());
        assertEquals(new Integer(3), testIter.previous());
        
        assertEquals(new Integer(29), testIter.previous());
        assertEquals(new Integer(29), listIter.previous());
    }
    
    @Test
    public void testListIteratorAdd() {
        ListIterator<Integer> iterTest = test.listIterator();
        ListIterator<Integer> iterList = list.listIterator();
        
        for (int i = 0; i < 11; ++i) {
            iterTest.add(i);
            
            System.out.println(i);
            iterList.add(i);
            
            if (i == 5) {
                iterTest.previous();
                iterTest.remove();
                
                iterList.previous();
                iterList.remove();
            }
        }
        
        eq();
        
        System.out.println(test);
        System.out.println(list);
    }
    
    
    
    @Test 
    public void testListIteratorAlternation() {
        list.add(10);
        ListIterator<Integer> iter = list.listIterator();
        
        assertFalse(iter.hasPrevious());
        assertTrue(iter.hasNext());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());
        
        assertEquals(new Integer(10), iter.next());
        
        assertTrue(iter.hasPrevious());
        assertFalse(iter.hasNext());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());
        
        assertEquals(new Integer(10), iter.previous());
        
        assertFalse(iter.hasPrevious());
        assertTrue(iter.hasNext());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());
    }

    @Test
    public void testListIteratorSet() {
        for (int i = 0; i < 4; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator(2);
        ListIterator<Integer> testIter = test.listIterator(2);
        
        assertEquals(1, listIter.previousIndex());
        assertEquals(1, testIter.previousIndex());
        
        assertEquals(2, listIter.nextIndex());
        assertEquals(2, testIter.nextIndex());
        
        eq();
        
        assertEquals(new Integer(2), listIter.next());
        assertEquals(new Integer(2), testIter.next());
        
        eq();
        
        listIter.set(10);
        testIter.set(10);
        
        assertTrue(listIter.hasNext());
        assertTrue(testIter.hasNext());
        
        eq();
        
        listIter.set(25);
        testIter.set(25);
        
        eq();
        
        listIter.set(29);
        testIter.set(29);
        
        eq();
    }
    
    @Test
    public void testListIterator_int() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator(10);
        ListIterator<Integer> testIter = test.listIterator(10);
        
        while (testIter.hasNext()) {
            assertTrue(listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            Integer listInt = listIter.next();
            Integer testInt = testIter.next();
            
            assertEquals(testInt, listInt);
        }
        
        assertFalse(listIter.hasNext());
        
        while (testIter.hasPrevious()) {
            assertTrue(listIter.hasPrevious());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            Integer listInt = listIter.previous();
            Integer testInt = testIter.previous();
            
            assertEquals(testInt, listInt);
        }
        
        assertFalse(listIter.hasPrevious());
    }

    @Test
    public void testSubList() {
    }
    
    private void eq() {
        assertEquals(test.size(), list.size());
        
        for (int i = 0; i < list.size(); ++i) {
            Integer testInt = test.get(i);
            Integer listInt = list.get(i);
            assertTrue(Objects.equals(listInt, testInt));
        }
        
        Iterator<Integer> itList = list.iterator();
        Iterator<Integer> itTest = test.iterator();
        
        while (itTest.hasNext()) {
            assertTrue(itList.hasNext());
            assertTrue(Objects.equals(itTest.next(), itList.next()));
        }
        
        assertFalse(itList.hasNext());
//        list.checkHealth();
    }
    
    private void eq(LinkedArrayList<Integer> list, List<Integer> test) {
        assertEquals(list.size(), test.size());
        
        for (int i = 0; i < test.size(); ++i) {
            assertTrue(Objects.equals(test.get(i), list.get(i)));
        }
        
        Iterator<Integer> itList = list.iterator();
        Iterator<Integer> itTest = test.iterator();
        
        while (itTest.hasNext()) {
            assertTrue(itList.hasNext());
            assertTrue(Objects.equals(itTest.next(), itList.next()));
        }
        
        assertFalse(itList.hasNext());
        
        ListIterator<Integer> itList2 = list.listIterator();
        ListIterator<Integer> itTest2 = test.listIterator();
        
        while (itTest2.hasNext()) {
            assertTrue(itList2.hasNext());
            assertTrue(Objects.equals(itList2.next(), itTest2.next()));
            assertEquals(itTest2.previousIndex(), itList2.previousIndex());
            assertEquals(itTest2.nextIndex(), itList2.nextIndex());
        }
            
        assertFalse(itList2.hasNext());
        
        while (itTest2.hasPrevious()) {
            assertTrue(itList2.hasPrevious());
            assertTrue(Objects.equals(itList2.previous(), itTest2.previous()));
            assertEquals(itTest2.previousIndex(), itList2.previousIndex());
            assertEquals(itTest2.nextIndex(), itList2.nextIndex());
        }
//        test.checkHealth();
    }
}
