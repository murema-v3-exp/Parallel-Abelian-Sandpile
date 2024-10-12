import matplotlib.pyplot as plt
import pandas as pd

# Loading data
data1 = pd.read_csv('Six_Cores.csv')
data2 = pd.read_csv("Four_cores.csv")


# Extract columns for Six Core PC
grid_sizes1 = data1['Grid Size']
serial_times1 = data1['Serial Time']
parallel_times1 = data1['Parallel Time']
speedup1 = serial_times1 / parallel_times1

# Extract columns for 4 Core Senior LabPC
grid_sizes2 = data2['Grid Size']
serial_times2 = data2['Serial Time']
parallel_times2 = data2['Parallel Time']
speedup2 = serial_times2 / parallel_times2

# Plot Speedup vs. Grid Size for both PCs
plt.figure(figsize=(12, 8))
plt.plot(grid_sizes1, speedup1, marker='o', linestyle='-', color='r', label='6 cores (Windows Machine)')
plt.plot(grid_sizes2, speedup2, marker='s', linestyle='--', color='b', label='4 cores (Ubuntu Machine)')
plt.title('Speedup vs. Grid Size for 2 different computer architectures', fontsize=14)
plt.xlabel('Grid Size', fontsize=14)
plt.ylabel('Speedup', fontsize=14)
plt.xscale('log')  # Using log scale because grid sizes vary widely
plt.yscale('linear') 


plt.grid(True)
plt.legend(fontsize=12)
plt.show()