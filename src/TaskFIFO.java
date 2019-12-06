import java.util.LinkedList;
import java.util.*;
import java.lang.Integer;


//so we are going to pass in 1000 page numbers, from 1-250
//we are going to try to access these from our 100 memory spots

public class TaskFIFO implements Runnable {
    protected Set<Integer> current_pages = new HashSet<Integer>();
    protected Queue<Integer> page_table = new LinkedList<>() ;
    private int page_faults_count = 0;
    private int page_faults [][];
    private int iteration = 0;
    private int mem_capacity = 0;
    private int page_sequence [];

    public TaskFIFO(int[] sequence, int maxMemoryFrames,  int[][] pageFaults, int iteration) {
        this.page_sequence = sequence;
        this.mem_capacity = maxMemoryFrames;
        this.page_faults = pageFaults;
        this.iteration = iteration;

    }

    //Method where we are going to check if our page is in the frames
    public void update_frames(Integer page) {
        //if not all of our frames are full
        if (this.current_pages.size() < this.mem_capacity) {
            //if we don't have the current page in our queue
            if (!this.current_pages.contains(page)) {
                this.current_pages.add(page);
                this.add_faults();
                this.page_table.add(page);
            }
        }
        //if all of our frames are full, take out the oldest one, then stick our new one at the end
        else {
            if (!this.current_pages.contains(page)) {
                Integer oldest = this.page_table.poll();
                this.current_pages.remove(oldest);
                this.current_pages.add(page);
                this.page_table.add(page);
                this.add_faults();
            }

        }
    }

    //Method for the threads to run, calls update_frames
    public void run() {
        for (int i = 0; i < this.page_sequence.length; i++) {
            Integer page = 0;
            page  = page.valueOf(this.page_sequence[i]);
            this.update_frames(page);
        }
        this.page_faults[this.iteration][this.mem_capacity] = this.page_faults_count;

        if (this.page_faults_count == 0) {
            System.out.println("Iteration " + this.iteration + " Mem " + this.mem_capacity + " Page fault 0");
        }
    }

    //Method to update the number of page faults we have
    public void add_faults() {
        this.page_faults_count++;
    }
}