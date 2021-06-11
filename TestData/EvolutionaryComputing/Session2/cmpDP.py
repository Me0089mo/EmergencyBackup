#Asking for the values required
change = int(input("Insert total change required: "))
num_den = int(input("Insert the number of denominations available: "))
denom = []
for i in range(num_den):
    denomination = int(input("Denomination: "))
    denom.append(denomination)
denom.sort()

#Starts DP algorithm
mount = [[None]*(change+1) for i in range(num_den)]
for i in range(change+1):
    mount[0][i] = i
for i in range(1,num_den):
    for j in range(change+1):
        if denom[i] > j:
            mount[i][j] = mount[i-1][j]
        else:
            mount[i][j] = min(mount[i-1][j], mount[i][j-denom[i]]+1)

#Calculating number of coins of each value were required
num_conins = mount[-1][-1]
selected = {}
i = len(denom)-1
j = change
while num_conins != 0:
    if mount[i][j] != mount[i-1][j]:
        if not denom[i] in selected:
            selected[denom[i]] = 1
        else:
            selected.update({denom[i]:selected.get(denom[i])+1})
        num_conins -= 1
        j = j-denom[i]
    else:
        i = i-1

for i in range(len(mount)):
    print(mount[i])
print("Less number of coins to complete change: ", mount[-1][-1])
print("Coins taken: ")
for i in selected:
    print(f"${i}: {selected[i]} coins")