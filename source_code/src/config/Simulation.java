package config;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import config.DataClasses.LocationData;

public class Simulation {
    Random r = new Random();
    public Map<String, List<Integer>> locationData;
    private List<Map<String, Double>> thisGen;
    private Comparator<Map<String,Double>> c = new Comparator<Map<String, Double>>() {
        @Override
        public int compare(Map<String, Double> m1, Map<String, Double> m2){
            double fit1 = m1.values().iterator().next(); 
            double fit2 = m2.values().iterator().next(); 
            return Double.compare(fit1, fit2);
        }    
    };

    public Simulation(LocationData locationData) {
        this.locationData = locationData.coordByLocation;
		String filename = Constants.PATH_CONF_FILE;
		PropParser.load(filename);
    }

    public List<Map<String, Double>> initializePopulation(){
        List<Map<String, Double>> population = new ArrayList<>();

        int populationSize = Constants.POP_SIZE;
        for (int pop = 0; pop<populationSize; pop++) {
            Map<String, Double> organism = new HashMap<>();
            String genomeString = "";
            Set<String> c = locationData.keySet();
            List<String> locCopy = new ArrayList<>();
            locCopy.addAll(c);
            for (int i = locCopy.size() - 1; i > -1; i--) {
                int index = 0;
                if(i > 0){
                    index = r.nextInt(i);
                } 
                String loc = locCopy.remove(index);
                genomeString += loc;
            }
            Double fitness = calculateFitness(genomeString);
            organism.put(genomeString, fitness);
            population.add(organism);
        }
        population.sort(c);
        return population;
    }


    public Double calculateFitness(String genomeString) {
        double fitness = 0;
        for(int i = 0; i< genomeString.length() - 1; i++){
            String loc1 = genomeString.substring(i, i + 1);
            String loc2 = genomeString.substring(i + 1, i + 2);
            int loc1X = locationData.get(loc1).get(0);
            int loc1Y = locationData.get(loc1).get(1);
            int loc2X = locationData.get(loc2).get(0);
            int loc2Y = locationData.get(loc2).get(1);
            double distance = Math.pow(Math.pow(loc2Y - loc1Y, 2) + Math.pow(loc2X - loc1X, 2), .5);
            fitness += distance;
        }
        return fitness;

    }

    public List<Map<String, Double>> runSimulation(List<Map<String, Double>> pop, String selectionMethod, int elitists) {
        this.thisGen = pop;
        List<Map<String, Double>> elitistsToPreserve = saveElitists(pop, elitists);
        runNaturalSelectionAndReproduction(selectionMethod, elitistsToPreserve);
        runCrossover(elitists);
        mutateGenerationAndSort(elitists);
        return thisGen;
    }

    private List<Map<String, Double>> saveElitists(List<Map<String, Double>> pop, int elitists) {
        List<Map<String, Double>> elitistsToPreserve = new ArrayList<>();
        for (int i = 0; i < elitists; i++) {
            elitistsToPreserve.add(pop.get(i));
        }
        return elitistsToPreserve;
    }

    private void runCrossover(int elitists) {
        double crossoverRate = Constants.RECOM_RATE;
        for (int i = elitists; i < thisGen.size(); i++) {
            if(r.nextDouble() < crossoverRate) {
                int secondParentIndex = r.nextInt(thisGen.size() - elitists) + elitists;
                System.out.println(i + ";" + secondParentIndex);
                performCross(i, secondParentIndex);
            }
        }
    }

    private void performCross(int i, int secondParentIndex) {
        String firstGenome = thisGen.get(i).keySet().iterator().next();
        String secondGenome = thisGen.get(secondParentIndex).keySet().iterator().next();
        System.out.println(firstGenome + ";" + secondGenome);
        List<Integer> possibleIndexes = new ArrayList<>();
        for (int k = 0; k < firstGenome.length(); k++) {
            possibleIndexes.add(k);
        }
        int index1 = possibleIndexes.remove(r.nextInt(possibleIndexes.size()));
        int index2 = possibleIndexes.remove(r.nextInt(possibleIndexes.size()));
        if (index2 < index1) {
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }
        String geneCluster1 = firstGenome.substring(index1, index2);
        String geneCluster2 = secondGenome.substring(index1, index2);
        String firstGenomeShell = firstGenome.substring(0, index1) + firstGenome.substring(index2, firstGenome.length());
        String secondGenomeShell = secondGenome.substring(0, index1) +  secondGenome.substring(index2, secondGenome.length());
        String newGene1 = geneCluster1 + secondGenomeShell;
        String newGene2 = firstGenomeShell + geneCluster2;
        String dupesIn1 = "";
        String dupesIn2 = "";
        for(int j = 0; j < newGene1.length(); j++) {
            char check = newGene1.charAt(j);
            String toCheck = newGene1.substring(0, j) + newGene1.substring(j + 1, newGene1.length());
            if (toCheck.indexOf(check) != -1) {
                dupesIn1 += check;
                newGene1 = newGene1.substring(0, j) + newGene1.substring(j + 1, newGene1.length());
            }
        }
        for(int j = 0; j < newGene2.length(); j++) {
            char check = newGene2.charAt(j);
            String toCheck = newGene2.substring(0, j) + newGene2.substring(j + 1, newGene2.length());
            if (toCheck.indexOf(check) != -1) {
                dupesIn2 += check;
                newGene2 = newGene2.substring(0, j) + newGene2.substring(j + 1, newGene2.length());
            }
        }
        newGene1 += dupesIn2;
        newGene2 += dupesIn1;
        System.out.println("Crossing over: " + firstGenome + " AND " + secondGenome + " resulted in: " + newGene1 + " AND " + newGene2 + " using indexes " + index1 + " AND " + index2);
        Map<String, Double> firstNewGene = new HashMap<>();
        firstNewGene.put(newGene1, calculateFitness(newGene1));
        Map<String, Double> secondNewGene = new HashMap<>();
        secondNewGene.put(newGene2, calculateFitness(newGene2));
        thisGen.set(i, firstNewGene);
        thisGen.set(secondParentIndex, secondNewGene);
        
    }

    private void runNaturalSelectionAndReproduction(String selectionMethod, List<Map<String, Double>> elitistsToPreserve) {
        if (selectionMethod.equals("Truncation")) {
            truncationSelection();
        } else if (selectionMethod.equals("Ranked")) {
            rankedSelection(elitistsToPreserve);
        } else {
            rouletteWheel();
        }
    }

    private void rouletteWheel() {
        List<Map<String, Double>> elitistsToPreserve = new ArrayList<>();
        System.out.println("TODO: Ask Dr. Yoder about how to do this for TSP");
        List<Map<String,Double>> nextGen = new ArrayList<>();
        int populationSize = thisGen.size();
        double totalFitness = 0;
        for (int i = 0; i < populationSize; i++) {
            totalFitness += thisGen.get(i).values().iterator().next();
        }
        Double culmulatingProb = 0.0;
        List<Double> probabilityOfBeingChosen = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Double thisProb = (double)thisGen.get(i).values().iterator().next()/totalFitness;
            culmulatingProb += thisProb;
            probabilityOfBeingChosen.add(culmulatingProb);
        }
        for (int i = 0; i < populationSize; i++){
            Double winner = r.nextDouble();
            for (int j = 0; j < populationSize; j++) {
                if (winner < probabilityOfBeingChosen.get(j)) {
                    nextGen.add(thisGen.get(j));
                    break;
                }
            }
        }
        thisGen = nextGen;
    }

    private void rankedSelection(List<Map<String, Double>> elitistsToPreserve) {
        List<Map<String, Double>> nextGen = new ArrayList<>();
        nextGen.addAll(elitistsToPreserve);
        int populationSize = thisGen.size();
        int totalRank = 0;
        for (int i = 1; i < populationSize + 1; i++) {
            totalRank += i;
        }
        Double culmulatingProb = 0.0;
        List<Double> probabilityOfBeingChosen = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            int rankValue = populationSize - i;
            Double thisProb = (double)rankValue / totalRank;
            culmulatingProb += thisProb;
            probabilityOfBeingChosen.add(culmulatingProb);
        }
        for (int i = 0; i < populationSize; i++){
            Double winner = r.nextDouble();
            for (int j = 0; j < populationSize; j++) {
                if (winner < probabilityOfBeingChosen.get(j)) {
                    nextGen.add(thisGen.get(j));
                    break;
                }
            }
        }
        for(int i = elitistsToPreserve.size()  + populationSize - 1; i >= populationSize; i--) {
            nextGen.remove(i);
        }
        thisGen = nextGen;
    }

    private void truncationSelection() {
        List<Map<String, Double>> duplicateHalf = new ArrayList<>();
            for (int i = 0; i < thisGen.size() / 2; i++){
                duplicateHalf.add(thisGen.get(i));
                duplicateHalf.add(thisGen.get(i));
            }
        thisGen = duplicateHalf;
    }

    private void mutateGenerationAndSort(int elitists) {
        double mutationRate = Constants.MUT_RATE;
        for(int i = elitists; i < thisGen.size(); i++) {
            String genome = thisGen.get(i).keySet().iterator().next();
            for(int j = 0; j < genome.length(); j++){
                if(r.nextDouble() < mutationRate) {
                    List<Integer> counts = new ArrayList<>();
                    for(int k = 0; k < genome.length(); k++) {
                        counts.add(k);
                    }
                    int i1 = counts.remove(r.nextInt(counts.size()));
                    int i2 = counts.remove(r.nextInt(counts.size()));
                    char tempGene = genome.charAt(i1);
                    genome = genome.substring(0, i1) + genome.charAt(i2) + genome.substring(i1 + 1, genome.length());
                    genome = genome.substring(0, i2) + tempGene + genome.substring(i2 + 1, genome.length());
                    Double newFit = calculateFitness(genome);
                    Map<String, Double> newEntry = new HashMap<>();
                    newEntry.put(genome, newFit);
                    // System.out.println("Mutation Successful. Changing genome " + 
                    // thisGen.get(i) + " to " + newEntry);
                    thisGen.set(i, newEntry);
                }
            }
        }
        thisGen.sort(c);
    }

    public void changeComparator() {
        this.c = new Comparator<Map<String, Double>>() {
        @Override
        public int compare(Map<String, Double> m1, Map<String, Double> m2){
            double fit1 = m1.values().iterator().next(); 
            double fit2 = m2.values().iterator().next(); 
            return Double.compare(fit2, fit1);
        }    
    };
    }

}
