import java.util.LinkedList;
import java.util.*;
import java.lang.Integer;

public class TaskLRU implements Runnable {
    protected Set<Integer> current_pages = new HashSet<Integer>();
    protected LinkedList <Integer> page_table = new LinkedList<>() ;
    private int page_faults_count = 0;
    private int page_faults [][];
    private int iteration = 0;
    private int mem_capacity = 0;
    private int page_sequence [];

    public TaskLRU(int[] sequence, int maxMemoryFrames,  int[][] pageFaults, int iteration) {
        this.page_sequence = sequence;
        this.mem_capacity = maxMemoryFrames;
        this.page_faults = pageFaults;
        this.iteration = iteration;

    }

    //Method where we are going to check if our page is in the frames
    public void update_frames(Integer page) {
        //if our frames are not full
        if (this.current_pages.size() < this.mem_capacity) {
            //if our page is not in the current frames
            if (!this.current_pages.contains(page)) {
                this.current_pages.add(page);
                this.add_faults();
                this.page_table.addFirst(page);
            }
            //if it is, move it to the front of the list
            else {
                int index = this.page_table.indexOf(page);
                this.page_table.remove(index);
                this.page_table.addFirst(page);
            }
        } else {
            //if our page is not in the current frames
            if (!this.current_pages.contains(page)) {
                //remove the oldest task, and put our new one on the front
                Integer oldest = this.page_table.removeLast();
                this.current_pages.remove(oldest);
                this.current_pages.add(page);
                this.page_table.addFirst(page);
                this.add_faults();
            }
            //if it is, move it to the front of the list
            else {
                int index = this.page_table.indexOf(page);
                this.page_table.remove(index);
                this.page_table.addFirst(page);
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
    }

    //Method to update the number of page faults we have
    public void add_faults() {
        this.page_faults_count++;
    }
}