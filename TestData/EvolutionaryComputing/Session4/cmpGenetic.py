import random
import functools

class cmpGenetic:
    change = 0
    num_den = 0
    denom = []
    num_chromosomes = 10
    len_chromosome = 0
    chromosomes = []
    nextGen = []
    fitness = []
    prob_mutation = 0.5

    def __init__(self):
        #Asking for the values required
        self.change = int(input("Insert total change required: "))
        self.num_den = int(input("Insert the number of denominations available: "))
        for i in range(self.num_den):
            denomination = int(input("Denomination: "))
            self.denom.append(denomination)
        self.denom.sort()
        self.len_chromosome = 3*self.num_den
        
    def creationChromosomes(self):
        chromosome = []
        for j in range(self.num_chromosomes):
            for i in range(self.len_chromosome):
                if random.random() < 0.5:
                    chromosome.append(0)
                else:
                    chromosome.append(1)
            self.chromosomes.append(chromosome.copy())
            chromosome.clear()

    def decodeChromosome(self, chromosome):
        val = 0
        num = 0
        expo = 0
        actual_den = 0
        deco = []
        for i in range(self.len_chromosome):
            if i%self.num_den == self.num_den-1:
                num += (2**expo)*chromosome[-1-i]
                deco.append(num)
                val += num * self.denom[actual_den]
                num = 0 
                expo = 0
                actual_den += 1
            else:
                num += (2**expo)*chromosome[-1-i]
                expo += 1
        deco.insert(0,val)
        return deco

    def fitnessFunction(self, chromosome):
        deco = self.decodeChromosome(chromosome)
        return (0.9 * abs(self.change - deco[0])) + (0.1 * (deco[1]+deco[2]+deco[3]))

    def compareChromosome(self, chrom1, chrom2):
        fit1 = self.fitnessFunction(chrom1)
        fit2 = self.fitnessFunction(chrom2)
        if fit1 > fit2:
            return 1
        elif fit1 == fit2:
            return 0
        else:
            return -1

    def evaluateChromosome(self):
        for chro in self.chromosomes:
            self.fitness.append(self.fitnessFunction(chro))
    
    def createWheel(self):
        len_wheel = 100
        maxFit = min(self.fitness)
        acumDif = 0
        prob_in_wheel = []
        wheel = []
        prob_count = 0
        #Calculating acumulative difference
        for i in range(self.num_chromosomes):
            acumDif += abs(maxFit - self.fitness[i])
        #Calculating probabilities for the wheel according to fitness values
        for i in range(self.num_chromosomes):
            prob_in_wheel.append(abs(maxFit - self.fitness[i])/acumDif)
            if prob_in_wheel[-1] < 1.0/len_wheel:
                prob_in_wheel[-1] = 1.0/len_wheel
        #Creating the wheel
        for prob in prob_in_wheel:
            spaceWheel = int(prob*len_wheel)
            for j in range(spaceWheel):
                wheel.append(prob_count)
            prob_count += 1
        return wheel

    def nextGeneration(self):
        nextGen = []
        self.chromosomes.sort(key = functools.cmp_to_key(self.compareChromosome))
        self.evaluateChromosome()
        print("Best solution so far:")
        best = self.decodeChromosome(self.chromosomes[0])
        print(f"Chromosome: {self.chromosomes[0]}")
        print(f"Value: {best[0]}, Coins: {best[1]} {best[2]} {best[3]}")
        print(f"Fitness: {self.fitness[0]}")
        #Elitism. The two best chromosomes go directly to the next generation
        nextGen.append(self.chromosomes[0])
        nextGen.append(self.chromosomes[1])
        #Using mutation techniques for the rest of chromosomes in the generation
        wheel = self.createWheel()
        for i in range(self.num_chromosomes-2):
            #Choosing the parents with the wheel
            parent = random.choice(wheel)
            child = self.chromosomes[parent].copy()
            if random.random() < self.prob_mutation:
                child[int(round(random.random()*(self.len_chromosome-1)))]^=1
            nextGen.append(child)
        self.chromosomes = nextGen.copy()
        self.fitness.clear()
        print("\n")
            

cmp1 = cmpGenetic()
cmp1.creationChromosomes()
generations = 100
i = 0
while i < generations:
    print(f"Generation {i+1}")
    cmp1.nextGeneration()
    i += 1