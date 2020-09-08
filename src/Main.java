public class Main {

    private final static int PARENT_SELECTION_VERSION = 2; //// 0 to 2
    private final static int CROSSOVER_VERSION = 2; //// 0 to 2
    private final static int REMAINING_SELECTION_VERSION = 1; //// 0 to 1
    private final static int NUMBER_OF_GENERATION = 1000;

    public static void main(String[] args) {

        for (int n = 2; n <= 9; n++) {
            Knapsack knapsack = new Knapsack(n);

            knapsack.createScanner();
            knapsack.setValuesAndArrays();
            knapsack.createFirsPopulation();
            knapsack.evaluation(0);

            int iterator = 0;
            while (iterator != NUMBER_OF_GENERATION) {
                knapsack.selection(PARENT_SELECTION_VERSION);
                knapsack.crossover(CROSSOVER_VERSION);
                knapsack.mutation();
                knapsack.evaluation(1);
                knapsack.remainingSelection(REMAINING_SELECTION_VERSION, iterator);
                iterator++;
            }

            knapsack.printResult();
        }

    }
}
