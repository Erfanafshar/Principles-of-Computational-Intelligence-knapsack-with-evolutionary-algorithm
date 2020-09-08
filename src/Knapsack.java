import java.io.File;
import java.util.*;

public class Knapsack {

    private int NUMBER_OF_CHROMOSOME;
    private int NUMBER_OF_PARENT_SELECTION;
    private int NUMBER_OF_CHILDREN_CREATION;
    private int NUMBER_OF_N_PARENT_POINTS;
    private final static double MUTATION_RATE = 0.1;
    private int TOURNAMENT_SIZE;
    private int GENES_IN_CHROMOSOME;
    private final static int COLUMNS_OF_TABLE = 2;
    private final static String FILE_NAME = "knapsack_3.txt";

    private Scanner scanner;

    private int NUMBER_OF_ITEMS;
    private int knapsackWeight;

    private int[][] items;
    private int[][] parents;
    private int[][] children;
    private int[] parentFitness;
    private int[] childrenFitness;
    private int selectedParentsIndex[];

    Knapsack(int numberOfSelectionItem) {
        GENES_IN_CHROMOSOME = numberOfSelectionItem;
        NUMBER_OF_N_PARENT_POINTS = numberOfSelectionItem / 2;
        if (numberOfSelectionItem >= 9) {
            NUMBER_OF_CHROMOSOME = 10;
            NUMBER_OF_PARENT_SELECTION = 5;
            NUMBER_OF_CHILDREN_CREATION = 10;
            TOURNAMENT_SIZE = 2;
        } else {
            NUMBER_OF_CHROMOSOME = 100;
            NUMBER_OF_PARENT_SELECTION = 50;
            NUMBER_OF_CHILDREN_CREATION = 100;
            TOURNAMENT_SIZE = 5;
        }

        parentFitness = new int[NUMBER_OF_CHROMOSOME];
        childrenFitness = new int[NUMBER_OF_CHILDREN_CREATION];
        selectedParentsIndex = new int[NUMBER_OF_PARENT_SELECTION];

        parents = new int[NUMBER_OF_CHROMOSOME][GENES_IN_CHROMOSOME];
        children = new int[NUMBER_OF_CHILDREN_CREATION][GENES_IN_CHROMOSOME];
    }

    public void createScanner() {
        try {
            File file = new File(FILE_NAME);
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("file not found");
        }
    }

    public void setValuesAndArrays() {
        String spt[] = scanner.nextLine().split(" ");
        NUMBER_OF_ITEMS = Integer.valueOf(spt[0]);
        knapsackWeight = Integer.valueOf(spt[1]);

        items = new int[NUMBER_OF_ITEMS][COLUMNS_OF_TABLE];

        for (int i = 0; i < NUMBER_OF_ITEMS; i++) {
            spt = scanner.nextLine().split(" ");
            items[i][0] = Integer.valueOf(spt[0]);
            items[i][1] = Integer.valueOf(spt[1]);
        }
    }

    public void createFirsPopulation() {
        for (int i = 0; i < NUMBER_OF_CHROMOSOME; i++) {
            Random rand = new Random();
            int[] parent = rand.ints(3 * GENES_IN_CHROMOSOME, 0, NUMBER_OF_ITEMS - 1)
                    .distinct().limit(GENES_IN_CHROMOSOME).toArray();
            if (checkChromosome(parent))
                parents[i] = parent;
            else
                i--;
        }
    }

    public void evaluation(int version) {
        if (version == 0)
            evaluationCalculation(parents, parentFitness, NUMBER_OF_CHROMOSOME);
        if (version == 1) {
            evaluationCalculation(children, childrenFitness, NUMBER_OF_CHILDREN_CREATION);
        }

    }

    public void printResult() {
        double maxValue = Integer.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0; i < NUMBER_OF_CHILDREN_CREATION; i++) {
            if (childrenFitness[i] > maxValue) {
                maxValue = childrenFitness[i];
                maxIndex = i;
            }
        }
        System.out.print(GENES_IN_CHROMOSOME + " - ");
        int ww = 0;
        for (int i = 0; i < GENES_IN_CHROMOSOME; i++) {
            System.out.print(items[children[maxIndex][i]][1] + "_");
            System.out.print(items[children[maxIndex][i]][0] + " -- ");
            ww += items[children[maxIndex][i]][1];
        }
        System.out.print(ww + " == ");
        System.out.println(maxValue);
    }

    private void evaluationCalculation(int evalArray[][], int fitArray[], int upper) {
        int sumValue;
        for (int i = 0; i < upper; i++) {
            sumValue = 0;
            for (int j = 0; j < GENES_IN_CHROMOSOME; j++) {
                sumValue += items[evalArray[i][j]][0];
            }
            fitArray[i] = sumValue;
        }
    }

    public void selection(int version) {
        switch (version) {
            case 0:
                rouletteWheel();
                break;
            case 1:
                sus();
                break;
            case 2:
                tournament();
                break;
        }
    }

    private void rouletteWheel() {
        double[] points = createRuler();
        double sumFitness = points[NUMBER_OF_CHROMOSOME - 1];
        double[] randomNumbers = setRandomNumbers1(sumFitness);
        setSelectedParentsIndex(points, randomNumbers);
    }

    private double[] createRuler() {
        double[] points = new double[NUMBER_OF_CHROMOSOME];
        double sumFitness = 0.0;
        for (int i = 0; i < NUMBER_OF_CHROMOSOME; i++) {
            sumFitness += parentFitness[i];
            points[i] = sumFitness;
        }
        return points;
    }

    private double[] setRandomNumbers1(double sumFitness) {
        double[] randomNumbers = new double[NUMBER_OF_PARENT_SELECTION];
        for (int i = 0; i < NUMBER_OF_PARENT_SELECTION; i++) {
            Random random = new Random();
            randomNumbers[i] = random.nextDouble() * sumFitness;
        }
        return randomNumbers;
    }

    private void setSelectedParentsIndex(double[] points, double[] randomNumbers) {
        for (int i = 0; i < NUMBER_OF_PARENT_SELECTION; i++) {
            binarySearch(points, randomNumbers[i], i);
        }
    }

    private void binarySearch(double[] points, double randomNumber, int k) {
        int fIndex = 0;
        int eIndex = NUMBER_OF_CHROMOSOME - 1;
        int mIndex = (eIndex + fIndex) / 2;
        double mPoint = points[mIndex];
        while (true) {
            if (randomNumber < mPoint) {
                eIndex = mIndex;
            } else {
                fIndex = mIndex;
            }
            mIndex = (fIndex + eIndex) / 2;
            mPoint = points[mIndex];
            if (mIndex == fIndex) {
                selectedParentsIndex[k] = mIndex + 1;
                break;
            }
        }
    }

    private void sus() {
        double[] points = createRuler();
        double sumFitness = points[NUMBER_OF_CHROMOSOME - 1];
        double[] randomNumbers = setRandomNumbers2(sumFitness);
        setSelectedParentsIndex(points, randomNumbers);
    }

    private double[] setRandomNumbers2(double sumFitness) {
        double[] randomNumbers = new double[NUMBER_OF_PARENT_SELECTION];
        Random random = new Random(System.currentTimeMillis());
        double step = sumFitness / NUMBER_OF_PARENT_SELECTION;
        randomNumbers[0] = random.nextDouble() * step;
        for (int i = 1; i < NUMBER_OF_PARENT_SELECTION; i++) {
            randomNumbers[i] = randomNumbers[i - 1] + step;
        }
        return randomNumbers;
    }

    private void tournament() {
        ArrayList<Integer> tournamentParentIndex = new ArrayList<>();
        int randNum;
        for (int i = 0; i < NUMBER_OF_PARENT_SELECTION; i++) {
            tournamentParentIndex.clear();
            for (int j = 0; j < TOURNAMENT_SIZE; j++) {
                Random random = new Random();
                randNum = random.nextInt(NUMBER_OF_CHROMOSOME);
                if (!tournamentParentIndex.contains(randNum)) {
                    tournamentParentIndex.add(randNum);
                } else {
                    j--;
                }
            }
            tournamentSelection(tournamentParentIndex, i);
        }
    }

    private void tournamentSelection(ArrayList<Integer> tournamentParentIndex, int k) {
        int maxIndex = -1;
        double maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            if (parentFitness[tournamentParentIndex.get(i)] > maxVal) {
                maxVal = parentFitness[tournamentParentIndex.get(i)];
                maxIndex = tournamentParentIndex.get(i);
            }
        }
        selectedParentsIndex[k] = maxIndex;
    }

    public void crossover(int version) {
        crossoverLoop(version);
    }

    private void crossoverLoop(int version) {
        for (int i = 0; i < (NUMBER_OF_CHILDREN_CREATION / 2); i++) {
            int[] randIndex = createTwoRandInteger(NUMBER_OF_PARENT_SELECTION);
            if (!crossoverChoose(randIndex, 2 * i, version))
                i--;
        }
    }

    private boolean crossoverChoose(int[] randIndex, int childrenIndex, int version) {
        int[] parent1 = parents[selectedParentsIndex[randIndex[0]]];
        int[] parent2 = parents[selectedParentsIndex[randIndex[1]]];
        int[] child1 = new int[GENES_IN_CHROMOSOME];
        int[] child2 = new int[GENES_IN_CHROMOSOME];

        return selectCrossoverType(parent1, parent2, child1, child2, childrenIndex, version);

    }

    private boolean selectCrossoverType(int[] parent1, int[] parent2, int[] child1, int[] child2, int childrenIndex, int version) {
        boolean b1;
        boolean b2;
        switch (version) {
            case 0:
                int randomIndex = getRandomIndex();
                b1 = onePoint(parent1, parent2, child1, childrenIndex, randomIndex);
                b2 = onePoint(parent2, parent1, child2, childrenIndex + 1, randomIndex);
                return (b1 && b2);
            case 1:
                int[] points = getPoints();
                b1 = nPoint(parent1, parent2, child1, childrenIndex, points);
                b2 = nPoint(parent2, parent1, child2, childrenIndex + 1, points);
                return (b1 && b2);
            case 2:
                double[] randNumbers = getRandNumbers();
                b1 = uniform(parent1, parent2, child1, childrenIndex, randNumbers);
                b2 = uniform(parent2, parent1, child2, childrenIndex + 1, randNumbers);
                return (b1 && b2);
            default:
                System.out.println("error");
                return false;
        }
    }

    private int getRandomIndex() {
        Random random = new Random();
        return random.nextInt(GENES_IN_CHROMOSOME);
    }

    private boolean onePoint(int[] parent1, int[] parent2, int[] child, int childIndex, int randomIndex) {
        System.arraycopy(parent1, 0, child, 0, randomIndex);
        System.arraycopy(parent2, randomIndex, child, randomIndex, GENES_IN_CHROMOSOME - randomIndex);
        if (checkChromosome(child))
            children[childIndex] = child;
        else
            return false;
        return true;
    }

    private int[] getPoints() {
        Random rand = new Random();
        int[] points = new int[NUMBER_OF_N_PARENT_POINTS + 2];
        points[0] = 0;
        int[] points2 = rand.ints(NUMBER_OF_N_PARENT_POINTS * 10, 0, GENES_IN_CHROMOSOME - 1)
                .distinct().limit(NUMBER_OF_N_PARENT_POINTS).toArray();
        System.arraycopy(points2, 0, points, 1, NUMBER_OF_N_PARENT_POINTS);

        points[NUMBER_OF_N_PARENT_POINTS + 1] = GENES_IN_CHROMOSOME;
        Arrays.sort(points);
        return points;
    }

    private boolean nPoint(int[] parent1, int[] parent2, int[] child, int childIndex, int[] points) {
        boolean turn = true;
        for (int i = 0; i < NUMBER_OF_N_PARENT_POINTS + 1; i++) {
            for (int j = points[i]; j < points[i + 1]; j++) {
                if (turn) {
                    child[j] = parent1[j];
                } else {
                    child[j] = parent2[j];
                }
            }
            turn = !turn;
        }
        if (checkChromosome(child))
            children[childIndex] = child;
        else
            return false;
        return true;
    }

    private double[] getRandNumbers() {
        double[] randNumbers = new double[GENES_IN_CHROMOSOME];
        for (int i = 0; i < GENES_IN_CHROMOSOME; i++) {
            Random random = new Random();
            randNumbers[i] = random.nextDouble();
        }
        return randNumbers;
    }

    private boolean uniform(int[] parent1, int[] parent2, int[] child, int childIndex, double[] randNumbers) {
        for (int i = 0; i < GENES_IN_CHROMOSOME; i++) {
            if (randNumbers[i] < 0.5)
                child[i] = parent1[i];
            else
                child[i] = parent2[i];
        }

        if (checkChromosome(child))
            children[childIndex] = child;
        else
            return false;
        return true;
    }

    private int[] createTwoRandInteger(int upperBound) {
        Random random = new Random();
        int[] randsIndex = new int[2];
        randsIndex[0] = random.nextInt(upperBound);
        randsIndex[1] = randsIndex[0];
        while (randsIndex[1] == randsIndex[0]) {
            randsIndex[1] = random.nextInt(upperBound);
        }
        Arrays.sort(randsIndex);
        return randsIndex;
    }

    public void mutation() {
        for (int i = 0; i < NUMBER_OF_CHILDREN_CREATION * MUTATION_RATE; i++) {
            Random rand1 = new Random();
            int childIndex = rand1.nextInt(NUMBER_OF_CHILDREN_CREATION);
            Random rand2 = new Random();
            int geneIndex = rand2.nextInt(GENES_IN_CHROMOSOME);
            Random rand3 = new Random();
            int newGeneIndex = rand3.nextInt(NUMBER_OF_ITEMS);

            int[] newChromosome = new int[GENES_IN_CHROMOSOME];
            System.arraycopy(children[childIndex], 0, newChromosome, 0, GENES_IN_CHROMOSOME);
            newChromosome[geneIndex] = newGeneIndex;
            if (checkChromosome(newChromosome))
                children[childIndex][geneIndex] = newGeneIndex;
            else
                i--;

        }
    }

    private boolean checkChromosome(int[] chromosome) {
        boolean b1 = checkItems(chromosome);
        boolean b2 = checkWeights(chromosome);
        return (b1 && b2);
    }

    private boolean checkItems(int[] chromosome) {
        for (int i = 0; i < GENES_IN_CHROMOSOME - 1; i++) {
            int val = chromosome[i];
            for (int j = i + 1; j < GENES_IN_CHROMOSOME; j++) {
                if (val == chromosome[j])
                    return false;
            }
        }
        return true;
    }

    private boolean checkWeights(int[] chromosome) {
        int weight = 0;
        for (int i = 0; i < GENES_IN_CHROMOSOME; i++) {
            weight += items[chromosome[i]][1];
        }
        return (weight <= knapsackWeight);
    }

    public void remainingSelection(int version, int iterator) {
        if (version == 0)
            chooseChildren();
        if (version == 1)
            chooseBest(iterator);
    }

    private void chooseChildren() {
        parents = children;
        parentFitness = childrenFitness;
    }

    private void chooseBest(int iterator) {
        int[][] newParents = new int[NUMBER_OF_CHROMOSOME][GENES_IN_CHROMOSOME];
        int newParentsFitness[] = new int[NUMBER_OF_CHROMOSOME];

        ArrayList<Integer> chooseIndex1 = new ArrayList<>();
        double maxVal1 = Integer.MIN_VALUE;
        int maxInx1 = -1;

        ArrayList<Integer> chooseIndex2 = new ArrayList<>();
        double maxVal2 = Integer.MIN_VALUE;
        int maxInx2 = -1;

        int whichChoose = 0;

        int k = 0;
        for (int i = 0; i < NUMBER_OF_CHROMOSOME; i++) {
            if (whichChoose != 2) {
                if (iterator == 0) {
                    maxVal1 = Integer.MIN_VALUE;
                    maxInx1 = -1;
                    for (int j = 0; j < NUMBER_OF_CHROMOSOME; j++) {
                        if (!chooseIndex1.contains(j)) {
                            if (parentFitness[j] > maxVal1) {
                                maxVal1 = parentFitness[j];
                                maxInx1 = j;
                            }
                        }
                    }
                } else {
                    maxVal1 = parentFitness[k];
                    maxInx1 = k;
                    k++;
                }
            }

            if (whichChoose != 1) {
                maxVal2 = Integer.MIN_VALUE;
                maxInx2 = -1;
                for (int j = 0; j < NUMBER_OF_CHILDREN_CREATION; j++) {
                    if (!chooseIndex2.contains(j)) {
                        if (childrenFitness[j] > maxVal2) {
                            maxVal2 = childrenFitness[j];
                            maxInx2 = j;
                        }
                    }
                }
            }

            if (maxVal1 > maxVal2) {
                whichChoose = 1;
                newParents[i] = parents[maxInx1];
                newParentsFitness[i] = parentFitness[maxInx1];
                chooseIndex1.add(maxInx1);
            } else {
                whichChoose = 2;
                newParents[i] = children[maxInx2];
                newParentsFitness[i] = childrenFitness[maxInx2];
                chooseIndex2.add(maxInx2);
            }

        }

        parents = newParents;
        parentFitness = newParentsFitness;
    }

}
