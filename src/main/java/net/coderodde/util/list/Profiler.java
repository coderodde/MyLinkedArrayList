package net.coderodde.util.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.stream.StreamSupport;

/**
 * This class demonstrates performance of {@link java.util.ArrayList},
 * {@link java.util.LinkedList}, 
 * {@link net.coderodde.util.list.LinkedArrayList} and ...
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6
 */
public class Profiler {
    
    private static final int ADD_N = 1000000;
    private static final int ADD_INT_N = 500;
    private static final int DEQUE_N = 2000;
    private static final int REMOVE_INT_N = 1000;
    private static final int CONTAINS_ALL_N = 1000;
    
    public static void main(String[] args) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new LinkedList<>();
        List<Integer> list3 = 
                new LinkedArrayList<>(128, LinkedArrayList.NodeType.TRIVIAL);
        List<Integer> list4 = new EnemyTreeList<>();
        
        long seed = System.currentTimeMillis();
        
        System.out.println("Seed: " + seed);
        
        profile(list1, seed);
        profile(list2, seed);
        profile(list3, seed);
        profile(list4, seed);
        
        title("End of profiling");
        
        System.out.println("Lists have same content: " + equals(list1,
                                                                list2,
                                                                list3,
                                                                list4));
    }
    
    private static void profile(List<Integer> list, long seed) {
        list.clear();
        
        if (list instanceof LinkedArrayList) {
            title(list.getClass().getSimpleName() + ", degree " +
                  ((LinkedArrayList) list).getDegree());
        } else {
            title(list.getClass().getSimpleName());
        }
        
        Random random = new Random(seed);
        
        long totalDuration = 0L;
        
        totalDuration += profileAdd(list, random);
        totalDuration += profileAddInt(list, random);
        totalDuration += profileIteration(list);
        totalDuration += profileRemoveInt(list, random);
        totalDuration += profileContainsAll(list, random);
        totalDuration += profileAddFirst(list, random);
        totalDuration += profileRemoveFirst(list, random);
        totalDuration += profileAddLast(list, random);
        totalDuration += profileRemoveLast(list, random);
        totalDuration += profileSequentialStream(list);
        totalDuration += profileParallelStream(list);
        
        System.out.println("Total duration: " + totalDuration);
    }
    
    private static final long profileAdd(List<Integer> list, Random random) {
        long ta = System.currentTimeMillis();
        
        for (int i = 0; i < ADD_N; ++i) {
            list.add(random.nextInt());
        }
        
        long tb = System.currentTimeMillis();
        
        System.out.println("add(E) in " + (tb - ta) + " ms.");
        
        return tb - ta;
    }
    
    private static final long profileAddFirst(List<Integer> list,
                                              Random random) {
        if (list instanceof Deque) {
            Deque<Integer> d = (Deque<Integer>) list;
            long ta = System.currentTimeMillis();

            for (int i = 0; i < DEQUE_N; ++i) {
                d.addFirst(random.nextInt(list.size() + 1));
            }

            long tb = System.currentTimeMillis();

            System.out.println("addFirst(E) in " + (tb - ta) + " ms.");

            return tb - ta;
        }
        
        long ta = System.currentTimeMillis();

        for (int i = 0; i < DEQUE_N; ++i) {
            list.add(0, random.nextInt(list.size() + 1));
        }

        long tb = System.currentTimeMillis();

        System.out.println("addFirst(E) in " + (tb - ta) + " ms.");

        return tb - ta;
    }
    
    private static final long profileRemoveFirst(List<Integer> list,
                                              Random random) {
        if (list instanceof Deque) {
            Deque<Integer> d = (Deque<Integer>) list;
            long ta = System.currentTimeMillis();

            for (int i = 0; i < DEQUE_N; ++i) {
                d.removeFirst();
            }

            long tb = System.currentTimeMillis();

            System.out.println("removeFirst() in " + (tb - ta) + " ms.");

            return tb - ta;
        }
        
        long ta = System.currentTimeMillis();

        for (int i = 0; i < DEQUE_N; ++i) {
            list.remove(0);
        }

        long tb = System.currentTimeMillis();

        System.out.println("removeFirst() in " + (tb - ta) + " ms.");

        return tb - ta;
    }
    
    private static final long profileAddLast(List<Integer> list,
                                              Random random) {
        if (list instanceof Deque) {
            Deque<Integer> d = (Deque<Integer>) list;
            long ta = System.currentTimeMillis();

            for (int i = 0; i < DEQUE_N; ++i) {
                d.addLast(random.nextInt(list.size() + 1));
            }

            long tb = System.currentTimeMillis();

            System.out.println("addLast(E) in " + (tb - ta) + " ms.");

            return tb - ta;
        }
        
        long ta = System.currentTimeMillis();

        for (int i = 0; i < DEQUE_N; ++i) {
            list.add(i);
        }

        long tb = System.currentTimeMillis();

        System.out.println("addLast(E) in " + (tb - ta) + " ms.");

        return tb - ta;
    }
    
    private static final long profileRemoveLast(List<Integer> list,
                                              Random random) {
        if (list instanceof Deque) {
            Deque<Integer> d = (Deque<Integer>) list;
            long ta = System.currentTimeMillis();

            for (int i = 0; i < DEQUE_N; ++i) {
                d.removeLast();
            }

            long tb = System.currentTimeMillis();

            System.out.println("removeLast() in " + (tb - ta) + " ms.");

            return tb - ta;
        }
        
        long ta = System.currentTimeMillis();

        for (int i = 0; i < DEQUE_N; ++i) {
            list.remove(list.size() - 1);
        }

        long tb = System.currentTimeMillis();

        System.out.println("removeLast() in " + (tb - ta) + " ms.");

        return tb - ta;
    }
    
    private static final long profileSequentialStream(List<Integer> list) {
        long ta = System.currentTimeMillis();
        StreamSupport.stream(list.spliterator(), false).reduce((a, b) -> a);
        long tb = System.currentTimeMillis();
        
        System.out.println("Sequential stream in " + (tb - ta) + " ms.");
        
        return tb - ta;
    }
    
    private static final long profileParallelStream(List<Integer> list) {
        long ta = System.currentTimeMillis();
        StreamSupport.stream(list.spliterator(), true).reduce((a, b) -> b);
        long tb = System.currentTimeMillis();
        
        System.out.println("Parallel stream in " + (tb - ta) + " ms.");
        
        return tb - ta;
    }
    
    private static final long profileAddInt(List<Integer> list, Random random) {
        long ta = System.currentTimeMillis();
        
        for (int i = 0; i < ADD_INT_N; ++i) {
            list.add(random.nextInt(list.size() + 1), i);
        }
        
        long tb = System.currentTimeMillis();
        
        System.out.println("add(int, E) in " + (tb - ta) + " ms.");
        
        return tb - ta;
    }
    
    private static final long profileIteration(List<Integer> list) {
        long duration = 0L;
        long ta = System.currentTimeMillis();
        
        for (Integer i : list) {
            
        }
        
        long tb = System.currentTimeMillis();
        
        System.out.println("Iterator in " + (tb - ta) + " ms.");
        duration += tb - ta;
        
        ta = System.currentTimeMillis();
        
        ListIterator<Integer> iter = list.listIterator();
        
        while (iter.hasNext()) {
            iter.next();
        }
        
        tb = System.currentTimeMillis();
        duration += tb - ta;
        
        System.out.println("ListIterator forward in " + (tb - ta) + " ms.");
        
        ta = System.currentTimeMillis();
        
        while (iter.hasPrevious()) {
            iter.previous();
        }
        
        tb = System.currentTimeMillis();
        duration += tb - ta;
        
        System.out.println("ListIterator backward in " + (tb - ta) + " ms.");
        
        return duration;
    }
    
    private static final long profileRemoveInt(List<Integer> list, 
                                               Random random) {
        long ta = System.currentTimeMillis();
        
        for (int i = 0; i < REMOVE_INT_N; ++i) {
            list.remove(random.nextInt(list.size()));
        }
        
        long tb = System.currentTimeMillis();
        
        System.out.println("remove(int) in " + (tb - ta) + " ms.");
        
        return tb - ta;
    }
    
    private static final long profileContainsAll(List<Integer> list, 
                                                 Random random) {
        Collection<Integer> coll = new ArrayList<>();
        
        for (int i = 0; i < CONTAINS_ALL_N; ++i) {
            Integer tmp = random.nextInt();
            coll.add(tmp);
            list.add(tmp);
        }
            
        long ta = System.currentTimeMillis();
        
        boolean result = list.containsAll(coll);
        
        long tb = System.currentTimeMillis();
        
        System.out.println("containsAll(Collection) in " + (tb - ta) + 
                           " ms. Result: " + result);
        
        return tb - ta;
    }
    
    private static final void title(String s) {
        StringBuilder sb = new StringBuilder(81);
        
        int titleLength = s.length() + 2;
        int left = (80 - titleLength) / 2;
        int right = 80 - left - titleLength;
        
        for (int i = 0; i < left; ++i) {
            sb.append("-");
        }
        
        sb.append(" ").append(s).append(" ");
        
        for (int i = 0; i < right; ++i) {
            sb.append("-");
        }
        
        System.out.println(sb.toString());
    }
    
    private static final boolean equals(List<Integer>... lists) {
        if (lists.length < 2) {
            return true;
        }
        
        for (int i = 0; i < lists.length - 1; ++i) {
            if (lists[i].size() != lists[i + 1].size()) {
                return false;
            }
        }
        
        Iterator[] iterators = new Iterator[lists.length];
        Integer[] ints = new Integer[lists.length];
        
        for (int i = 0; i < lists.length; ++i) {
            iterators[i] = lists[i].iterator();
        }
        
        for (int i = 0; i < lists[0].size(); ++i) {
            for (int j = 0; j < iterators.length; ++j) {
                ints[j] = (Integer) iterators[j].next();
            }
            
            for (int j1 = 0; j1 < ints.length; ++j1) {
                for (int j2 = 0; j2 < ints.length; ++j2) {
                    if (!Objects.equals(ints[j1], ints[j2])) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
}
