#File example_ga.py
#Example of GA
#Dr. Jorge Luis Rosas Trigueros
#Last change: 12feb19


from tkinter import *
import math
import random
import functools
import numpy as np

#Chromosomes are 4 bits long
L_chromosome=16
N_chains=2**L_chromosome
#Lower and upper limits of search space
a=-20
b=20
crossover_point=L_chromosome//2

#Creation of chromosome
def random_chromosome():
    chromosome=[]
    for i in range(0,L_chromosome):
        if random.random()<0.5:
            chromosome.append(0)
        else:
            chromosome.append(1)
    return chromosome

#Number of chromosomes
N_chromosomes=10
#Probability of mutation
prob_m=0.5

F0=[]
fitness_values=[]

for i in range(0,N_chromosomes):
    F0.append(random_chromosome())
    fitness_values.append(0)

#Binary codification
def decode_chromosome(chromosome, function):
    global L_chromosome,N_chains,a,b
    if function != "acley":
        value=0
        for p in range(L_chromosome):
            value+=(2**p)*chromosome[-1-p]
        return a+(b-a)*float(value)/(N_chains-1)
    else:
        value_x = 0
        value_y = 0
        values = []
        for p in range(L_chromosome):
            if(p >= L_chromosome/2):
                value_y+=(2**(p-L_chromosome/2))*chromosome[-1-p]
            else:
                value_x+=(2**p)*chromosome[-1-p]
        values.append(value_x)
        values.append(value_y)
        #print(f"Value x: {value_x}, Value y: {value_y}")
        N = 2**(L_chromosome/2)
        values[0] = a+(b-a)*float(values[0])/(N-1)  
        values[1] = a+(b-a)*float(values[1])/(N-1)
        #print(f"V0: {values[0]}, V1: {values[1]}")
        return values



def f(x):
    #Function professor
    #return 0.05*x*x-4*math.cos(x)
    #Rastrigin
    #return 10 + (x**2 - 10 * np.cos(2 * math.pi * x))

    #Acley
    #print(f"{x[0]}, {x[1]}")
    value = -20*math.exp(-0.2*math.sqrt(0.5*(x[0]**2 + x[1]**2))) - math.exp(0.5*(np.cos(2*math.pi*x[0]) + np.cos(2*math.pi*x[1]))) + math.e + 20
    #print(f"Evaluation: {value}")
    return value


def evaluate_chromosomes():
    global F0

    for p in range(N_chromosomes):
        v=decode_chromosome(F0[p], "acley")
        fitness_values[p]=f(v)
        

def compare_chromosomes(chromosome1,chromosome2):
    vc1=decode_chromosome(chromosome1, "acley")
    vc2=decode_chromosome(chromosome2, "acley")
    fvc1=f(vc1)
    fvc2=f(vc2)
    if fvc1 > fvc2:
        return 1
    elif fvc1 == fvc2:
        return 0
    else: #fvg1<fvg2
        return -1


suma=float(N_chromosomes*(N_chromosomes+1))/2.

Lwheel=N_chromosomes*10

def create_wheel():
    global F0,fitness_values

    maxv=max(fitness_values)
    acc=0
    for p in range(N_chromosomes):
        acc+=maxv-fitness_values[p]
    fraction=[]
    for p in range(N_chromosomes):
        fraction.append( float(maxv-fitness_values[p])/acc)
        if fraction[-1]<=1.0/Lwheel:
            fraction[-1]=1.0/Lwheel
    fraction[0]-=(sum(fraction)-1.0)/2
    fraction[1]-=(sum(fraction)-1.0)/2

    wheel=[]

    pc=0

    for f in fraction:
        Np=int(f*Lwheel)
        for i in range(Np):
            wheel.append(pc)
        pc+=1

    return wheel
        
F1=F0[:]

def nextgeneration():
    w.delete(ALL)
    #print(F0)
    F0.sort(key=functools.cmp_to_key(compare_chromosomes))
    #print(fitness_values)
    #print(F0)
    print( "Best solution so far:" )
    print( "f("+str(decode_chromosome(F0[0], "acley"))+")= "+
           str(f(decode_chromosome(F0[0], "acley"))) )
                                                                    
    #elitism, the two best chromosomes go directly to the next generation
    F1[0]=F0[0]
    F1[1]=F0[1]
    for i in range(0,(N_chromosomes-2)//2):
        roulette=create_wheel()
        #Two parents are selected
        p1=random.choice(roulette)
        p2=random.choice(roulette)
        #Two descendants are generated
        o1=F0[p1][0:crossover_point]
        o1.extend(F0[p2][crossover_point:L_chromosome])
        o2=F0[p2][0:crossover_point]
        o2.extend(F0[p1][crossover_point:L_chromosome])
        #Each descendant is mutated with probability prob_m
        if random.random() < prob_m:
            o1[int(round(random.random()*(L_chromosome-1)))]^=1
        if random.random() < prob_m:
            o2[int(round(random.random()*(L_chromosome-1)))]^=1
        #The descendants are added to F1
        F1[2+2*i]=o1
        F1[3+2*i]=o2

    #graph_f()
    graph_population(F0,w,s,s,xo,yo,'red')
    graph_population(F1,w,s,s*0.5,xo,yo,'green')
    #The new generation replaces the old one
    F0[:]=F1[:]



#visualization
master = Tk()

xmax=400
ymax=400

xo=200
yo=200

s=10

w = Canvas(master, width=xmax, height=ymax)
w.pack()

            
button1 = Button(master, text="Next Generation", command=nextgeneration)
button1.pack()

N=100

"""
def graph_f():
    xini=-20.
    xfin=20.

    dx=(xfin-xini)/N

    xold=xini
    yold=f(xold)
    for i in range(1,N):
        xnew=xini+i*dx
        ynew=f(xnew)
        w.create_line(xo+xold*s,yo-yold*s,xo+xnew*s,yo-ynew*s)
        xold=xnew
        yold=ynew
"""
def graph_population(F,mycanvas,escalax,escalay,xcentro,ycentro,color):
    for chromosome in F:
        x=decode_chromosome(chromosome, "acley")
        #mycanvas.create_line(xcentro+x*escalax,ycentro-10*escalay,xcentro+x*escalax, ycentro+10*escalay,fill=color)


#graph_f()
graph_population(F0,w,s,s,xo,yo,'red')
F0.sort(key=functools.cmp_to_key(compare_chromosomes))
evaluate_chromosomes()



mainloop()

