package scheduling;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TiredThread extends Thread implements Comparable<TiredThread> {

    private static final Runnable POISON_PILL = () -> {}; // Special task to signal shutdown

    private final int id; // Worker index assigned by the executor
    private final double fatigueFactor; // Multiplier for fatigue calculation

    private final AtomicBoolean alive = new AtomicBoolean(true); // Indicates if the worker should keep running

    // Single-slot handoff queue; executor will put tasks here
    private final BlockingQueue<Runnable> handoff = new ArrayBlockingQueue<>(1);

    private final AtomicBoolean busy = new AtomicBoolean(false); // Indicates if the worker is currently executing a task

    private final AtomicLong timeUsed = new AtomicLong(0); // Total time spent executing tasks
    private final AtomicLong timeIdle = new AtomicLong(0); // Total time spent idle
    private final AtomicLong idleStartTime = new AtomicLong(0); // Timestamp when the worker became idle

    public TiredThread(int id, double fatigueFactor) {
        this.id = id;
        this.fatigueFactor = fatigueFactor;
        this.idleStartTime.set(System.nanoTime());
        setName(String.format("FF=%.2f", fatigueFactor));
    }

    public int getWorkerId() {
        return id;
    }

    public double getFatigue() {
        return fatigueFactor * timeUsed.get();
    }

    public boolean isBusy() {
        return busy.get();
    }

    public long getTimeUsed() {
        return timeUsed.get();
    }

    public long getTimeIdle() {
        return timeIdle.get();
    }

    public boolean getAlive() {
        return alive.get();
    }

    /**
     * Assign a task to this worker.
     * This method is non-blocking: if the worker is not ready to accept a task,
     * it throws IllegalStateException.
     */
    public void newTask(Runnable task) {
        if (!handoff.offer(task))
            throw new IllegalStateException("Thread isn't ready to accept a new task");
    }

    /**
     * Request this worker to stop after finishing current task.
     * Inserts a poison pill so the worker wakes up and exits.
     */
    public void shutdown() {
        try {
            handoff.put(POISON_PILL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (alive.get()) {
            try {
                // TODO
                // is the placement of time field updates correct?
                Runnable task = handoff.take();
                timeIdle.addAndGet(System.nanoTime() - idleStartTime.get());
                if (task == POISON_PILL)
                    alive.set(false);
                else {
                    busy.set(true);
                    long usedStartTime = System.nanoTime();
                    try {
                        task.run();
                    } catch (Exception e) {
                        // REMOVE THIS, ONLY FOR DEBUGGING PURPOSES
                        e.printStackTrace();
                    } finally {
                        timeUsed.addAndGet(System.nanoTime() - usedStartTime);
                        idleStartTime.set(System.nanoTime());
                        busy.set(false);
                    }
                }
            } catch (InterruptedException e) {
                timeIdle.addAndGet(System.nanoTime() - idleStartTime.get());
                alive.set(false);
            }
        }
    }

    @Override
    public int compareTo(TiredThread o) {
        if (o == null)
            throw new IllegalArgumentException("Thread is null");

        // TODO
        // Is there a different way lol?
        // can't just do fatigue - otherFatigue
        double fatigue = getFatigue();
        double otherFatigue = o.getFatigue();
        return (fatigue < otherFatigue ? -1 :
            (fatigue == otherFatigue ? 0 : 1));
    }
}
