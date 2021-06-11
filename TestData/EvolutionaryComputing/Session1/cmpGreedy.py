#Asking for the values required
change = int(input("Insert total change required: "));
num_den = int(input("Insert the number of denominations available: "));
denom = [];
for i in range(num_den):
    denomination = int(input("Denomination: "));
    denom.append(denomination);
denom.sort(reverse=True);

#Starts Greedy algorithm
num_coins = 0;
#change_given = 0;
for i in range(num_den):
    if change > 0:
        c = int(change/denom[i]);
        num_coins = num_coins + c;
        change = change-(c*denom[i]);
    else:
        break;

print("Less number of coins to complete change: ", num_coins);
