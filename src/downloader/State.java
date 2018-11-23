package downloader;

/**
 * Created by HerrSergio on 04.08.2016.
 */
public class State {
    private int linksInProcessCount;
    private int linksDetectedCount;
    private  boolean paused;
    private long timeElapsed;

    public State(int linksInProcessCount, int linksDetectedCount, boolean paused, long timeElapsed) {
        this.linksInProcessCount = linksInProcessCount;
        this.linksDetectedCount = linksDetectedCount;
        this.paused = paused;
        this.timeElapsed = timeElapsed;
    }

    public int getLinksInProcessCount() {
        return linksInProcessCount;
    }

    public void setLinksInProcessCount(int linksInProcessCount) {
        this.linksInProcessCount = linksInProcessCount;
    }

    public int getLinksDetectedCount() {
        return linksDetectedCount;
    }

    public void setLinksDetectedCount(int linksDetectedCount) {
        this.linksDetectedCount = linksDetectedCount;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    @Override
    public String toString() {
        return "State{" +
                "linksInProcessCount=" + linksInProcessCount +
                ", linksDetectedCount=" + linksDetectedCount +
                ", paused=" + paused +
                ", timeElapsed=" + timeElapsed +
                '}';
    }
}
