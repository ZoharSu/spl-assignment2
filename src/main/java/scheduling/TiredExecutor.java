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
        for (int i = 0; i < numThreads; i++)
            workers[i] = new TiredThread(i, 0);
        idleMinHeap.addAll(List.of(workers));
    }

    public void submit(Runnable task) {
        try {
            TiredThread thread = idleMinHeap.take();
            thread.newTask(task);
            int old, newVal;
            do {
                old = inFlight.get();
                newVal = old+1;
            } while (!inFlight.compareAndSet(old,newVal));
        } catch(InterruptedException e) {
            // TODO: DO THIS NOW TODO
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for (Runnable task : tasks)
            submit(task);
    }

    public void shutdown() throws InterruptedException {
        // TODO
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        return null;
    }
}
