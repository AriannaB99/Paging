import java.util.Random;
import  java.lang.Math;
import java.lang.*;
import java.lang.Runtime;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.lang.Runtime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Assn6 {
    private Random random = new Random();

    //Defining all of our constants for the program
    public final int PAGE_SEQUENCE_LENGTH = 1000;
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public final int PAGE_MAX = 250;
    public final int MAIN_MEM_FRAMES_MAX = 100;
    public final int NUM_SIMS = 1000;


    public int [][] FIFO_page_faults = new int [this.NUM_SIMS][this.MAIN_MEM_FRAMES_MAX+1];
    public int [][] LRU_page_faults = new int [this.NUM_SIMS][this.MAIN_MEM_FRAMES_MAX+1];
    public int [][] MRU_page_faults = new int [this.NUM_SIMS][this.MAIN_MEM_FRAMES_MAX+1];

    //method to randomly generate our page sequence, from 1-250
    public int [] page_sequence() {
        int [] pages = new int [PAGE_SEQUENCE_LENGTH];
        for (int i = 0; i < PAGE_SEQUENCE_LENGTH; i++) {
            pages[i] = random.nextInt(PAGE_MAX);
            pages[i] = pages[i] +1;
        }
            return pages;
    }

    public static void main (String args[]) {
        Assn6 a = new Assn6();
        ExecutorService executor = Executors.newFixedThreadPool(a.CPU_COUNT);

        long start_time  = System.currentTimeMillis();

        for (int i = 0; i < a.NUM_SIMS; i++) {
            int [] page_reference_sequence = a.page_sequence();

            for (int j = 1; j <= a.MAIN_MEM_FRAMES_MAX; j++) {
                TaskFIFO f = new TaskFIFO(page_reference_sequence, j, a.FIFO_page_faults, i);
                TaskLRU l = new TaskLRU(page_reference_sequence, j, a.LRU_page_faults, i);
                TaskMRU m = new TaskMRU(page_reference_sequence, j, a.MRU_page_faults, i);
                executor.execute(f);
                executor.execute(l);
                executor.execute(m);
            }
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(100000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long end_time = System.currentTimeMillis() - start_time;

        System.out.println("Simulation took " + end_time +" ms");
        System.out.println();

        int MRU_min = 0;
        int LRU_min = 0;
        int FIFO_min = 0;

        //Looping through our matrix to construct where each simulation was the minimum
        for (int row = 0; row < a.FIFO_page_faults.length; row++) {
            for (int col = 1; col < a.FIFO_page_faults[row].length; col++) {
                if ((a.FIFO_page_faults[row][col] < a.LRU_page_faults[row][col]) && (a.FIFO_page_faults[row][col] < a.MRU_page_faults[row][col])) {
                    FIFO_min++;
                }

                else if ((a.MRU_page_faults[row][col] < a.LRU_page_faults[row][col]) && (a.MRU_page_faults[row][col] < a.FIFO_page_faults[row][col])) {
                    MRU_min++;
                }
                else if ((a.LRU_page_faults[row][col] < a.MRU_page_faults[row][col]) && (a.LRU_page_faults[row][col] < a.FIFO_page_faults[row][col])) {
                    LRU_min++;
                }

                else {
                    FIFO_min++;
                    MRU_min++;
                    LRU_min++;
                }

            }
        }

        //Reporting where each simulation had the minimum
        System.out.println("FIFO min PF : " + FIFO_min);
        System.out.println("LRU min PF : " + LRU_min);
        System.out.println("MRU min PF : " + MRU_min);

        System.out.println();

        int anomaly_count = 0;
        int max_diff = 0;

        //Reporting the results for FIFO
       System.out.println("Belady's Anomaly Report for FIFO");
        for (int row = 0; row < a.FIFO_page_faults.length; row++) {
            for (int col = 2; col < a.FIFO_page_faults[row].length; col++) {
                if (a.FIFO_page_faults[row][col] > a.FIFO_page_faults[row][col-1]) {
                    int diff = a.FIFO_page_faults[row][col] - a.FIFO_page_faults[row][col-1];
                    if (diff > max_diff) {
                        max_diff = diff;
                    }
                    if (diff > 0) {
                        System.out.println("detected - Previous " + a.FIFO_page_faults[row][col - 1] + " : Current " + a.FIFO_page_faults[row][col] + " (" + diff + ")");
                        anomaly_count++;
                    }
                }
            }
        }
        System.out.println("Anomaly detected " + anomaly_count + " times with a max difference of " + max_diff);

        int lru_anomaly_count = 0;
        int lru_max_diff = 0;

        System.out.println();
        //Reporting the results for LRU
        System.out.println("Belady's Anomaly Report for LRU");
        for (int row = 0; row < a.LRU_page_faults.length; row++) {
            for (int col = 2; col < a.LRU_page_faults[row].length; col++) {
                //System.out.println(">");
                if (a.LRU_page_faults[row][col] > a.LRU_page_faults[row][col-1]) {
                    //System.out.println("iteration " + row + " memory " + col);
                    int diff = a.LRU_page_faults[row][col] - a.LRU_page_faults[row][col-1];
                    if (diff > lru_max_diff) {
                        lru_max_diff = diff;
                    }
                    if (diff > 0) {
                        System.out.println("detected - Previous " + a.LRU_page_faults[row][col - 1] + " : Current " + a.LRU_page_faults[row][col] + " (" + diff + ")");
                        lru_anomaly_count++;
                    }
                }
            }
        }
        System.out.println("Anomaly detected " + lru_anomaly_count + " times with a max difference of " + lru_max_diff);

        int mru_anomaly_count = 0;
        int mru_max_diff = 0;

        System.out.println();
        //Reporting the results for MRU
        System.out.println("Belady's Anomaly Report for MRU");
        for (int row = 0; row < a.MRU_page_faults.length; row++) {
            for (int col = 2; col < a.MRU_page_faults[row].length; col++) {
                //System.out.println(">");
                if (a.MRU_page_faults[row][col] > a.MRU_page_faults[row][col-1]) {
                    //System.out.println("iteration " + row + " memory " + col);
                    int diff = a.MRU_page_faults[row][col] - a.MRU_page_faults[row][col-1];
                    if (diff > lru_max_diff) {
                        mru_max_diff = diff;
                    }
                    if (diff > 0) {
                        System.out.println("detected - Previous " + a.MRU_page_faults[row][col - 1] + " : Current " + a.MRU_page_faults[row][col] + " (" + diff + ")");
                        mru_anomaly_count++;
                    }
                }
            }
        }
        System.out.println("Anomaly detected " + mru_anomaly_count + " times with a max difference of " + mru_max_diff);

    }
}