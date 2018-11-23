package downloader;

import threadpool.ThreadPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by HerrSergio on 31.07.2016.
 */
public class Data implements AutoCloseable {
    private Map<String, Set<String>> urlMap = new HashMap<>();
    private int inProcessCount;
    private ThreadPool threadPool;
    private long timeCorrection;


    public Data(int threadCount, String url) {
        threadPool = new ThreadPool(threadCount);
        timeCorrection -= System.currentTimeMillis();
        synchronized (urlMap) {
            addUrl(url);
        }

    }

    public boolean isDone() {
        synchronized (urlMap) {
            return inProcessCount == 0;
        }
    }

    public void waitUntilDone() throws InterruptedException {
        synchronized (urlMap) {
            while (inProcessCount > 0)
                urlMap.wait();
        }
    }

    public State getState() {
        synchronized (urlMap) {
            long timeElapsed = timeCorrection;
            if(!threadPool.isPaused())
                timeElapsed += System.currentTimeMillis();
            boolean paused = isPaused();
            return new State(inProcessCount, urlMap.size(), paused, timeElapsed);
        }
    }

    @Override
    public void close() {
        try {
            threadPool.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public Map<String, Set<String>> getUrlMap() {
        synchronized (urlMap) {
            if (inProcessCount > 0 && !threadPool.isDisposed())
                throw new IllegalStateException();
            return urlMap;
        }
    }

    /*public Map<String, Set<String>> getUrlMapCopy() {
        synchronized (urlMap) {
            return new HashMap<>(urlMap);
        }
    }*/

    Map<String, Set<String>> getUrlMapInternal() {
        return urlMap;
    }

    void addUrl(String url) {
        threadPool.addTask(new Task(this, url));
        urlMap.put(url, null);
        inProcessCount++;
    }

    void urlDone(String url, Set<String> urls) {
        if (urlMap.containsKey(url) && urlMap.get(url) == null) {
            inProcessCount--;
            urlMap.notifyAll();
            urlMap.put(url, urls);
        } else {
            throw new IllegalArgumentException();
        }
    }

    Object getLock() {
        return urlMap;
    }

    public boolean isPaused() {
        return threadPool.isPaused();
    }

    public synchronized void setPaused(boolean paused) {
        if(threadPool.isPaused() != paused) {
            threadPool.setPaused(paused);
            if(paused)
                timeCorrection += System.currentTimeMillis();
            else
                timeCorrection -= System.currentTimeMillis();
        }
    }
}
