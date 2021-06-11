import random
import functools

class jugGenetic:
    max_len = 0
    min_len = 0
    num_chromosomes = 6
    actual_chromosome = 0
    jugs = []
    jug_small = 0
    jug_big = 0
    goal = 0
    chromosomes = []
    prob_recomb = 0.2
    num_op = 6

    def __init__(self):
        #Choosing bounds randomly for chromosomes lenght
        a = int(random.random()*10)
        b = int(random.random()*10)
        self.max_len = max(a,b)
        self.min_len = min(a,b)
        self.jug_small = input("Insert the capacity of the smaller jug: ")
        self.jug_big = input("Insert the capacity of the bigger jug: ")
        self.goal = input("Insert the content required in the bigger jug: ")

    def generateChromosomes(self):
        chromosome = []
        for j in range(self.num_chromosomes):
            #The chromosome is created and is appended to the list
            len_chromosome = random.randint(self.min_len, self.max_len+1)
            for i in range(len_chromosome):
                chromosome.append(random.randint(0,6))
            self.chromosomes.append(chromosome.copy())
            chromosome.clear()
            #The jug corresponding to the chromosome is created
            self.jugs.append(jugs(self.jug_small, self.jug_big))
    
    def decodeChromosome(self, chromosome):
        got_to_goal = -1
        deco = []
        for i in range(len(chromosome)):
            oper = chromosome[i]
            jugs[self.actual_chromosome].operation(oper)
            if jugs[self.actual_chromosome].big == self.goal:
                got_to_goal += 1
        if self.actual_chromosome == 5:
            self.actual_chromosome = 0
        if got_to_goal != -1:
             jugs[self.actual_chromosome].big = self.goal
        deco.append(abs(goal-jugs[self.actual_chromosome].big))
        deco.append(got_to_goal)
        return deco

    def fitnessFunction(self, chromosome):
        deco = self.decodeChromosome(chromosome)
        if deco[1] != -1:
            return deco[1] * 0.1
        else:
            return deco[0]

    def evaluateChromosomes(self):
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
        #Elitism
        nextGen.append(self.chromosomes[0])
        nextGen.append(self.chromosomes[1])
        #Mutation
        roulette = self.createWheel()
        for i in range(self.chromosomes-2):
            child = self.chromosomes[random.choice(roulette)].copy()
            chrom_length = len(child)
            if(chrom_length < self.min_len+3):
                #Insertion
                child.insert(int(round(random.random()*(chrom_length-1))), random.choice(self.num_op))
            elif(chrom_length > self.min_len-3):
                if random.random() < 0.5:
                    #Recombination
                    pos1 = random.choice(chrom_length-1)
                    pos2 = random.choice(chrom_length-1)
                    aux = child[pos1]
                    child[pos1] = child[pos2]
                    child[pos2] = aux
                else:
                    #Deletion of gene
                    child.pop(int(round(random.random()*(chrom_length-1))))
            else:
                #Mutation
                child[int(round(random.random()*(chrom_length-1)))]^=1
            nextGen.append(child)


class jugs:
    big = 0
    small = 0
    jugb = 0
    jugs = 0

    def __init__(self, big, small):
        self.big = big
        self.small = small

    def operation(self, num):
        if num == 0:
            self.jugb = self.big
        elif num == 1:
            self.jugs = self.small
        elif num == 2:
            self.jugb = 0
        elif num == 3:
            self.jugs = 0
        elif num == 4:
            self.jugb = self.jugb + self.jugs
            if self.jugb > self.big:
                self.jugb = self.big 
        elif num == 5:
            self.jugs = self.jugs + self.jugb
            if self.jugs > self.small:
                self.jugs = self.small 


jug = jugGenetic()
jug.generateChromosomes()
generation = 15
i = 0
while i < generation:
    print(f"Generation {i+1}")
    jug.nextGeneration()
    i+=1 