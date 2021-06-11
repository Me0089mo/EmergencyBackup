import random
import functools

class knapsackGenetic:
    capacity = 0
    items = []
    len_chromosome = 0
    chromosomes = []
    num_chromosomes = 10
    cross_point = 0
    fitness = []
    mutation_prob = 0.9
    
    def __init__(self):
        #Asking for the values
        self.capacity = int(input("Insert capacity of the knapsack: "))
        self.len_chromosome = int(input("Insert the number of items: "))
        self.cross_point = self.len_chromosome // 2
        print("Insert the items in format: weight value")
        for i in range(self.len_chromosome):
            item = input(f"Item {i+1}: ").split()
            item[0] = int(item[0], 10)
            item[1] = int(item[1], 10)
            self.items.append(item)

    def generateChromosomes(self):
        chromosome = []
        for j in range(self.num_chromosomes):
            for i in range(self.len_chromosome):
                if random.random() < 0.2:
                    chromosome.append(0)
                else:
                    chromosome.append(1)
            self.chromosomes.append(chromosome.copy())
            chromosome.clear()
    
    def decodeChromosome(self, chromosome):
        weight = 0
        value = 0
        deco = []
        for i in range(self.len_chromosome):
            weight += chromosome[i] * self.items[i][0]
            value += chromosome[i] * self.items[i][1]
        deco.append(weight)
        deco.append(value)
        return deco

    def fitnessFunction(self, chromosome):
        deco = self.decodeChromosome(chromosome)
        if deco[0] > self.capacity:
            return (deco[0] - self.capacity) * 0.1
        else:
            return deco[1]

    def evaluateChromosome(self):
        for i in range(self.num_chromosomes):
            self.fitness.append(self.fitnessFunction(self.chromosomes[i]))

    def compareChromosome(self, chrom1, chrom2):
        fit1 = self.fitnessFunction(chrom1)
        fit2 = self.fitnessFunction(chrom2)
        if fit1 > fit2:
            return 1
        elif fit1 == fit2:
            return 0
        else:
            return -1

    def createWheel(self):
        len_wheel = 100
        wheel = []
        prob_wheel = []
        max_fit = max(self.fitness)
        dif_acc = 0
        #Calculating accumulation of the differences
        for i in range(self.num_chromosomes):
            dif_acc += abs(max_fit - self.fitness[i])
        #Calculating probabilities
        for i in range(self.num_chromosomes):
            prob = self.fitness[i]/dif_acc
            if prob < 0.01:
                prob_wheel.append(0.01)
            else:
                prob_wheel.append(prob)
        #Creating the wheel
        prob_count = 0
        for i in prob_wheel:
            space = int(i*100)
            for i in range(space):
                wheel.append(prob_count)
            prob_count += 1
        return wheel
    
    def nextGeneration(self):
        nextGen = []
        #Ordering the chromosomes according to its fitness
        self.chromosomes.sort(key = functools.cmp_to_key(self.compareChromosome), reverse = True)
        self.evaluateChromosome()
        print("Best solution so far:")
        best = self.decodeChromosome(self.chromosomes[0])
        print(f"Chromosome: {self.chromosomes[0]}")
        print(f"Weight: {best[0]}, Value: {best[1]}")
        #Elitism. The best to chromosomes pass directly to the next generation
        nextGen.append(self.chromosomes[0])
        nextGen.append(self.chromosomes[1])
        #Creation of the wheel
        wheel = self.createWheel()
        #Generating the next generation
        for i in range((self.num_chromosomes-2)//2):
            parent1 = self.chromosomes[random.choice(wheel)].copy()
            parent2 = self.chromosomes[random.choice(wheel)].copy()
            #Crossing to generate 2 childs
            child1 = parent1[0:self.cross_point]
            child1.extend(parent2[self.cross_point:self.len_chromosome])
            child2 = parent2[0:self.cross_point]
            child2.extend(parent1[self.cross_point:self.len_chromosome])
            #Mutating each child
            if random.random() < self.mutation_prob:
                child1[int(round(random.random()*(self.len_chromosome-1)))]^=1
            if random.random() < self.mutation_prob:
                child2[int(round(random.random()*(self.len_chromosome-1)))]^=1
            nextGen.append(child1)
            nextGen.append(child2)
        self.chromosomes = nextGen.copy()
        self.fitness.clear()
        print("\n")

kp = knapsackGenetic()
kp.generateChromosomes()
generation = 50
i = 0
while i < generation:
    print(f"Generation {i+1}")
    kp.nextGeneration()
    i+=1 