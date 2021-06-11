#Asking for the values
cap  = int(input("Insert capacity of the knapsack: "));
num_items = int(input("Insert the number of items: "));
weights = [];
values = [];
print("Insert the items in format: weight value");
for i in range(num_items):
    item = input(f"Item {i+1}: ");
    weights.append(int(item.split()[0]));
    values.append(int(item.split()[1]));

#Starts Greedy algorithm
ks_weight = 0;
total_value = 0;
index_mv = 0;
more = 0;
while ks_weight < cap and more == 0:
    max_value = 0;
    for i in range(len(values)):
        if values[i] > max_value:
            max_value = values[i];
            index_mv = i;
        if values[i] == max_value:
            if weights[i] < weights[index_mv]:
                max_value = values[i];
                index_mv = i;
    ks_weight = ks_weight + weights[index_mv];
    if ks_weight > cap:
        ks_weight = ks_weight - weights[index_mv];
        more = 1;
    else:
        total_value = total_value + max_value;
        values.pop(index_mv);
        weights.pop(index_mv);

#Result
print("Maximum value: ", total_value);
print("Capacity used: ", ks_weight);
