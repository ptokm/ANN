package ann;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {
    private ArrayList <ArrayList <Double>> _population;
    private ArrayList <ArrayList <Double>> _newPopulation;
    private final ArrayList <ArrayList <Double>> _patterns;
    private final int _dimensionPatterns;
    private final int _nodes;
    private final int _countGenesOfChromosome;
    private final int _countOfPopulation;
    private final int _maxEpochs;
    private final double _elitismRatio;
    private final int _elitismCountOfChromosomesThatPassToNextEpoch;
    
    GeneticAlgorithm(int dimension) {
        this._countOfPopulation = 200;
        this._maxEpochs = 300;
        this._elitismRatio = 0.05; // 5 %
        this._elitismCountOfChromosomesThatPassToNextEpoch = (int)Math.round(this._countOfPopulation * this._elitismRatio);
        this._countGenesOfChromosome = dimension;
        this._patterns = Data.getTrainPatterns();
        this._dimensionPatterns = Data.getDimension();
        this._nodes = Data.getNodes();
        this._population = new ArrayList <>();
    }
    
    private void randomInitializationGenes() {
        for (int i = 0; i < this._countOfPopulation; i++) {
            Random r = new Random();
            double min = -1;
            double max = 1;
            
            ArrayList <Double> tempChromosome = new ArrayList <>();
            for (int j = 0; j < this._countGenesOfChromosome; j++) {
                Double randomValue = min + (max - min) * r.nextDouble();
                tempChromosome.add(randomValue);
            }
            
            for (int j = 0; j < 3; j++) {
                tempChromosome.add(-999.999); // Default values for future usage
            }
            
            this._population.add(tempChromosome);
        }
    }
    
    private void calculateFitness() {
        for (int i = 0; i < this._countOfPopulation; i++) {
            ArrayList <Double> tempChromosome = new ArrayList<>(this._population.get(i));
            tempChromosome.set(this._countGenesOfChromosome, MLP.getTrainError(this._patterns, this._dimensionPatterns, this._nodes, tempChromosome));
            this._population.set(i, tempChromosome);
        }
    }
    
    private void fitnessScaling() {
        boolean hasNegativeFitnessValue = false;
        for (ArrayList <Double> chromosome : this._population) {
            if (chromosome.get(this._countGenesOfChromosome) < 0)
                hasNegativeFitnessValue = true;
        }
        
        if (hasNegativeFitnessValue) {
            double minFitnessValue = this._population.get(0).get(this._countGenesOfChromosome);
            for (int i = 0; i < this._population.size(); i++) {
                if (this._population.get(i).get(this._countGenesOfChromosome) < minFitnessValue)
                    minFitnessValue = this._population.get(i).get(this._countGenesOfChromosome);
            }
            
            minFitnessValue = Math.abs(minFitnessValue);
            for (int i = 0; i < this._population.size(); i++) {
                double scaledFitnessValue = 1.0 * this._population.get(i).get(this._countGenesOfChromosome) + minFitnessValue + 1; // +1 to avoid devide by zero after
                this._population.get(i).set(this._countGenesOfChromosome, scaledFitnessValue);
            }
        }
    }
    
    // Devide the fitness value of each chromosome
    // by the total values of all fitnesses
    private void normalizeFitnessValues() {
        double sumFitnessValue = 0.0;
        for (ArrayList <Double> chromosome : this._population) {
            sumFitnessValue += chromosome.get(this._countGenesOfChromosome);
        }

        for (int i = 0; i < this._population.size(); i++) {
            ArrayList <Double> inner = new ArrayList <> (this._population.get(i));
            inner.set(this._countGenesOfChromosome + 1, inner.get(this._countGenesOfChromosome) / sumFitnessValue);
            this._population.set(i, inner);
        }
    }
    
    private void sortPopulationByFitnessValue() {
        Collections.sort(this._population, Collections.reverseOrder((a, b) -> Double.compare(a.get(this._countGenesOfChromosome), b.get(this._countGenesOfChromosome))));
    }
    
    private void calculateCumulativeSumOfNormalizedFitnessValues() {
        for (int i = 0; i < this._population.size(); i++) {
            ArrayList <Double> temp = new ArrayList <>(this._population.get(i));
            double sum = temp.get(this._countGenesOfChromosome + 1);
            for (int j = (i + 1); j < this._population.size(); j++) {
                sum += this._population.get(j).get(this._countGenesOfChromosome + 1);
            }
            temp.set(this._countGenesOfChromosome + 2, sum);
            this._population.set(i, temp);
        }
    }
    
    private void elitism() {
        for (int i = this._population.size() - 1; i >= this._population.size() - this._elitismCountOfChromosomesThatPassToNextEpoch; i--) {
            this._newPopulation.add(this._population.get(i));
        }
    }
    
     private void singlePointCrossover(ArrayList <ArrayList <Double>> tempChromosomes) {
        Random r = new Random();
        int min = 1;
        int max = this._countGenesOfChromosome;
        int randomGenePosition = r.nextInt((max - min) + 1) + min;    
                
        ArrayList <Double> child1 = new ArrayList <>();

        for (int i = 0; i < randomGenePosition; i++) {
            child1.add(tempChromosomes.get(0).get(i));
        }
        for (int i = randomGenePosition; i < this._countGenesOfChromosome; i++) {
            child1.add(tempChromosomes.get(1).get(i));
        }
        for (int i = 0; i < 3; i++) {
            child1.add(-1.0);
        }

        this._newPopulation.add(child1);
    }
    
     private void doublePointCrossover(ArrayList <ArrayList <Double>> selectedChromosomes, int randomGenePosition, int secondRandomGenePosition) {
        ArrayList <Double> child1 = new ArrayList <>();

        for (int i = 0; i < randomGenePosition; i++) {
            child1.add(selectedChromosomes.get(0).get(i));
        }
        for (int i = randomGenePosition; i < secondRandomGenePosition; i++) {
            child1.add(selectedChromosomes.get(1).get(i));
        }
        for (int i = secondRandomGenePosition; i < this._countGenesOfChromosome; i++) {
            child1.add(selectedChromosomes.get(0).get(i));
        }

        for (int i = 0; i < 3; i++) {
            child1.add(-1.0);
        }
        
        this._newPopulation.add(child1);
    }
     
    private void rouletteWheel() {
       while (this._newPopulation.size() != this._population.size()) {
            // Take 2 parants and produce 1 child
            ArrayList <ArrayList <Double>> tempChromosomes = new ArrayList<>();
            
            for (int i = 0; i < 2; i++) {
                int ramdomParentIndex = (int) ((Math.random() * (this._countOfPopulation - 0)) + 0);
                tempChromosomes.add(this._population.get(ramdomParentIndex));
            }
            
            Random r = new Random();
            int min = 1;
            int max = this._countGenesOfChromosome;
            int randomGenePosition = r.nextInt((max - min) + 1) + min;
        
            int secondRandomGenePosition;
            do {
                secondRandomGenePosition = r.nextInt((max - min) + 1) + min;
            } while (secondRandomGenePosition == randomGenePosition);

            if (secondRandomGenePosition < randomGenePosition) {
                int temp = randomGenePosition;
                randomGenePosition = secondRandomGenePosition;
                secondRandomGenePosition = temp;
            }

            this.doublePointCrossover(tempChromosomes, randomGenePosition, secondRandomGenePosition);
        } 
    }
    
    private void mutation() {
        for (int i = this._elitismCountOfChromosomesThatPassToNextEpoch; i < this._newPopulation.size(); i++) {
            ArrayList <Double> tempChromosome = new ArrayList <>();
            for (int j = 0; j < this._newPopulation.get(i).size(); j++) {
                double probability_of_mutation = Math.random();
                if (probability_of_mutation <= 0.07) {
                   tempChromosome.add(this._newPopulation.get(i).get(j) + this._newPopulation.get(i).get(j) * probability_of_mutation);
                }
                else {
                    tempChromosome.add(this._newPopulation.get(i).get(j));
                }
            }
            
           this._newPopulation.set(i, tempChromosome);
        }
    }
    
    private void selection() {
        this.rouletteWheel();
    }
    
    private void fitnessFunctions() {
        this.calculateFitness();
        this.fitnessScaling();
        this.normalizeFitnessValues();
        this.sortPopulationByFitnessValue();
        this.calculateCumulativeSumOfNormalizedFitnessValues();
    }
    
    public ArrayList <Double> getBestChromosome() {
        this.randomInitializationGenes();
        this.fitnessFunctions();
            
        for (int i = 0; i < this._maxEpochs; i++) {
            this._newPopulation = new ArrayList <>();
            this.elitism();
            this.selection();
            this.mutation();
            
            this._population = new ArrayList <>(this._newPopulation);
            this._newPopulation = new ArrayList<>();
        
             this.fitnessFunctions();
            
            //this.displayTrainError(i);
        }
        
        return this._population.get(this._population.size() - 1);
    }
    
    private void displayTrainError(int i) {
        System.out.println("i[" + i + "] = " + this._population.get(this._population.size() - 1).get(this._countGenesOfChromosome));
    }
}