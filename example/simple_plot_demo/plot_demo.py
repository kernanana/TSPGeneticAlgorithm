import matplotlib.pyplot as plt
import csv

from numpy import double

# Installation instructions for matplotlib
# https://matplotlib.org/stable/users/installing.html

# Longer tutorial here
# https://docs.python.org/3/library/csv.html

data="run.csv"
generations=[]
best=[]
average=[]
worst=[]

with open(data, newline='') as csvfile:
    # Using the csv reader automatically places all values 
    # in columns within a row in a dictionary with a 
    # key based on the header (top line of the file)
    reader = csv.DictReader(csvfile)
    for row in reader:
        generations.append( row["generation"] )
        best.append( float(row["max"] ) )
        average.append(float(row["average"] ))
        worst.append( float(row["min"] ))
        

plt.plot(generations,best,label="best")
plt.plot(generations,average,label="average")
plt.plot(generations,worst,label="worst")

plt.legend()
plt.xlabel('generations')
plt.ylabel('fitness')


plt.savefig('demo_plot.png')
plt.show()
