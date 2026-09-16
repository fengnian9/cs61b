package bstmap;

import edu.princeton.cs.algs4.Stopwatch;

public class FengnianTestBSTMap {



    public static double insertInOrder(Map61B<String, Integer> map61B, int N) {
        Stopwatch sw = new Stopwatch();
        String s = "cat";
        for (int i = 0; i < N; i++) {
            s = StringUtils.nextString(s);
            map61B.put(s, new Integer(i));
        }
        return sw.elapsedTime();
    }

    public static void main(String[] args) {

        BSTMap treeMap = new BSTMap<> ();

        insertInOrder(treeMap, 10000);

        treeMap.printInOrder();
    }
}
