import java.util.Random;

class ThreadSum extends Thread {
    private int[] array;
    private int startIndex;
    private int endIndex;
    private int threadId;
    private int partialSum;

    public ThreadSum(int[] array, int startIndex, int endIndex, int threadId) {
        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.threadId = threadId;
        this.partialSum = 0;
    }

    @Override
    public void run() {
        System.out.printf("Thread %d vai somar os elementos de %d a %d: [",
                threadId, startIndex, endIndex - 1);
        for (int i = startIndex; i < endIndex; i++) {
            System.out.print(array[i]);
            if (i < endIndex - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");

        for (int i = startIndex; i < endIndex; i++) {
            partialSum += array[i];
        }

        System.out.printf("Thread %d terminou com soma parcial: %d\n",
                threadId, partialSum);
    }

    public int getPartialSum() {
        return partialSum;
    }
}

public class ParallelSumApp {
    public static final int ARRAY_SIZE = 40;
    public static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        int[] numbers = new int[ARRAY_SIZE];
        Random random = new Random();

        System.out.println("Array gerado:");
        for (int i = 0; i < ARRAY_SIZE; i++) {
            numbers[i] = random.nextInt(100);
            System.out.print(numbers[i] + " ");
            if ((i + 1) % 10 == 0) System.out.println();
        }
        System.out.println();

        if (ARRAY_SIZE % NUM_THREADS != 0) {
            System.err.println("ERRO: O tamanho do array deve ser múltiplo do número de threads.");
            return;
        }

        int elementsPerThread = ARRAY_SIZE / NUM_THREADS;

        ThreadSum[] threads = new ThreadSum[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * elementsPerThread;
            int end = start + elementsPerThread;
            threads[i] = new ThreadSum(numbers, start, end, i);
            threads[i].start();
        }

        for (ThreadSum thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrompida: " + e.getMessage());
            }
        }

        int totalSum = 0;
        for (ThreadSum thread : threads) {
            totalSum += thread.getPartialSum();
        }

        int sequentialSum = 0;
        for (int num : numbers) {
            sequentialSum += num;
        }

        System.out.println("\nResultado:");
        System.out.println("Soma paralela: " + totalSum);
        System.out.println("Soma sequencial: " + sequentialSum);
        System.out.println("As somas " + (totalSum == sequentialSum ?
                "conferem!" : "não conferem!"));
    }
}
