import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Test_Experimentale_ARN_ABR {

    private static void generateWorstCase(ARN<Integer> arbre, int n) {
        for (int i = 0; i < n; i++) {
            arbre.add(i);
        }
    }

    private static void generateWorstCase(ABR<Integer> arbre, int n) {
        for (int i = 0; i < n; i++) {
            arbre.add(i);
        }
    }

    private static void generateAverageCase(ARN<Integer> arbre, int n) {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            arbre.add(rand.nextInt(n));
        }
    }

    private static void generateAverageCase(ABR<Integer> arbre, int n) {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            arbre.add(rand.nextInt(n));
        }
    }

    private static long measureInsertionTime(ARN<Integer> arbre, int n, boolean isWorstCase) {
        long startTime = System.nanoTime();
        if (isWorstCase) {
            generateWorstCase(arbre, n);
        } else {
            generateAverageCase(arbre, n);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static long measureInsertionTime(ABR<Integer> arbre, int n, boolean isWorstCase) {
        long startTime = System.nanoTime();
        if (isWorstCase) {
            generateWorstCase(arbre, n);
        } else {
            generateAverageCase(arbre, n);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static long measureSearchTime(ARN<Integer> arbre, int n) {
        long startTime = System.nanoTime();
        for (int i = 0; i < 2 * n; i++) {
            arbre.contains(i);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static long measureSearchTime(ABR<Integer> arbre, int n) {
        long startTime = System.nanoTime();
        for (int i = 0; i < 2 * n; i++) {
            arbre.contains(i);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static void writeResultsToCSV(String fileName, int[] ns, long[] times1, long[] times2, String label1, String label2) throws IOException {
        FileWriter csvWriter = new FileWriter(fileName);
        csvWriter.append("n," + label1 + " (ns)," + label2 + " (ns)\n");
        for (int i = 0; i < ns.length; i++) {
            csvWriter.append(ns[i] + "," + times1[i] + "," + times2[i] + "\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    public static void main(String[] args) {
        int[] ns = {100, 1000, 10000, 100000}; // Différentes tailles de n pour les tests
        long[] worstCaseInsertionTimesARN = new long[ns.length];
        long[] averageCaseInsertionTimesARN = new long[ns.length];
        long[] worstCaseSearchTimesARN = new long[ns.length];
        long[] averageCaseSearchTimesARN = new long[ns.length];
        long[] worstCaseInsertionTimesABR = new long[ns.length];
        long[] averageCaseInsertionTimesABR = new long[ns.length];
        long[] worstCaseSearchTimesABR = new long[ns.length];
        long[] averageCaseSearchTimesABR = new long[ns.length];

        for (int i = 0; i < ns.length; i++) {
            System.out.println("Testing for n = " + ns[i]);

            ARN<Integer> arbreWorstCaseARN = new ARN<>();
            ARN<Integer> arbreAverageCaseARN = new ARN<>();
            ABR<Integer> arbreWorstCaseABR = new ABR<>();
            ABR<Integer> arbreAverageCaseABR = new ABR<>();

            System.out.println("Measuring worst case insertion time for ARN...");
            worstCaseInsertionTimesARN[i] = measureInsertionTime(arbreWorstCaseARN, ns[i], true);

            System.out.println("Measuring average case insertion time for ARN...");
            averageCaseInsertionTimesARN[i] = measureInsertionTime(arbreAverageCaseARN, ns[i], false);

            System.out.println("Measuring worst case search time for ARN...");
            worstCaseSearchTimesARN[i] = measureSearchTime(arbreWorstCaseARN, ns[i]);

            System.out.println("Measuring average case search time for ARN...");
            averageCaseSearchTimesARN[i] = measureSearchTime(arbreAverageCaseARN, ns[i]);

            System.out.println("Measuring worst case insertion time for ABR...");
            worstCaseInsertionTimesABR[i] = measureInsertionTime(arbreWorstCaseABR, ns[i], true);

            System.out.println("Measuring average case insertion time for ABR...");
            averageCaseInsertionTimesABR[i] = measureInsertionTime(arbreAverageCaseABR, ns[i], false);

            System.out.println("Measuring worst case search time for ABR...");
            worstCaseSearchTimesABR[i] = measureSearchTime(arbreWorstCaseABR, ns[i]);

            System.out.println("Measuring average case search time for ABR...");
            averageCaseSearchTimesABR[i] = measureSearchTime(arbreAverageCaseABR, ns[i]);
        }

        try {
            writeResultsToCSV("average_case_insertion.csv", ns, averageCaseInsertionTimesARN, averageCaseInsertionTimesABR, "Average Case Insertion ARN", "Average Case Insertion ABR");
            writeResultsToCSV("average_case_search.csv", ns, averageCaseSearchTimesARN, averageCaseSearchTimesABR, "Average Case Search ARN", "Average Case Search ABR");
            writeResultsToCSV("worst_case_insertion.csv", ns, worstCaseInsertionTimesARN, worstCaseInsertionTimesABR, "Worst Case Insertion ARN", "Worst Case Insertion ABR");
            writeResultsToCSV("worst_case_search.csv", ns, worstCaseSearchTimesARN, worstCaseSearchTimesABR, "Worst Case Search ARN", "Worst Case Search ABR");
            System.out.println("Les fichiers CSV ont été créés avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}