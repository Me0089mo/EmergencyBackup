#File example_ga.py
#Example of GA
#Dr. Jorge Luis Rosas Trigueros
#Last change: 12feb19

from tkinter import *
import math
import random
import functools
import numpy as np

class geneticAlgorithm:
    F0=[]
    fitness_values=[]
    L_chromosome = 16
    N_chains = 2**L_chromosome
    func = ""
    generation = 0
    #Lower and upper limits of search space
    a=-20
    b=20
    crossover_point=L_chromosome//2
    #Number of chromosomes
    N_chromosomes=10
    #Probability of mutation
    prob_m=0.5
    suma=float(N_chromosomes*(N_chromosomes+1))/2.
    Lwheel=N_chromosomes*10
    #Variables for ploting
    xmax=400
    ymax=400
    xo=200
    yo=200
    s=10
    #Visualization
    master = None
    w = None
    button1 = None

    def __init__(self, func):
        self.func = func
        for i in range(0,self.N_chromosomes):
            self.F0.append(self.random_chromosome())
            self.fitness_values.append(0)
        self.master = Tk()
        self.w = Canvas(self.master, width=self.xmax, height=self.ymax)
        self.w.pack()
        self.button1 = Button(self.master, text="Next Generation", command=self.nextgeneration)
        self.button1.pack()
        if func != "acley":
            self.graph_f()
            self.graph_population(self.F0,self.w,self.s,self.s,self.xo,self.yo,'red')

    #Creation of chromosome
    def random_chromosome(self):
        chromosome = []
        for i in range(0,self.L_chromosome):
            if random.random()<0.5:
                chromosome.append(0)
            else:
                chromosome.append(1)
        return chromosome

    #Binary codification
    def decode_chromosome(self, chromosome):
        if self.func != "acley":
            value=0
            for p in range(self.L_chromosome):
                value+=(2**p)*chromosome[-1-p]
            return self.a+(self.b-self.a)*float(value)/(self.N_chains-1)
        else:
            value_x = 0
            value_y = 0
            values = []
            for p in range(self.L_chromosome):
                if(p >= self.L_chromosome/2):
                    value_y+=(2**(p-self.L_chromosome/2))*chromosome[-1-p]
                else:
                    value_x+=(2**p)*chromosome[-1-p]
            values.append(value_x)
            values.append(value_y)
            N = 2**(self.L_chromosome/2)
            values[0] = self.a+(self.b-self.a)*float(values[0])/(N-1)  
            values[1] = self.a+(self.b-self.a)*float(values[1])/(N-1)
            return values

    def f(self, x):
        #Function professor
        if self.func == "other":
            return 0.05*x*x-4*math.cos(x)
        #Rastrigin
        elif self.func == "rastrigin":
            return 10 + (x**2 - 10 * np.cos(2 * math.pi * x))
        #Acley
        else:
            return -20*math.exp(-0.2*math.sqrt(0.5*(x[0]**2 + x[1]**2))) - math.exp(0.5*(np.cos(2*math.pi*x[0]) + np.cos(2*math.pi*x[1]))) + math.e + 20

    def evaluate_chromosomes(self):
        for p in range(self.N_chromosomes):
            v=self.decode_chromosome(self.F0[p])
            self.fitness_values[p]=self.f(v)
        

    def compare_chromosomes(self, chromosome1,chromosome2):
        vc1=self.decode_chromosome(chromosome1)
        vc2=self.decode_chromosome(chromosome2)
        fvc1=self.f(vc1)
        fvc2=self.f(vc2)
        if fvc1 > fvc2:
            return 1
        elif fvc1 == fvc2:
            return 0
        else:
            return -1

    def create_wheel(self):
        maxv=max(self.fitness_values)
        acc=0
        for p in range(self.N_chromosomes):
            acc+=maxv-self.fitness_values[p]
        fraction=[]
        for p in range(self.N_chromosomes):
            fraction.append( float(maxv-self.fitness_values[p])/acc)
            if fraction[-1]<=1.0/self.Lwheel:
                fraction[-1]=1.0/self.Lwheel
        fraction[0]-=(sum(fraction)-1.0)/2
        fraction[1]-=(sum(fraction)-1.0)/2

        wheel=[]

        pc=0

        for f in fraction:
            Np=int(f*self.Lwheel)
            for i in range(Np):
                wheel.append(pc)
            pc+=1

        return wheel

    def nextgeneration(self):
        self.F1=self.F0[:]
        self.w.delete(ALL)
        self.generation += 1
        self.F0.sort(key=functools.cmp_to_key(self.compare_chromosomes))
        print( f"Best solution so far (Generation {self.generation}):" )
        print( "f("+str(self.decode_chromosome(self.F0[0]))+")= "+
            str(self.f(self.decode_chromosome(self.F0[0]))) )
                                                                        
        #elitism, the two best chromosomes go directly to the next generation
        self.F1[0]=self.F0[0]
        self.F1[1]=self.F0[1]
        for i in range(0,(self.N_chromosomes-2)//2):
            roulette=self.create_wheel()
            #Two parents are selected
            p1=random.choice(roulette)
            p2=random.choice(roulette)
            #Two descendants are generated
            o1=self.F0[p1][0:self.crossover_point]
            o1.extend(self.F0[p2][self.crossover_point:self.L_chromosome])
            o2=self.F0[p2][0:self.crossover_point]
            o2.extend(self.F0[p1][self.crossover_point:self.L_chromosome])
            #Each descendant is mutated with probability prob_m
            if random.random() < self.prob_m:
                o1[int(round(random.random()*(self.L_chromosome-1)))]^=1
            if random.random() < self.prob_m:
                o2[int(round(random.random()*(self.L_chromosome-1)))]^=1
            #The descendants are added to F1
            self.F1[2+2*i]=o1
            self.F1[3+2*i]=o2
        if self.func != "acley":
            self.graph_f()
            self.graph_population(self.F0,self.w,self.s,self.s,self.xo,self.yo,'red')
            self.graph_population(self.F1,self.w,self.s,self.s*0.5,self.xo,self.yo,'green')
        #The new generation replaces the old one
        self.F0[:]=self.F1[:]

    def graph_f(self):
        xini=-20.
        xfin=20.

        dx=(xfin-xini)/self.Lwheel

        xold=xini
        yold=self.f(xold)
        for i in range(1,self.Lwheel):
            xnew=xini+i*dx
            ynew=self.f(xnew)
            self.w.create_line(self.xo+xold*self.s,self.yo-yold*self.s,self.xo+xnew*self.s,self.yo-ynew*self.s)
            xold=xnew
            yold=ynew

    def graph_population(self, F,mycanvas,escalax,escalay,xcentro,ycentro,color):
        for chromosome in F:
            x=self.decode_chromosome(chromosome)
            mycanvas.create_line(xcentro+x*escalax,ycentro-10*escalay,xcentro+x*escalax, ycentro+10*escalay,fill=color)


ga = geneticAlgorithm("acley")
ga.F0.sort(key=functools.cmp_to_key(ga.compare_chromosomes))
ga.evaluate_chromosomes()
ga.nextgeneration()
ga.master.mainloop()