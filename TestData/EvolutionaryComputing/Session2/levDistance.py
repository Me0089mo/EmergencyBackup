str1 = input("Insert the first char sequence: ")
str2 = input("Insert the second char sequence: ")

changes = [[0]*(len(str1)+1) for i in range(len(str2)+1)]

for i in range(len(str1)+1):
    changes[0][i] = i
for i in range(len(str2)+1):
    changes[i][0] = i
for i in range(1,len(str2)+1):
    for j in range(1,len(str1)+1):
        same_ind = 1
        if str1[j-1] == str2[i-1]:
            same_ind = 0
        min_lev = min(changes[i-1][j]+1, changes[i][j-1]+1, changes[i-1][j-1]+same_ind)
        if min_lev == 0:
            changes[i][j] = max(i,j)
        else:
            changes[i][j] = min_lev

for i in range(len(changes)):
    print(changes[i])
print(f"Levenshtein distance: {changes[-1][-1]}")