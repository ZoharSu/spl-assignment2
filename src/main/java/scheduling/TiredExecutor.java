package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            TiredThread t = new TiredThread(i, Math.random() + 0.5);
            workers[i] = t;
            idleMinHeap.add(t);
            t.start();
        }
    }

    public void submit(Runnable task) {
        try {
            TiredThread thread = idleMinHeap.take();
            inFlight.incrementAndGet();
            thread.newTask(() -> {
                try {
                    task.run();
                } finally {
                    idleMinHeap.add(thread);
                    // TODO: what do you think?
                    if (inFlight.decrementAndGet() == 0) {
                        synchronized(inFlight) {
                            inFlight.notifyAll();
                        }
                    }
                }
            });
        } catch(InterruptedException e) {
            // TODO: remove this
            // try {
            //     // Shutdown, though shouldn't get here
            //     shutdown();
            // } catch (InterruptedException e1) {
            //     e1.printStackTrace();
            // }
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for (Runnable task : tasks)
            submit(task);

        synchronized(inFlight) {
            while (inFlight.get() > 0) {
                try {
                    inFlight.wait();
                } catch (InterruptedException e) {}
            }
        }
    }

    // TODO
    // WHY DOES THIS METHOD THROW INTERRUPTED EXCEPTION??
    public void shutdown() throws InterruptedException {
        for (TiredThread worker : workers) {
            if (worker.getAlive())
                worker.shutdown();
        }

        // TODO: I think this is the way
        for (TiredThread worker : workers)
            if (worker.getAlive())
                worker.join();
    }

    public synchronized String getWorkerReport() {
        if (workers.length == 0)
            return "";

        StringBuilder ret = new StringBuilder();
        double avgFatigue = 0;
        double[] fatigueArr = new double[workers.length];

        for (TiredThread t : workers) {
            double fatigue = t.getFatigue();
            fatigueArr[t.getWorkerId()] = fatigue;
            avgFatigue += fatigue;
            ret.append(
                "------------------------------"    + "\n" +
                "Id: "          + t.getWorkerId()   + "\n" +
                "Fatigue: "     + fatigue           + "\n" +
                "Busy: "        + t.isBusy()        + "\n" +
                "Time used: "   + t.getTimeUsed()   + "\n" +
                "Time idle: "   + t.getTimeIdle()   + "\n"
            );
        }

        avgFatigue /= workers.length;
        double fairness = 0;
        
        for (TiredThread t : workers)
            fairness += Math.pow(fatigueArr[t.getWorkerId()] - avgFatigue, 2);

        ret.append(
            "------------------------------"    + "\n" +
            "Fairness: " + fairness             + "\n" +
            "------------------------------"    + "\n"
        );
        return ret.toString();
    }
}
