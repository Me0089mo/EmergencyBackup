#Asking for the values
cap  = int(input("Insert capacity of the knapsack: "))
num_items = int(input("Insert the number of items: "))
weights = []
values = []
print("Insert the items in format: weight value")
for i in range(num_items):
    item = input(f"Item {i+1}: ")
    weights.append(int(item.split()[0]))
    values.append(int(item.split()[1]))

#Starts DP algorithm
table = [[None]*(cap+1) for i in range(num_items+1)]
for i in range(cap+1):
    table[0][i] = 0
for i in range(1,num_items+1):
    for j in range(cap+1):
        if weights[i-1] > j:
            table[i][j] = table[i-1][j]
        else:
            table[i][j] = max(table[i-1][j], table[i-1][j-weights[i-1]]+values[i-1])

#Obtaining items that were selected
final_value = table[-1][-1]
i = len(table)-1
j = len(table[0])-1
items_selected = []
while final_value != 0:
    if(table[i][j] != final_value):
        j = j-1
    else:
        if table[i-1][j] != table[i][j]:
            final_value = final_value - weights[i-1]
            items_selected.append(i)
        i = i-1
items_selected.sort()
for i in range(len(table)):
    print(table[i])
print("\nMaximum value: ", table[-1][-1])
print("Items selected: ")
for i in range(len(items_selected)):
    print(f"Item {items_selected[i]}")