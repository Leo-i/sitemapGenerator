package threadpool;

import java.util.ArrayList;

/**
 * Created by HerrSergio on 31.07.2016.
 */
public class ThreadPool implements AutoCloseable {

    private ArrayList<Runnable> tasks = new ArrayList<>();
    private PoolThread[] threads;
    private boolean paused;

    public ThreadPool(int threadCount) {
        threads = new PoolThread[threadCount];
        for(int i = 0; i < threadCount; i++) {
            threads[i] = new PoolThread(this);
            threads[i].start();
        }
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void setPaused(boolean paused) {
        if(this.paused != paused) {
            this.paused = paused;
            if(!paused) {
                if(tasks.size() == 1)
                    notify();
                else if(tasks.size() > 1)
                    notifyAll();
            }
        }
    }

    Runnable getTask() {
        if(this.paused)
            return null;
        else if(tasks.size() > 0)
            return tasks.remove(0);
        else
            return null;
    }

    public synchronized void addTask(Runnable task) {
        if(task != null){
            tasks.add(task);
            if(!paused)
                this.notify();
        }
    }

    public boolean isDisposed() {
        return threads == null;
    }

    @Override
    public void close() throws Exception {
        if(threads != null) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            threads = null;
        }
    }
}
