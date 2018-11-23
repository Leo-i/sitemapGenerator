package threadpool;

/**
 * Created by HerrSergio on 31.07.2016.
 */
class PoolThread extends Thread {

    private final ThreadPool threadPool;

    PoolThread(ThreadPool threadPool) {
        this.threadPool = threadPool;
        setDaemon(true);
    }

    @Override
    public void run() {
        //super.run();
        Runnable task = null;
        while(!interrupted()) {
            try {
                synchronized (threadPool) {
                    while (true) {
                        task = threadPool.getTask();
                        if (task != null)
                            break;
                        threadPool.wait();
                    }
                }

                task.run();

            } catch (InterruptedException e) {
                interrupt();
            } catch (Throwable w) {

            }
        }
    }
}
