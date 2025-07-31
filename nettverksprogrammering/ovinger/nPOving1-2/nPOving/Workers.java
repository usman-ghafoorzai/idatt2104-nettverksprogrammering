import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.*;

public class Workers {
  private final List<Runnable> taskQueue = new LinkedList<>();
  private final Thread[] workerThreads;
  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private volatile boolean running = true;

  public Workers(int numThreads) {
    workerThreads = new Thread[numThreads];

    // Opprett og start worker-tråder
    for (int i = 0; i < numThreads; i++) {
      workerThreads[i] = new Thread(this::workerLoop);
      workerThreads[i].start();
    }
  }

  private void workerLoop() {
    while (running) {
      Runnable task = null;
      lock.lock();
      try {
        while (taskQueue.isEmpty() && running) {
          condition.await(); // Vent på nye oppgaver
        }
        if (!taskQueue.isEmpty()) {
          task = taskQueue.removeFirst();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        lock.unlock();
      }
      if (task != null) {
        task.run();
      }
    }
  }

  public void post(Runnable task) {
    lock.lock();
    try {
      taskQueue.add(task);
      condition.signal(); // Vekker en tråd
    } finally {
      lock.unlock();
    }
  }

  public void stop() {
    lock.lock();
    try {
      running = false;
      condition.signalAll(); // Vekk alle tråder slik at de kan avslutte
    } finally {
      lock.unlock();
    }

    for (Thread thread : workerThreads) {
      try {
        thread.join(); // Vent på at alle tråder avslutter
      } catch (InterruptedException ignored) {}
    }
  }

  public static void main(String[] args) {
    Workers workerThreads = new Workers(4);
    Workers eventLoop = new Workers(1);

    workerThreads.post(() -> System.out.println("Task A"));
    workerThreads.post(() -> System.out.println("Task B"));

    eventLoop.post(() -> System.out.println("Task C"));
    eventLoop.post(() -> System.out.println("Task D"));

    try {
      Thread.sleep(3000); // Vent for å la oppgavene kjøre
    } catch (InterruptedException ignored) {}

    workerThreads.stop();
    eventLoop.stop();
  }
}