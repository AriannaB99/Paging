public class testLRU {
    public void test() {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[][] pageFaults = new int[4][4];  // 4 because maxMemoryFrames is 3

        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskLRU(sequence1, 1, pageFaults, 1)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1][1]);

        // Replacement should be: 2, 1, 3, 1, 2
        // Page Faults should be 7
        (new TaskLRU(sequence2, 2,  pageFaults, 1)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1][2]);

        // Replacement should be: 1
        // Page Faults should be 4
        (new TaskLRU(sequence2, 3,  pageFaults, 1)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1][3]);
    }
}
