import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPOving1 {

  // Funksjon for å sjekke om et tall er et primtall
  public static boolean isPrime(int n) {
    if (n < 2) return false;
    for (int i = 2; i <= Math.sqrt(n); i++) {
      if (n % i == 0) return false;
    }
    return true;
  }

  // Trådklasse for å finne primtall i et intervall
  static class PrimeFinderThread extends Thread {
    private final int start;
    private final int end;
    private final List<Integer> primes;

    public PrimeFinderThread(int start, int end, List<Integer> primes) {
      this.start = start;
      this.end = end;
      this.primes = primes;
    }

    @Override
    public void run() {
      List<Integer> localPrimes = new ArrayList<>();
      for (int i = start; i <= end; i++) {
        if (isPrime(i)) {
          localPrimes.add(i);
        }
      }
      synchronized (primes) {
        primes.addAll(localPrimes);
      }
    }
  }

  public static List<Integer> findPrimesMultithreaded(int start, int end, int numThreads) {
    List<Integer> primes = Collections.synchronizedList(new ArrayList<>());
    List<PrimeFinderThread> threads = new ArrayList<>();

    // Beregn arbeidsområdet for hver tråd
    int rangeSize = (int) Math.ceil((double) (end - start + 1) / numThreads);

    for (int i = 0; i < numThreads; i++) {
      int subStart = start + i * rangeSize;
      int subEnd = Math.min(start + (i + 1) * rangeSize - 1, end);

      PrimeFinderThread thread = new PrimeFinderThread(subStart, subEnd, primes);
      threads.add(thread);
      thread.start();
    }

    // Vent på at alle tråder skal bli ferdige
    for (PrimeFinderThread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // Sorter primtallene
    Collections.sort(primes);
    return primes;
  }

  public static void main(String[] args) {
    int start = 1;
    int end = 100;
    int numThreads = 4;

    System.out.println("Finn primtall mellom " + start + " og " + end + " med " + numThreads + " tråder.");
    List<Integer> primes = findPrimesMultithreaded(start, end, numThreads);
    System.out.println("Primtall: " + primes);
  }
}

