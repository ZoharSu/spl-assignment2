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
                    inFlight.decrementAndGet();
                    idleMinHeap.add(thread);
                }
            });
        } catch(InterruptedException e) {
            try {
                shutdown();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for (Runnable task : tasks)
            submit(task);
    }

    // TODO
    // WHY DOES THIS METHOD THROW INTERRUPTED EXCEPTION??
    public void shutdown() throws InterruptedException {
        for (TiredThread worker : workers) {
            // Should we use the alive field?
            if (worker.getAlive())
                worker.shutdown();
        }
    }

    // WHY IS THIS SYNCHRONIZED?
    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String ret = "";
        for (TiredThread t : workers) {
            ret.concat(
                "Id: " + t.getWorkerId() + "\n" +
                "Fatigue:" + t.getFatigue() + "\n" +
                "Busy:" + t.isBusy() + "\n" +
                "Time used:" + t.getTimeUsed() + "\n" +
                "Time idle:" + t.getTimeIdle() + "\n\n"
            );
        }
        return null;
    }
}