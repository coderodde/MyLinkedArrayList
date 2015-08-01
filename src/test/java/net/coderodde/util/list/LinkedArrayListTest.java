package net.coderodde.util.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
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
    public void testDequeAddFirst() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            deque.add(i);
            list.add(i);
        }
        
        for (int i = 0; i < 7; ++i) {
            eq(deque, list);
            d.addFirst(i + 123);
            list.addFirst(i + 123);
        }
        
        eq(deque, list);
    }
    
    @Test
    public void testDequeAddLast() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            deque.add(i);
            list.add(i);
        }
        
        for (int i = 0; i < 7; ++i) {
            eq(deque, list);
            d.addLast(i + 123);
            list.addLast(i + 123);
        }
        
        eq(deque, list);
    }

    @Test
    public void testDequeDescendingIterator() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        Iterator<Integer> iter1 = d.descendingIterator();
        Iterator<Integer> iter2 = list.descendingIterator();
        
        assertFalse(iter1.hasNext());
        assertFalse(iter2.hasNext());
        
        try {
            iter1.next();
            fail();
        } catch (NoSuchElementException ex) {
            
        } 
        
        try {
            iter2.next();
            fail();
        } catch (NoSuchElementException ex) {
            
        } 
        
        for (int i = 0; i < 5; ++i) {
            d.addLast(i);
            list.addLast(i);
        }
        
        iter1 = d.descendingIterator();
        iter2 = list.descendingIterator();
        
        while (iter1.hasNext()) {
            assertEquals(iter1.next(), iter2.next());
        }
        
        assertFalse(iter2.hasNext());
        
        for (int i = 0; i < 5; ++i) {
            d.addLast(i);
            list.addLast(i);
        }
        
        iter1 = d.descendingIterator();
        iter2 = list.descendingIterator();
        
        eq(deque, list);
        
        for (int i = 0; i < 3; ++i) {
            assertEquals(iter1.next(), iter2.next());
        }
        
        iter1.remove();
        iter2.remove();
        
        System.out.println(d);
        
        eq(deque, list);
        
        while (iter1.hasNext()) {
            assertEquals(iter1.next(), iter2.next());
        }
        
        assertFalse(iter2.hasNext());
        
        eq(deque, list);
        
        iter1 = d.descendingIterator();
        iter2 = list.descendingIterator();
        
        class TempConsumer implements Consumer<Integer> {

            private int index;
            private final Integer[] array;
            
            TempConsumer() {
                array = new Integer[]{
                    4, 3, 1, 0, 4, 3, 2, 1, 0, 
                };
            }
            
            @Override
            public void accept(Integer t) {
                assertEquals(t, array[index++]);
            }
            
        }
        
        iter1.forEachRemaining(new TempConsumer());
        iter2.forEachRemaining(new TempConsumer());
    }
    
    @Test
    public void testDequeElement() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            deque.add(i);
            list.add(i);
        }
        
        eq(deque, list);
        
        assertEquals(d.element(), list.element());
        
        d.clear();
        list.clear();
        
        try {
            d.element();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.element();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testDequeGetFirst() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            deque.add(i);
            list.add(i);
        }
        
        eq(deque, list);
        
        assertEquals(d.getFirst(), list.getFirst());
        
        d.addFirst(1111);
        list.addFirst(1111);
        
        assertEquals(d.getFirst(), list.getFirst());
        
        d.clear();
        list.clear();
        
        try {
            d.getFirst();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.getFirst();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testDequeGetLast() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            deque.add(i);
            list.add(i);
        }
        
        eq(deque, list);
        
        assertEquals(d.getLast(), list.getLast());
        
        d.addLast(1111);
        list.addLast(1111);
        
        assertEquals(d.getLast(), list.getLast());
        
        d.clear();
        list.clear();
        
        try {
            d.getLast();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.getLast();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testDequeOffer() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            d.offer(i);
            list.offer(i);
        }
        
        eq(deque, list);
        
        assertTrue(d.offer(12321));
        assertTrue(list.offer(12321));
        
        eq(deque, list);
        
        assertEquals(d.getLast(), list.getLast());
    }
    
    @Test
    public void testDequeOfferFirst() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            d.offerFirst(i);
            list.offerFirst(i);
        }
        
        eq(deque, list);
        
        assertTrue(d.offerFirst(12321));
        assertTrue(list.offerFirst(12321));
        
        eq(deque, list);
        
        assertEquals(d.getFirst(), list.getFirst());
    }
    
    @Test
    public void testDequeOfferLast() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 10; ++i) {
            d.offerLast(i);
            list.offerLast(i);
        }
        
        eq(deque, list);
        
        assertTrue(d.offerLast(12321));
        assertTrue(list.offerLast(12321));
        
        eq(deque, list);
        
        assertEquals(d.getLast(), list.getLast());
    }
    
    @Test
    public void testPeek() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        assertNull(d.peek());
        assertNull(list.peek());
        
        assertNull(d.peekFirst());
        assertNull(list.peekFirst());
        
        assertNull(d.peekLast());
        assertNull(list.peekLast());
        
        list.offer(1);
        d.offer(1);
        
        assertEquals(d.peek(), list.peek());
        assertEquals(d.peekFirst(), list.peekFirst());
        assertEquals(d.peekLast(), list.peekLast());
        assertEquals(d.peekFirst(), list.peekLast());
        
        list.offer(4);
        d.offer(4);
        
        assertEquals(d.peek(), list.peek());
        assertEquals(d.peekFirst(), list.peekFirst());
        assertEquals(d.peekLast(), list.peekLast());
        assertFalse(d.peekFirst().equals(d.peekLast()));
        assertFalse(list.peekFirst().equals(list.peekLast()));
    }
    
    @Test
    public void testDequePoll() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        assertNull(list.poll());
        assertNull(d.poll());
        
        for (int i = 0; i < 10; ++i) {
            d.offerLast(i);
            list.offerLast(i);
        }
        
        assertEquals(d.poll(), list.poll());
        
        d.addFirst(123);
        list.addFirst(123);
        
        assertEquals(d.poll(), list.poll());
        
        eq(deque, list);
        
        d.addFirst(124);
        list.addFirst(124);
        
        eq(deque, list);
        
        assertEquals(d.poll(), list.poll());
       
        eq(deque, list);
        
        d.addFirst(125);
        list.addFirst(125);
        d.addFirst(126);
        list.addFirst(126);
        
        eq(deque, list);
        
        assertEquals(d.poll(), list.poll());
        assertEquals(d.poll(), list.poll());
        
        eq(deque, list);
    }
    
    @Test
    public void testPollFirst() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        assertNull(list.poll());
        assertNull(d.poll());
        
        for (int i = 0; i < 10; ++i) {
            d.offerLast(i);
            list.offerLast(i);
        }
        
        assertEquals(d.pollFirst(), list.pollFirst());
        
        d.addFirst(123);
        list.addFirst(123);
        
        assertEquals(d.pollFirst(), list.pollFirst());
        
        eq(deque, list);
        
        d.addFirst(124);
        list.addFirst(124);
        
        eq(deque, list);
        
        assertEquals(d.pollFirst(), list.pollFirst());
       
        eq(deque, list);
        
        d.addFirst(125);
        list.addFirst(125);
        d.addFirst(126);
        list.addFirst(126);
        
        eq(deque, list);
        
        assertEquals(d.pollFirst(), list.pollFirst());
        assertEquals(d.pollFirst(), list.pollFirst());
        
        eq(deque, list);
    }
    
    @Test
    public void testPollLast() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        assertNull(list.pollLast());
        assertNull(d.pollLast());
        
        for (int i = 0; i < 10; ++i) {
            d.offerLast(i);
            list.offerLast(i);
        }
        
        assertEquals(d.pollLast(), list.pollLast());
        
        d.addLast(123);
        list.addLast(123);
        
        assertEquals(d.pollLast(), list.pollLast());
        
        eq(deque, list);
        
        d.addLast(124);
        list.addLast(124);
        
        eq(deque, list);
        
        assertEquals(d.pollLast(), list.pollLast());
       
        eq(deque, list);
        
        d.addLast(125);
        list.addLast(125);
        d.addLast(126);
        list.addLast(126);
        
        eq(deque, list);
        
        assertEquals(d.pollLast(), list.pollLast());
        assertEquals(d.pollLast(), list.pollLast());
        
        eq(deque, list);
    }
    
    @Test
    public void testDequePop() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        try {
            d.pop();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.pop();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        for (int i = 0; i < 10; ++i) {
            d.offerLast(i);
            list.offerLast(i);
        }
        
        while (!list.isEmpty()) {
            assertEquals(d.pop(), list.pop());
        }
    }
    
    @Test
    public void testDequePush() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        for (int i = 0; i < 23; ++i) {
            list.push(i);
            d.push(i);
        }
        
        while (!list.isEmpty()) {
            assertEquals(d.pop(), list.pop());
        }
        
        assertTrue(d.isEmpty());
    }
    
    @Test
    public void testDequeRemove() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        try {
            d.remove();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.remove();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        for (int i = 0; i < 10; ++i) {
            d.addLast(i);
            list.addLast(i);
        }
        
        for (int i = 0; i < 10; ++i) {
            assertEquals(d.remove(), list.remove());
        }
        
        assertTrue(d.isEmpty());
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void testDequeRemoveFirst() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        try {
            d.removeFirst();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.removeFirst();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        for (int i = 0; i < 10; ++i) {
            d.addLast(i);
            list.addLast(i);
        }
        
        for (int i = 0; i < 10; ++i) {
            assertEquals(d.removeFirst(), list.removeFirst());
        }
        
        assertTrue(d.isEmpty());
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void testDequeRemoveFirstOccurrence() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        Integer num = Integer.valueOf(2);
        
        assertFalse(d.removeFirstOccurrence(num));
        assertFalse(list.removeFirstOccurrence(num));
        
        for (int i = 0; i < 10; ++i) {
            d.add(i);
            list.add(i);
        }
        
        num = Integer.valueOf(6);
        d.add(num);
        list.add(num);
        
        assertTrue(d.removeFirstOccurrence(num));
        assertTrue(list.removeFirstOccurrence(num));
        
        eq(deque, list);
    }
    
    @Test
    public void testDequeRemoveLast() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        
        try {
            d.removeLast();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            list.removeLast();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        for (int i = 0; i < 10; ++i) {
            d.addLast(i);
            list.addLast(i);
        }
        
        for (int i = 0; i < 10; ++i) {
            assertEquals(d.removeLast(), list.removeLast());
        }
        
        assertTrue(d.isEmpty());
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void testDequeRemoveLastOccurrence() {
        List<Integer> deque = new LinkedList<>();
        Deque<Integer> d = (Deque<Integer>) deque;
        Integer num = Integer.valueOf(2);
        
        assertFalse(d.removeLastOccurrence(num));
        assertFalse(list.removeLastOccurrence(num));
        
        for (int i = 0; i < 10; ++i) {
            d.add(i);
            list.add(i);
        }
        
        num = Integer.valueOf(6);
        d.add(num);
        list.add(num);
        
        assertTrue(d.removeLastOccurrence(num));
        assertTrue(list.removeLastOccurrence(num));
        
        eq(deque, list);
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
    public void testForEach() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Consumer<Integer> consumer = new Consumer() {
            private int num = 0;
            
            @Override
            public void accept(Object t) {
                Integer i = (Integer) t;
                
                if (i != num++) {
                    throw new IllegalStateException();
                }
            }
        };
        
        list.forEach(consumer);
        
        consumer = new Consumer() {
            private int num = 0;
            
            @Override
            public void accept(Object t) {
                Integer i = (Integer) t;
                
                if (i != num) {
                    throw new IllegalStateException();
                }
                
                ++num;
            }
        };
        
        test.forEach(consumer);
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
        final long mySeed = 0L;
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
                
                test.add(element);
                list.add(element);
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
    public void testListIteratorAdd() {
        ListIterator<Integer> iterTest = test.listIterator();
        ListIterator<Integer> iterList = list.listIterator();
        
        for (int i = 0; i < 11; ++i) {
            iterTest.add(i);
            iterList.add(i);
            
            if (i == 5) {
                iterTest.previous();
                iterTest.remove();
                
                iterList.previous();
                iterList.remove();
            }
        }
        
        eq();
    }
    
    @Test
    public void testListIteratorAdd0() {
        for (int i = 0; i < 10; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter = test.listIterator();
        ListIterator<Integer> listIter = list.listIterator();
        
        assertEquals(testIter.next(), listIter.next());
        assertEquals(testIter.next(), listIter.next());
        assertEquals(testIter.previous(), listIter.previous());
        
        testIter.remove();
        listIter.remove();
        
        testIter.add(10);
        testIter.add(11);
        testIter.add(12);
        testIter.add(14);
        
        listIter.add(10);
        listIter.add(11);
        listIter.add(12);
        listIter.add(14);
        
        eq();
    }
    
    @Test
    public void testListIteratorThrowsOnConcurrentModificationViaIterator() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter1 = test.listIterator();
        ListIterator<Integer> testIter2 = test.listIterator();
        
        ListIterator<Integer> listIter1 = list.listIterator();
        ListIterator<Integer> listIter2 = list.listIterator();
        
        testIter1.add(10);
        
        try {
            testIter2.next();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        listIter1.add(10);
        
        try {
            listIter2.next();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        testIter2 = test.listIterator(2);
        listIter2 = list.listIterator(2);
        
        testIter1.add(10);
        
        try {
            testIter2.previous();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        listIter1.add(10);
        
        try {
            listIter2.previous();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        eq();
    }
    
    @Test
    public void testListIteratorThrowsOnConcurrentModificationViaList() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter = test.listIterator();
        ListIterator<Integer> listIter = test.listIterator();
        
        test.remove(1);
        
        try {
            testIter.next();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        list.remove(1);
        
        try {
            listIter.next();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        testIter = test.listIterator(2);
        listIter = list.listIterator(2);
        
        test.add(0);
        
        try {
            testIter.previous();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        list.add(0);
        
        try {
            listIter.previous();
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        eq();
    }
    
    @Test
    public void testListIteratorSetThrowsOnConcurrencyViaList() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter = test.listIterator(2);
        ListIterator<Integer> listIter = list.listIterator(2);
        
        testIter.next();
        listIter.next();
        
        test.add(0);
        
        try {
            testIter.set(10);
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        list.add(0);
        
        try {
            listIter.set(10);
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testListIteratorSetThrowsOnConcurrencyViaListIterator() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter = test.listIterator(2);
        ListIterator<Integer> listIter = list.listIterator(2);
        
        ListIterator<Integer> testIter2 = test.listIterator(2);
        ListIterator<Integer> listIter2 = list.listIterator(2);
        
        testIter.next();
        listIter.next();
        
        testIter2.add(10);
        
        try {
            testIter.set(10);
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
        
        listIter2.add(10);
        
        try {
            listIter.set(10);
            fail("Should have thrown an exception.");
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testListIteratorAddSemantics() {
        for (int i = 0; i < 4; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator(2);
        ListIterator<Integer> testIter = test.listIterator(2);
        
        listIter.add(10);
        testIter.add(10);
        
        assertEquals(new Integer(2), listIter.next());
        assertEquals(new Integer(2), testIter.next());
        
        assertEquals(new Integer(2), listIter.previous());
        assertEquals(new Integer(2), testIter.previous());
        
        assertEquals(new Integer(10), listIter.previous());
        assertEquals(new Integer(10), testIter.previous());
        
        assertEquals(new Integer(1), listIter.previous());
        assertEquals(new Integer(1), testIter.previous());
        
        eq();
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
        
        assertEquals(new Integer(10), iter.next());
        assertEquals(new Integer(10), iter.previous());
        assertEquals(new Integer(10), iter.next());
        assertEquals(new Integer(10), iter.previous());
    }
    
    @Test
    public void testListIteratorFirstOperationAdd() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator(list.size());
        ListIterator<Integer> testIter = test.listIterator(test.size());
        
        for (int i = 0; i < 8; ++i) {
            testIter.add(i + 7);
            listIter.add(i + 7);
            
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            
            eq();
        }
        
        listIter = list.listIterator(3);
        testIter = test.listIterator(3);
        
        for (int i = 0; i < 8; ++i) {
            testIter.add(i + 7);
            listIter.add(i + 7);
            
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            
            eq();
        }
    }
    
    @Test
    public void testListIteratorIteration() {
        long seed = System.currentTimeMillis();
        System.out.println("testListIteratorIteration: seed = " + seed);
        
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Random random = new Random(seed);
        int startIndex = random.nextInt(list.size() + 1); 
        
        ListIterator<Integer> listIter = list.listIterator(startIndex);
        ListIterator<Integer> testIter = test.listIterator(startIndex);
        
        for (int ops = random.nextInt(100) + 50; ops > 0; --ops) {
            assertEquals(listIter.hasNext(), testIter.hasNext());
            assertEquals(listIter.hasPrevious(), testIter.hasPrevious());
            assertEquals(listIter.previousIndex(), testIter.previousIndex());
            assertEquals(listIter.nextIndex(), testIter.nextIndex());
            
            if (random.nextFloat() < 0.5) {
                if (testIter.hasNext()) {
                    assertEquals(testIter.next(), listIter.next());
                }
            } else {
                if (testIter.hasPrevious()) {
                    assertEquals(testIter.previous(), listIter.previous());
                }
            }
        }
    }
    
    @Test
    public void testListIteratorNonModifyingOperations() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator();
        ListIterator<Integer> testIter = test.listIterator();
        
        assertTrue(listIter.hasNext());
        assertTrue(testIter.hasNext());
        
        assertFalse(listIter.hasPrevious());
        assertFalse(testIter.hasPrevious());
        
        assertEquals(-1, listIter.previousIndex());
        assertEquals(-1, testIter.previousIndex());
        
        assertEquals(0, listIter.nextIndex());
        assertEquals(0, testIter.nextIndex());
        
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.next(), testIter.next());
        assertEquals(listIter.previous(), testIter.previous());
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
    public void testListIterator_brute() {
        long mySeed = 0L;
        long seed = mySeed != 0L ? mySeed : System.currentTimeMillis();
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
            
            boolean lastAdd = false;
            boolean lastRemove = true;
            boolean lastNextOrPrev = false;
            
            for (int operation = 0; operation < operationAmount; ++operation) {
//                System.out.print("runId = " + runId + ", operation = " + operation + ": ");
                
                assertEquals(testIterator.previousIndex(), 
                             listIterator.previousIndex());
                
                assertEquals(testIterator.nextIndex(),
                             listIterator.nextIndex());
                
                assertEquals(testIterator.hasPrevious(), 
                             listIterator.hasPrevious());
                
                assertEquals(testIterator.hasNext(), listIterator.hasNext());
                
                float chance = random.nextFloat();
                
                if (chance < 0.1f) {
                    // Try remove.
                    if (!lastRemove && !lastAdd) {
                        lastRemove = true;
                        lastNextOrPrev = false;
                        
                        listIterator.remove();
                        testIterator.remove();
                        
//                        System.out.println("DEBUG: " + listIterator.hasNext() +
//                                           testIterator.hasNext());
//                        
//                        System.out.println("remove()");
                    }
                } else if (chance < 0.27f) {
                    // Try add.
                    lastAdd = true;
                    lastNextOrPrev = false;
                    
                    Integer tmp = random.nextInt();
                    listIterator.add(tmp);
                    testIterator.add(tmp);
//                    System.out.println("add()");
                } else if (chance < 0.35f) {
                    // Try set.
                    if (lastNextOrPrev) {
                        Integer tmp = random.nextInt();
                        listIterator.set(tmp);
                        testIterator.set(tmp);
//                        System.out.println("set()");
                    }
                } else if (chance < 0.67f) {
                    // Try next.
                    assertEquals(testIterator.hasNext(), 
                                 listIterator.hasNext());
                    
                    if (listIterator.hasNext()) {
                        Integer testInt = testIterator.next();
                        Integer listInt = listIterator.next();
                        
                        assertEquals(testInt, listInt);
                        
                        lastNextOrPrev = true;
                        lastRemove = false;
                        lastAdd = false;
//                        System.out.println("next()");
                    }
                    
                } else {
                    // Try previous.
                    assertEquals(testIterator.hasPrevious(),
                                 listIterator.hasPrevious());
                    
                    if (listIterator.hasPrevious()) {
                        Integer testInt = testIterator.previous();
                        Integer listInt = listIterator.previous();
                        assertEquals(testInt, listInt);
                        
                        lastNextOrPrev = true;
                        lastRemove = false;
                        lastAdd = false;
//                        System.out.println("previous()");
                    }
                }
                
                eq();
            }
            
            list.clear();
            test.clear();
        }
    }
    
    @Test
    public void testListIteratorBug1() {
        for (int i = 0; i < 10; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter = test.listIterator(4);
        ListIterator<Integer> listIter = list.listIterator(4);
        
        for (int i = 0; i < 4; ++i) {
            assertEquals(testIter.next(), listIter.next());
        }
        
        testIter.remove();
        listIter.remove();
        
        assertEquals(testIter.next(), listIter.next());
        eq();
        
        for (int i = 0; i < 3; ++i) {
            assertEquals(testIter.previous(), listIter.previous());
            eq();
        }
        
        for (int i = 0; i < 2; ++i) {
            assertEquals(testIter.next(), listIter.next());
            eq();
        }
        
        eq();
        
        testIter.remove();
        listIter.remove();
        
        eq();
    }
    
    @Test
    public void testListIteratorRemove() {
        for (int i = 0; i < 10; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> testIter = test.listIterator();
        ListIterator<Integer> listIter = list.listIterator();
        
        for (int i = 0; i < 5; ++i) {
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            assertEquals(testIter.next(), listIter.next());
        }
        
        testIter.remove();
        listIter.remove();
        
        eq();
        
        for (int i = 0; i < 2; ++i) {
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            assertEquals(testIter.previous(), listIter.previous());
        }
        
        testIter.remove();
        listIter.remove();
        
        eq();
        
        assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
        assertEquals(testIter.hasNext(), listIter.hasNext());
        assertEquals(testIter.previousIndex(), listIter.previousIndex());
        assertEquals(testIter.nextIndex(), listIter.nextIndex());

        assertEquals(testIter.previous(), listIter.previous());
        assertEquals(testIter.next(), listIter.next());
        
        eq();
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
    
    @Test(expected = NoSuchElementException.class) 
    public void testListIteratorThrowsOnNoPreviousElement() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator(4);
        ListIterator<Integer> testIter = test.listIterator(4);
        
        for (int i = 0; i < 4; ++i) {
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            assertEquals(testIter.previous(), listIter.previous());
        }
        
        assertFalse(testIter.hasPrevious());
        assertFalse(listIter.hasPrevious());
        
        listIter.previous();
    }
    
    @Test(expected = NoSuchElementException.class) 
    public void testListIteratorThrowsOnNoNextElement() {
        for (int i = 0; i < 7; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator(4);
        ListIterator<Integer> testIter = test.listIterator(4);
        
        for (int i = 0; i < 3; ++i) {
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            assertEquals(testIter.next(), listIter.next());
        }
        
        assertFalse(testIter.hasNext());
        assertFalse(listIter.hasNext());
        
        testIter.next();
    }
    
    @Test
    public void testListIteratorAddToEmptyList() {
        ListIterator<Integer> listIter = list.listIterator();
        ListIterator<Integer> testIter = test.listIterator();
        
        listIter.add(0);
        testIter.add(0);
        
        eq();
        
        listIter.add(1);
        testIter.add(1);
        
        eq();
        
        listIter.add(2);
        testIter.add(2);
        
        eq();
        
        listIter.add(3);
        testIter.add(3);
        
        eq();
        
        listIter.add(4);
        testIter.add(4);
        
        eq();
        
        for (int i = 4; i >= 0; --i) {
            assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
            assertEquals(testIter.hasNext(), listIter.hasNext());
            assertEquals(testIter.previousIndex(), listIter.previousIndex());
            assertEquals(testIter.nextIndex(), listIter.nextIndex());
            
            assertEquals(new Integer(i), testIter.previous());
            assertEquals(new Integer(i), listIter.previous());
        }
        
        assertEquals(testIter.hasPrevious(), listIter.hasPrevious());
        assertEquals(testIter.hasNext(), listIter.hasNext());
        assertEquals(testIter.previousIndex(), listIter.previousIndex());
        assertEquals(testIter.nextIndex(), listIter.nextIndex());
    }
    
    //// YOOOOO
    @Test
    public void testListIteratorSetAfterAdd() {
        ListIterator<Integer> listIter = list.listIterator();
        ListIterator<Integer> testIter = test.listIterator();
        
        listIter.add(10);
        testIter.add(10);
        
        boolean listThrew = false;
        boolean testThrew = false;
        
        try {
            listIter.set(11);
        } catch (IllegalStateException ex) {
            listThrew = true;
        }
        
        try {
            testIter.set(11);
        } catch (IllegalStateException ex) {
            testThrew = true;
        }
        
        eq();
        
        assertTrue(testThrew && listThrew);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testListIteratorSetThrowsOnBegin() {
        for (int i = 0; i < 5; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.listIterator().set(0);
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testListIteratorThrowsOnExceedingElements() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iter = test.listIterator(2);
        
        assertTrue(iter.hasPrevious());
        assertEquals(new Integer(1), iter.previous());
        assertTrue(iter.hasPrevious());
        assertEquals(new Integer(0), iter.previous());
        
        iter.previous();
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
    
    @Test(expected = IllegalStateException.class)
    public void testListIteratorThrowsOnRemovingTwice() {
        for (int i = 0; i < 10; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iter = list.listIterator();
        
        iter.next();
        iter.next();
        iter.next();
        iter.previous();
        
        iter.remove();
        iter.remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testListIteratorThrowsOnRemoveWithNoCursor() {
        for (int i = 0; i < 10; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iter = list.listIterator();
        
        iter.remove();
    }
    
    @Test(expected = IllegalStateException.class) 
    public void testListIteratorThrowsOnSetAfterRemove() {
        for (int i = 0; i < 5; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter = list.listIterator();
        
        iter.next();
        iter.next();
        
        iter.remove();
        iter.set(100);
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
        
        assertEquals(new Integer(2), testIter.previous());
        assertEquals(new Integer(2), listIter.previous());
        
        assertEquals(testIter.previousIndex(), listIter.previousIndex());
        assertEquals(testIter.nextIndex(), listIter.nextIndex());
        
        eq();
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
    public void testSublistAdd() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        test.subList(2, 4).add(10);
        list.subList(2, 4).add(10);
        
        eq();
        
        test.subList(3, 6).add(11);
        list.subList(3, 6).add(11);
        
        eq();
        
        test.clear();
        list.clear();
        
        test.subList(0, 0).add(20);
        list.subList(0, 0).add(20);
    }
    
    @Test
    public void testSubListAddInt() {
        for (int i = 0; i < 6; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(1, 3);
        List<Integer> sublist2 = list.subList(1, 3);
        
        sublist1.add(0, 10);
        sublist2.add(0, 10);
        
        eq();
        
        Iterator<Integer> iter1 = sublist1.iterator();
        Iterator<Integer> iter2 = sublist2.iterator();
        
        while (iter1.hasNext()) {
            assertTrue(iter2.hasNext());
            assertEquals(iter1.next(), iter2.next());
        }
        
        assertFalse(iter2.hasNext());
        
        sublist1.add(1, 11);
        sublist2.add(1, 11);
        
        eq();
        
        sublist1.add(sublist1.size(), 12);
        sublist2.add(sublist2.size(), 12);
        
        eq();
    }
    
    @Test
    public void testSublistAddAll() {
        List<Integer> sublistTest = test.subList(0, 0);
        List<Integer> sublistList = list.subList(0, 0);
        
        Collection<Integer> coll = new LinkedList<>();
        
        coll.add(1);
        coll.add(2);
        coll.add(3);
        coll.add(4);
        
        sublistTest.addAll(coll);
        sublistList.addAll(coll);
        
        eq();
        
        eq(sublistList, sublistTest);
        
        coll.clear();
        
        coll.add(10);
        coll.add(11);
        
        sublistTest.addAll(coll);
        sublistList.addAll(coll);
        
        eq();
        
        eq(sublistList, sublistTest);
    }
    
    @Test
    public void testSublistAddAllInt() {
        List<Integer> list1 = test.subList(0, 0);
        List<Integer> list2 = list.subList(0, 0);
        
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = 0; i < 4; ++i) {
            coll.add(i);
        }
        
        list1.addAll(0, coll);
        list2.addAll(0, coll);
        
        eq();
        eq(list1, list2);
        
        coll.clear();
        
        for (int i = 4; i < 9; ++i) {
            coll.add(i);
        }
        
        list1.addAll(2, coll);
        list2.addAll(2, coll);
        
        eq();
        eq(list1, list2);
    }
    
    @Test 
    public void testSublistClear() {
        for (int i = 0; i < 20; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(4, 9);
        List<Integer> sublist2 = list.subList(4, 9);
        
        sublist1.clear();
        sublist2.clear();
        
        eq();
        
        sublist1 = test.subList(0, 4);
        sublist2 = list.subList(0, 4);
        
        sublist1.clear();
        sublist2.clear();
        
        eq();
        
        sublist1 = test.subList(3, test.size());
        sublist2 = list.subList(3, list.size());
        
        sublist1.clear();
        sublist2.clear();
        
        eq();
        
        test.clear();
        list.clear();
        
        for (int i = 0; i < 30; ++i) {
            test.add(i);
            list.add(i);
        }
        
        sublist1 = test.subList(5, 25).subList(5, 15);
        sublist2 = list.subList(5, 25).subList(5, 15);
        
        eq(sublist1, sublist2);
        
        sublist1.clear();
        sublist2.clear();
        
        eq();
    }
    
    @Test
    public void testSublistContains() {
        for (int i = 0; i < 20; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(5, 10);
        List<Integer> sublist2 = list.subList(5, 10);
        
        for (int i = 0; i < 5; ++i) {
            assertFalse(sublist1.contains(i));
            assertFalse(sublist2.contains(i));
        }
        
        for (int i = 5; i < 10; ++i) {
            assertTrue(sublist1.contains(i));
            assertTrue(sublist2.contains(i));
        }
            
        for (int i = 10; i < 20; ++i) {
            assertFalse(sublist1.contains(i));
            assertFalse(sublist2.contains(i));
        }
        
        assertFalse(sublist1.contains(-1));
        assertFalse(sublist2.contains(-1));
        
        assertFalse(sublist1.contains(20));
        assertFalse(sublist2.contains(20));
    }
    
    @Test
    public void testSublistContainsAll() {
        for (int i = 0; i < 20; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(6, 12);
        List<Integer> sublist2 = list.subList(6, 12);
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = 6; i < 12; ++i) {
            coll.add(i);
        }
        
        assertTrue(sublist1.containsAll(coll));
        assertTrue(sublist2.containsAll(coll));
        
        coll.remove(10);
        
        assertTrue(sublist1.containsAll(coll));
        assertTrue(sublist2.containsAll(coll));
        
        coll.add(4);
        
        assertTrue(test.containsAll(coll));
        assertTrue(list.containsAll(coll));
        
        assertFalse(sublist1.containsAll(coll));
        assertFalse(sublist2.containsAll(coll));
    }
    
    @Test
    public void testSublistEquals() {
        for (int i = 0; i < 20; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(5, 11);
        List<Integer> sublist2 = list.subList(5, 11);
        
        assertTrue(sublist1.equals(sublist2));
        assertTrue(sublist2.equals(sublist1));
        assertTrue(sublist1.equals(sublist1));
        assertTrue(sublist2.equals(sublist2));
        
        sublist2.set(3, 1001);
        
        assertFalse(sublist1.equals(sublist2));
        assertFalse(sublist2.equals(sublist1));
        assertTrue(sublist1.equals(sublist1));
        assertTrue(sublist2.equals(sublist2));
        
        sublist2.remove(3);

        assertFalse(sublist1.equals(sublist2));
        assertFalse(sublist2.equals(sublist1));
//        try {
//            fail();
//        } catch (ConcurrentModificationException ex) {
//            
//        }
//        
//        try {
//            fail();
//        } catch (ConcurrentModificationException ex) {
//            
//        }
        
        assertTrue(sublist1.equals(sublist1));
        assertTrue(sublist2.equals(sublist2));
    }
    
    @Test
    public void testSublistGet() {
        for (int i = 0; i < 20; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(5, 12);
        List<Integer> sublist2 = list.subList(5, 12);
        
        for (int i = 0; i < sublist1.size(); ++i) {
            assertEquals(sublist1.get(i), sublist2.get(i));
        }
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSublistGetThrowsOnNegativeIndex() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        list.subList(0, 4).get(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSublistGetThrowsOnLargeIndex() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        list.subList(0, 4).get(4);
    }
    
    @Test
    public  void testSublistIndexOf() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
        }
        
        List<Integer> sublist = list.subList(11, 18);
        
        for (int i = 11, j = 0; i < 18; ++i, ++j) {
            assertEquals(j, sublist.indexOf(i));
        }
        
        for (int i = 10; i > -5; --i) {
            assertEquals(-1, sublist.indexOf(i));
        }
        
        for (int i = 18; i < 26; ++i) {
            assertEquals(-1, sublist.indexOf(i));
        }
        
        list.clear();
        
        list.add(0);
        list.add(3);
        list.add(2);
        list.add(3);
        
        assertEquals(1, list.indexOf(3));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSublistThrowsOnNegativeFromIndexWithEmptySublist() {
        list.subList(-1, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSublistThrowsOnNegativeFromIndexWithNonEmptySublist() {
        list.add(0);
        list.add(1);
        
        list.subList(-1, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class) 
    public void testSublistThrowsOnLargeToIndexWithEmptySublist() {
        list.subList(0, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class) 
    public void testSublistThrowsOnLargeToIndexWithNonEmptySublist() {
        list.add(0);
        list.add(1);
        
        list.subList(0, 3);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSublistThrowsOnReversedIndices() {
        list.add(0);
        list.add(1);
        
        list.subList(1, 0);
    }
    
    @Test
    public void testSublistIsEmpty() {
        List<Integer> sublist = list.subList(0, 0);

        assertTrue(sublist.isEmpty());
        
        sublist.addAll(Arrays.asList(0, 1, 2));
        
        assertFalse(sublist.isEmpty());
        
        sublist.remove(1);
        
        assertFalse(sublist.isEmpty());
        
        sublist.clear();
        
        assertTrue(sublist.isEmpty());
    }
    
    @Test
    public void testSublistLastIndexOf() {
        list.add(0);
        list.add(3);
        list.add(2);
        list.add(3);
        
        assertEquals(3, list.lastIndexOf(3));
        assertEquals(0, list.lastIndexOf(0));
        assertEquals(2, list.lastIndexOf(2));
        
        assertEquals(-1, list.lastIndexOf(-1));
        assertEquals(-1, list.lastIndexOf(4));
    }
    
    @Test
    public void testSublistIteration() {
        for (int i = 0; i < 10; ++i) {
            test.add(i);
            list.add(i);
        }
        
        List<Integer> sublist1 = test.subList(2, 8);
        List<Integer> sublist2 = sublist1.subList(2, 6);
        List<Integer> sublist1a = test.subList(1, 6);
        
        ListIterator<Integer> iter = sublist1.listIterator();
        
        for (int i = 2; i < 8; ++i) {
            assertEquals(new Integer(i), iter.next());
        }
        
        iter = sublist1a.listIterator();
        
        for (int i = 1; i < 6; ++i) {
            assertEquals(new Integer(i), iter.next());
        }
        
        iter = sublist2.listIterator();
        
        for (int i = 4; i < 8; ++i) {
            assertEquals(new Integer(i), iter.next());
        }
    }
    
    @Test
    public void testSublistRemoveAll() {
        for (int i = 0; i < 26; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Collection<Integer> coll = new LinkedList<>();
        
        for (int i = -10; i < 30; i += 2) {
            coll.add(i);
        }
        
        List<Integer> sublist1 = list.subList(4, 16);
        List<Integer> sublist2 = test.subList(4, 16);
        
        assertTrue(sublist1.removeAll(coll));
        assertTrue(sublist2.removeAll(coll));
        
        eq(sublist1, sublist2);
        
        eq();
        
        coll.clear();
        
        assertFalse(sublist1.removeAll(coll));
        assertFalse(sublist2.removeAll(coll));
        
        eq(sublist1, sublist2);
        
        eq();
        
        for (int i = 30; i < 40; ++i) {
            coll.add(i);
        }
        
        assertFalse(sublist1.removeAll(coll));
        assertFalse(sublist2.removeAll(coll));
        
        eq(sublist1, sublist2);
        eq();
    }
    
    @Test
    public void testSublistRemoveInt() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(2, 5);
        List<Integer> sublist2 = test.subList(2, 5);
        
        assertEquals(new Integer(3), sublist1.remove(1));
        assertEquals(new Integer(3), sublist2.remove(1));
        
        eq(sublist1, sublist2);
        
        assertEquals(new Integer(4), sublist1.remove(1));
        assertEquals(new Integer(4), sublist2.remove(1));
        
        eq(sublist1, sublist2);
    }
    
    @Test
    public void testSublistRemoveObject() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 9);
        List<Integer> sublist2 = test.subList(3, 9);
            
        assertTrue(sublist1.remove(new Integer(4)));
        assertTrue(sublist2.remove(new Integer(4)));
            
        assertTrue(sublist1.remove(new Integer(6)));
        assertTrue(sublist2.remove(new Integer(6)));
            
        assertFalse(sublist1.remove(new Integer(9)));
        assertFalse(sublist2.remove(new Integer(9)));
        
        eq(sublist1, sublist2);
        
        eq();
    }
    
    @Test
    public void testSublistRetainAll() {
        for (int i = 0; i < 26; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(5, 17);
        List<Integer> sublist2 = test.subList(5, 17);
        
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = -10; i < 30; i += 2) {
            coll.add(i);
        }
        
        assertTrue(sublist1.retainAll(coll));
        assertTrue(sublist2.retainAll(coll));
        
        eq(sublist1, sublist2);
        eq();
    }
    
    @Test
    public void testSublistSet() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.subList(4, 6).set(1, 111);
        test.subList(4, 6).set(1, 111);
        
        eq();
    }
    
    @Test
    public void testSublistThrowsOnParallelSublist() {
        for (int i = 0; i < 15; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(2, 13)
                                     .subList(0, 9)
                                     .subList(2, 9);
        
        List<Integer> sublist2 = test.subList(2, 13)
                                     .subList(0, 9)
                                     .subList(2, 9);
        
        List<Integer> sublist11 = sublist1.subList(2, 5);
        List<Integer> sublist22 = sublist2.subList(2, 5);
        
        sublist1.add(123);
        sublist2.add(123);
        
        eq();
        
        try {
            sublist11.add(142);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.add(142);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.add(1, 123);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.add(1, 123);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        Collection<Integer> coll = new HashSet<>();
        
        assertFalse(sublist11.addAll(coll));
        assertFalse(sublist22.addAll(coll));
        
        coll.add(298);
        
        try {
            sublist11.addAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.addAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.addAll(1, coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.addAll(1, coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.clear();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.clear();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.contains(null);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.contains(null);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        coll.clear();
        
        assertTrue(sublist11.containsAll(coll));
        assertTrue(sublist22.containsAll(coll));
        
        coll.add(345);
        
        try {
            sublist11.containsAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.containsAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.forEach((i) -> {});
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.forEach((i) -> {});
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.get(1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.get(1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.indexOf(Integer.valueOf(3));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.indexOf(Integer.valueOf(3));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.isEmpty();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.isEmpty();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.iterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.iterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.lastIndexOf(null);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.lastIndexOf(null);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.listIterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.listIterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.listIterator(2);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.listIterator(2);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.remove(Integer.valueOf(5));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.remove(Integer.valueOf(5));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.remove(1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.remove(1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        coll.clear();
        
        try {
            sublist11.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        coll.add(321);
        
        try {
            sublist11.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        coll.clear();
        
        try {
            sublist11.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.set(2, 12321);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.set(2, 12321);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.size();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist22.size();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist11.subList(0, 1);
        } catch (ConcurrentModificationException ex) {
            // subList should not throw
            fail();
        }
        
        try {
            sublist22.subList(0, 1);
        } catch (ConcurrentModificationException ex) {
            // subList should not throw
            fail();
        }
        
        try {
            sublist11.toArray();
            fail();
        } catch (ConcurrentModificationException ex) {
        
        }
        
        try {
            sublist22.toArray();
            fail();
        } catch (ConcurrentModificationException ex) {
        
        }
        
        try {
            sublist11.toArray(new Integer[3]);
            fail();
        } catch (ConcurrentModificationException ex) {
        
        }
        
        try {
            sublist22.toArray(new Integer[3]);
            fail();
        } catch (ConcurrentModificationException ex) {
        
        }
        
        // Check that overlapping sublists throw as expected.
        sublist1 = list.subList(1, 4);
        List<Integer> sublist1b = list.subList(3, 7);
        
        sublist2 = test.subList(1, 4);
        List<Integer> sublist2b = test.subList(3, 7);
        
        sublist1.add(2, 222);
        sublist2.add(2, 222);
        
        try {
            sublist1b.set(1, 12321);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2b.set(1, 12321);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistSize() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        assertEquals(4, test.subList(4, 8).size());
        assertEquals(4, list.subList(4, 8).size());
        
        assertEquals(0, test.subList(10, 10).size());
        assertEquals(0, list.subList(10, 10).size());
        
        assertEquals(0, test.subList(0, 0).size());
        assertEquals(0, list.subList(0, 0).size());
        
        assertEquals(1, test.subList(9, 10).size());
        assertEquals(1, list.subList(9, 10).size());
        
        assertEquals(1, test.subList(0, 1).size());
        assertEquals(1, list.subList(0, 1).size());
        
        assertEquals(3, test.subList(2, 5).size());
        assertEquals(3, list.subList(2, 5).size());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testSublistListIteratorThrowsOnNoNextElement() {
        title("testSublistListIteratorThrowsOnNoNextElement");
        
        for (int i = 0; i < 33; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iterator1 = test.subList(10, 15).listIterator();
        ListIterator<Integer> iterator2 = list.subList(10, 15).listIterator();
        
        for (int i = 0; i < 5; ++i) {
            assertEquals(iterator1.next(), iterator2.next());
        }
        
        assertFalse(iterator1.hasNext());
        assertFalse(iterator2.hasNext());
        
        try {
            iterator1.next();
            fail("Failed.");
        } catch (NoSuchElementException ex) {
            System.out.println("-- ArrayList threw as expected.");
        }
        
        iterator2.next();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testSublistListItertorThrowsOnNoPreviousElement() {
        title("testSublistListIteratorThrowsOnNoPreviousElement");
        
        for (int i = 0; i < 33; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iterator1 = test.subList(10, 15).listIterator();
        ListIterator<Integer> iterator2 = list.subList(10, 15).listIterator();
        
        for (int i = 0; i < 2; ++i) {
            assertEquals(iterator1.next(), iterator2.next());
        }
        
        for (int i = 0; i < 2; ++i) {
            assertEquals(iterator1.previous(), iterator2.previous());
        }
        
        assertFalse(iterator1.hasPrevious());
        assertFalse(iterator2.hasPrevious());
        
        try {
            iterator1.previous();
            fail("Failed.");
        } catch (NoSuchElementException ex) {
            System.out.println("-- ArrayList threw as expected.");
        }
        
        iterator2.previous();
    }
    
    @Test
    public void testSublistListIteratorThrowsOnDoubleRemove() {
        title("testSublistListIteratorThrowsOnDoubleRemove");
        
        for (int i = 0; i < 33; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iterator1 = test.subList(5, 14).listIterator(2);
        ListIterator<Integer> iterator2 = list.subList(5, 14).listIterator(2);
        
        assertTrue(iterator1.hasNext());
        assertEquals(new Integer(7), iterator1.next());
        assertEquals(new Integer(7), iterator2.next());
        
        iterator2.remove();
        iterator1.remove();
        
        try {
            iterator2.remove();
            fail("List iterator of sublist should have threw.");
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iterator1.remove();
            fail("ArrayList iterator of sublist should have threw.");
        } catch (IllegalStateException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorThrowsOnInitialRemove() {
        for (int i = 0; i < 5; ++i) {
            test.add(i);
            list.add(i);
        }
        
        ListIterator<Integer> iterator1 = test.subList(1, 4).listIterator(1);
        ListIterator<Integer> iterator2 = list.subList(1, 4).listIterator(1);
        
        try {
            iterator1.remove();
            fail();
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iterator2.remove();
            fail();
        } catch (IllegalStateException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorThrowsOnNegativeInitialCursor() {
        for (int i = 0; i < 15; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter1;
                
        try {
            iter1 = test.subList(2, 12).listIterator(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        ListIterator<Integer> iter2;
        
        try {
            iter2 = list.subList(2, 12).listIterator(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorThrowsOnLargeInitialCursor() {
        for (int i = 0; i < 15; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter1;
                
        try {
            iter1 = test.subList(2, 12).listIterator(11);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        ListIterator<Integer> iter2;
        
        try {
            iter2 = list.subList(2, 12).listIterator(11);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorNext() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iterator1 = list.subList(2, 8).listIterator(2);
        ListIterator<Integer> iterator2 = test.subList(2, 8).listIterator(2);
        
        int i = 0;
        
        while (iterator2.hasNext()) {
            System.out.println(++i);
            assertEquals(iterator2.next(), iterator1.next());
        }
        
        assertFalse(iterator1.hasNext());
        
        list.add(100);
        test.add(100);
        
        try {
            iterator1.next();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            iterator2.next();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        iterator1 = list.subList(2, 8).listIterator(6);
        iterator2 = test.subList(2, 8).listIterator(6);
        
        try {
            iterator1.next();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            iterator2.next();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorPrevious() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iterator1 = list.subList(2, 8).listIterator(2);
        ListIterator<Integer> iterator2 = test.subList(2, 8).listIterator(2);
        
        for (int i = 0; i < 2; ++i) {
            assertTrue(iterator1.hasPrevious());
            assertTrue(iterator2.hasPrevious());
            
            assertEquals(iterator1.previous(), iterator2.previous());
        }
        
        assertFalse(iterator1.hasPrevious());
        assertFalse(iterator2.hasPrevious());
        
        try {
            iterator2.previous();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
        
        try {
            iterator1.previous();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorSet() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter1 = list.subList(1, 5).listIterator(1);
        ListIterator<Integer> iter2 = test.subList(1, 5).listIterator(1);
        
        iter1.next();
        iter2.next();
        
        iter1.set(1000);
        iter2.set(1000);
        
        eq();
        
        iter1.set(1010);
        iter2.set(1010);
        
        eq();
    }
    
    @Test
    public void testSublistListIteratorAdd() {
        for (int i = 0; i < 17; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter1 = list.subList(0, 4).listIterator();
        ListIterator<Integer> iter2 = test.subList(0, 4).listIterator();
        
        iter1.next();
        iter2.next();
        
        for (int i = 0; i < 3; ++i) {
            iter1.add(i + 100);
            iter2.add(i + 100);
        }
        
        eq();
    }
    
    @Test
    public void testSublistListIteratorThrowsOnSetAfterNoNextPrevious() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter1 = list.subList(3, 7).listIterator(2);
        ListIterator<Integer> iter2 = test.subList(3, 7).listIterator(2);
        
        try {
            iter1.set(1000);
            fail();
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iter2.set(1000);
            fail();
        } catch (IllegalStateException ex) {
            
        }
    }
    
    @Test
    public void testSublistListIteratorRemoveAfterAddThrows() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> iter1 = list.subList(2, 10).listIterator(3);
        ListIterator<Integer> iter2 = test.subList(2, 10).listIterator(3);
        
        assertEquals(iter1.next(), iter2.next());
        
        iter1.remove();
        iter2.remove();
        
        iter1.add(101);
        iter2.add(101);
        
        try {
            iter1.remove();
            fail();
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iter2.remove();
            fail();
        } catch (IllegalStateException ex) {
            
        }
        
        eq();
    }
    
    @Test
    public void testSublistDetectsConcurrencyInAdd() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 7);
        List<Integer> sublist2 = test.subList(3, 7);
        
        list.add(10);
        test.add(10);
        
        try {
            sublist1.add(100);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.add(100);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInAddInt() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 7);
        List<Integer> sublist2 = test.subList(3, 7);
        
        list.add(1, 10);
        test.add(1, 10);
        
        try {
            sublist1.add(2, 100);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.add(2, 100);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist1.add(-1, 200);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.add(-1, 200);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.add(5, 200);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.add(5, 200);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInAddAll() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = 0; i < 4; ++i) {
            coll.add(i + 10);
        }
        
        List<Integer> sublist1 = list.subList(2, 5);
        List<Integer> sublist2 = test.subList(2, 5);
        
        list.remove(3);
        test.remove(3);
        
        try {
            sublist1.addAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.addAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInAddAllInt() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = 0; i < 4; ++i) {
            coll.add(i + 10);
        }
        
        List<Integer> sublist1 = list.subList(2, 5);
        List<Integer> sublist2 = test.subList(2, 5);
        
        list.remove(3);
        test.remove(3);
        
        try {
            sublist1.addAll(1, coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.addAll(1, coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist1.addAll(-1, coll);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.addAll(-1, coll);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.addAll(4, coll);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.addAll(4, coll);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInClear() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(2, 7);
        List<Integer> sublist2 = test.subList(2, 7);
        
        list.remove(3);
        test.remove(3);
        
        try {
            sublist1.clear();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.clear();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInContains() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(2, 7);
        List<Integer> sublist2 = test.subList(2, 7);
        
        list.remove(3);
        test.remove(3);
        
        try {
            sublist1.contains(new Integer(3));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.contains(new Integer(3));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInContainsAll() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(2, 7);
        List<Integer> sublist2 = test.subList(2, 7);
        
        list.remove(3);
        test.remove(3);
        
        Collection<Integer> coll = new HashSet<>();
        // If 'coll' is empty, no exceptions should be thrown
        // and return value must be 'true'.
        assertTrue(sublist1.containsAll(coll));
        assertTrue(sublist2.containsAll(coll));
        
        coll.add(9);
        coll.add(6);
        
        try {
            sublist1.containsAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.containsAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInEquals() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(0, 10);
        List<Integer> sublist2 = test.subList(0, 10);
        
        list.add(4, 100);
        test.add(4, 100);
        
        try {
            sublist1.equals(sublist2);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.equals(sublist1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInGet() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(0, 10);
        List<Integer> sublist2 = test.subList(0, 10);
        
        list.add(4, 100);
        test.add(4, 100);
        
        try {
            sublist1.get(4);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.get(4);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist1.get(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.get(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.get(10);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.get(10);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        sublist1 = list.subList(4, 4);
        sublist2 = test.subList(4, 4);
        
        try {
            sublist1.get(0);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.get(0);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInIndexOf() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(0, 10);
        List<Integer> sublist2 = test.subList(0, 10);
        
        list.add(4, 100);
        test.add(4, 100);
        
        Integer num = Integer.valueOf(6);
        
        try {
            sublist1.indexOf(num);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.indexOf(num);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInIsEmpty() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(0, 10);
        List<Integer> sublist2 = test.subList(0, 10);
        
        list.add(4, 100);
        test.add(4, 100);
        
        Integer num = Integer.valueOf(6);
        
        try {
            sublist1.isEmpty();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.isEmpty();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInIterator() {
        for (int i = 0; i < 8; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 7);
        List<Integer> sublist2 = test.subList(3, 7);
        
        list.remove(0);
        test.remove(0);
        
        try {
            sublist1.iterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.iterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInLastIndexOf() {
        for (int i = 0; i < 8; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 7);
        List<Integer> sublist2 = test.subList(1, 7);
        
        list.remove(3);
        test.remove(3);
        
        try {
            sublist1.lastIndexOf(4);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.lastIndexOf(4);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInListIterator() {
        for (int i = 0; i < 8; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 7);
        List<Integer> sublist2 = test.subList(1, 7);
        
        list.remove(2);
        test.remove(2);
        
        try {
            sublist1.listIterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.listIterator();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInListIteratorInt() {
        for (int i = 0; i < 8; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 7);
        List<Integer> sublist2 = test.subList(1, 7);
        
        list.remove(2);
        test.remove(2);
        
        try {
            sublist1.listIterator(-1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.listIterator(-1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        sublist1 = list.subList(1, 5);
        sublist2 = test.subList(1, 5);
        
        try {
            sublist1.listIterator(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.listIterator(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInRemoveInt() {
        for (int i = 0; i < 8; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 8);
        List<Integer> sublist2 = test.subList(1, 8);
        
        list.remove(2);
        test.remove(2);
        
        try {
            sublist1.remove(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.remove(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.remove(7);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.remove(7);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        sublist1 = list.subList(1, 6);
        sublist2 = test.subList(1, 6);
        
        list.add(100);
        test.add(100);
        
        try {
            sublist1.remove(1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.remove(1);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInRemoveObject() {
        for (int i = 0; i < 14; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 10);
        List<Integer> sublist2 = test.subList(3, 10);
        
        list.add(3, 88);
        test.add(3, 88);
        
        try {
            sublist1.remove(new Integer(100));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.remove(new Integer(100));
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInRemoveAll() {
        for (int i = 0; i < 14; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 10);
        List<Integer> sublist2 = test.subList(3, 10);
        
        list.add(2, 88);
        test.add(2, 88);
        
        Collection<Integer> coll = new LinkedList<>();
        
        try {
            sublist1.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        coll.add(100);
        
        try {
            sublist1.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.removeAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInRetainAll() {
        for (int i = 0; i < 14; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 10);
        List<Integer> sublist2 = test.subList(3, 10);
        
        list.add(2, 88);
        test.add(2, 88);
        
        Collection<Integer> coll = new LinkedList<>();
        
        try {
            sublist1.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        coll.add(100);
        
        try {
            sublist1.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.retainAll(coll);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInSet() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(3, 8);
        List<Integer> sublist2 = test.subList(3, 8);
        
        list.remove(3);
        test.remove(3);
        
        try {
            sublist1.set(1, 100);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.set(1, 100);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist1.set(-1, 1000);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.set(-1, 1000);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.set(5, 1000);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.set(5, 1000);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInSize() {
        List<Integer> sublist1 = list.subList(0, 0);
        List<Integer> sublist2 = test.subList(0, 0);
        
        list.add(1000);
        test.add(1000);
        
        try {
            sublist1.size();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.size();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInSublist() {
        for (int i = 0; i < 11; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 10);
        List<Integer> sublist2 = test.subList(1, 10);
        
        list.add(101);
        test.add(101);
        
        sublist1 = sublist1.subList(2, 6);
        sublist2 = sublist2.subList(2, 6);
        
        try {
            sublist1.add(123);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.add(123);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist1.subList(-1, 2);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist2.subList(-1, 2);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        sublist1.subList(1, 4);
        sublist2.subList(1, 4);
        
        try {
            sublist2.subList(1, 5);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.subList(1, 5);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            
        }
        
        try {
            sublist1.subList(3, 2);
            fail();
        } catch (IllegalArgumentException ex) {
            
        }
        
        try {
            sublist2.subList(3, 2);
            fail();
        } catch (IllegalArgumentException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInToArray() {
        for (int i = 0; i < 11; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 10);
        List<Integer> sublist2 = test.subList(1, 10);
        
        list.add(101);
        test.add(101);
        
        try {
            sublist1.toArray();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.toArray();
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistDetectsConcurrencyInToArrayT() {
        for (int i = 0; i < 11; ++i) {
            list.add(i);
            test.add(i);
        }
        
        List<Integer> sublist1 = list.subList(1, 10);
        List<Integer> sublist2 = test.subList(1, 10);
        
        list.add(101);
        test.add(101);
        
        Integer[] array = new Integer[2];
        
        try {
            sublist1.toArray(array);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
        
        try {
            sublist2.toArray(array);
            fail();
        } catch (ConcurrentModificationException ex) {
            
        }
    }
    
    @Test
    public void testSublistBasicIterator() {
        for (int i = 0; i < 26; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Iterator<Integer> sublist1 = list.subList(10, 20).iterator();
        Iterator<Integer> sublist2 = test.subList(10, 20).iterator();
        
        for (int i = 0; i < 5; ++i) {
            assertTrue(sublist1.hasNext());
            assertTrue(sublist2.hasNext());
            
            Integer num = i + 10;
            
            assertEquals(num, sublist1.next());
            assertEquals(num, sublist2.next());
        }
        
        sublist1.remove();
        sublist2.remove();
        
        eq();
        eq(list, test);
        
        for (int i = 0; i < 5; ++i) {
            assertTrue(sublist2.hasNext());
            assertTrue(sublist1.hasNext());
            
            Integer num = i + 15;
            
            assertEquals(num, sublist1.next());
            assertEquals(num, sublist2.next());
        }
        
        eq();
        eq(list, test);
        
        sublist1.remove();
        sublist2.remove();
        
        eq();
        eq(list, test);
    }
    
    @Test
    public void testSublistBasicIteratorShit() {
        for (int i = 0; i < 26; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Iterator<Integer> listIter = list.iterator();
        Iterator<Integer> testIter = test.iterator();
        
        for (int i = 0; i < 5; ++i) {
            assertTrue(listIter.hasNext());
            assertTrue(testIter.hasNext());
            
            Integer num = i;
            
            assertEquals(num, listIter.next());
            assertEquals(num, testIter.next());
        }
        
        listIter.remove();
        testIter.remove();
        
        eq();
        
        for (int i = 0; i < 5; ++i) {
            assertTrue(listIter.hasNext());
            assertTrue(testIter.hasNext());
            
            Integer num = i + 5;
            
            assertEquals(num, listIter.next());
            assertEquals(num, testIter.next());
        }
        
        eq();
        
        listIter.remove();
        testIter.remove();
        
        eq();
    }
    
    @Test
    public void testRemoveRange() {
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        test.remove(2);
        list.removeRange(2, 3);
        
        eq();
        
        list.clear();
        test.clear();
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.removeRange(1, 2);
        test.remove(1);
        
        eq();
        
        list.clear();
        test.clear();
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.removeRange(0, 1);
        test.remove(0);
        
        eq();
        
        list.clear();
        test.clear();
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.removeRange(2, 4);
        test.remove(3);
        test.remove(2);
        
        eq();
        
        list.clear();
        test.clear();
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.removeRange(2, 5);
        test.remove(4);
        test.remove(3);
        test.remove(2);
        
        eq();
        
        list.clear();
        test.clear();
        
        for (int i = 0; i < 20; ++i) {
            list.add(i);
            test.add(i);
        }
        
        list.removeRange(2, 7);
        
        for (int i = 2; i < 7; ++i) {
            test.remove(2);
        }
        
        eq();
        
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        System.out.println("testRemoveRange: seed = " + seed);
        
        for (int op = 0; op < 100; ++op) {
            for (int i = 0; i < 20; ++i) {
                test.add(i);
                list.add(i);
            }
            
            int fromIndex = random.nextInt(list.size());
            int toIndex = fromIndex + random.nextInt(list.size() - fromIndex);
            
            list.removeRange(fromIndex, toIndex);
            
            for (int i = toIndex - 1; i >= fromIndex; --i) {
                test.remove(i);
            }
            
            eq();
            
            test.clear();
            list.clear();
        }
    }
    
    @Test
    public void testSublistToArray() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        assertTrue(Arrays.equals(test.subList(3, 8).toArray(), 
                                 list.subList(3, 8).toArray()));
        
        assertTrue(Arrays.equals(test.subList(3, 3).toArray(), 
                                 list.subList(3, 3).toArray()));
    }
    
    @Test
    public void testSublistToArrayGeneric() {
        for (int i = 0; i < 10; ++i) {
            list.add(i);
            test.add(i);
        }
        
        Integer[] array1 = new Integer[5];
        Integer[] array2 = new Integer[5];
        
        Integer[] ret1;
        Integer[] ret2;
        
        ret1 = list.subList(2, 8).toArray(array1);
        ret2 = test.subList(2, 8).toArray(array2);
        
        assertFalse(ret1 == array1);
        assertEquals(6, ret1.length);
        
        assertFalse(ret2 == array2);
        assertEquals(6, ret2.length);
        
        assertTrue(Arrays.equals(ret1, ret2));
        
        array1 = new Integer[6];
        array2 = new Integer[6];
        
        ret1 = list.subList(2, 8).toArray(array1);
        ret2 = test.subList(2, 8).toArray(array2);
        
        assertTrue(ret1 == array1);
        assertEquals(6, ret1.length);
        
        assertTrue(ret2 == array2);
        assertEquals(6, ret2.length);
        
        assertTrue(Arrays.equals(ret1, ret2));
        
        array1 = new Integer[10];
        array2 = new Integer[10];
        
        ret1 = list.subList(2, 8).toArray(array1);
        ret2 = test.subList(2, 8).toArray(array2);
        
        assertTrue(ret1 == array1);
        assertEquals(10, ret1.length);
        
        assertTrue(ret2 == array2);
        assertEquals(10, ret2.length);
        
        assertTrue(Arrays.equals(ret1, ret2));
        
        assertTrue(ret1[6] == null);
        assertTrue(ret2[6] == null);
    }
    
    @Test
    public void testTinyListIterator() {
        for (int i = 0; i < 5; ++i) {
            list.add(i);
            test.add(i);
        }
        
        ListIterator<Integer> listIter = list.listIterator();
        ListIterator<Integer> testIter = test.listIterator();
        
        for (int i = 0; i < 3; ++i) {
            assertEquals(testIter.next(), listIter.next());
        }
        
        listIter.remove();
        testIter.remove();
        
        eq();
        
        assertEquals(testIter.previous(), listIter.previous());
        
        eq();
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
    public void testToString() {
        for (int i = 0; i < 5; ++i) {
            list.add(i);
            test.add(i);
        }
        
        assertEquals(test.toString(), list.toString());
        
        list.clear();
        test.clear();
        
        assertEquals(test.toString(), list.toString());
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
            Integer testInt = itTest.next();
            Integer listInt = itList.next();
            assertEquals(testInt, listInt);
        }
        
        assertFalse(itList.hasNext());
        
        ListIterator<Integer> itList2 = list.listIterator();
        ListIterator<Integer> itTest2 = test.listIterator();
        
        while (itTest2.hasNext()) {
            assertTrue(itList2.hasNext());
            Integer testInt = itTest2.next();
            Integer listInt = itList2.next();
            assertEquals(testInt, listInt);
            
            assertEquals(itTest2.previousIndex(), itList2.previousIndex());
            assertEquals(itTest2.nextIndex(), itList2.nextIndex());
        }
        
        assertFalse(itList2.hasNext());
        
        while (itTest2.hasPrevious()) {
            assertTrue(itList2.hasPrevious());
            Integer testInt = itTest2.previous();
            Integer listInt = itList2.previous();
            assertEquals(testInt, listInt);
            
            assertEquals(itTest2.previousIndex(), itList2.previousIndex());
            assertEquals(itTest2.nextIndex(), itList2.nextIndex());
        }
        
        assertFalse(itList2.hasPrevious());
        list.checkHealth();
    }
    
    private void eq(List<Integer> list, List<Integer> test) {
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
    
    private static void title(String s) {
        StringBuilder sb = new StringBuilder(80);
        int textAreaLength = s.length() + 2;
        int leftBarLength = (80 - textAreaLength) / 2;
        int rightBarLength = 80 - textAreaLength - leftBarLength;
        
        for (int i = 0; i < leftBarLength; ++i) {
            sb.append("*");
        }
        
        sb.append(" ").append(s).append(" ");
        
        for (int i = 0; i < rightBarLength; ++i) {
            sb.append("*");
        }
        
        System.out.println(sb.toString());
    }
}
