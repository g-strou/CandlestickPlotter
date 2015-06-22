# CandlestickPlotter
A Candlestick Plotter

This is a part of a project I made while working in EYBOKAT. The full program was designed to connect to the Trader Workstation of Interactive Brokers (TWS) through the JAVA API, download real time prices for Bund stocks and apply various strategies. 

https://institutions.interactivebrokers.com/en/index.php?f=1537

This is mainly the graphical part which draws a candlestick chart given some historical data of a day (Open, High, Low and Close prices every minute). The input file contains one line for each minute with values in CSV format, as is shown on “demoData.txt”. The code concerning the real time data and the TWS API has been removed. 

The program reads these values and draws a candlestick chart. There are two modes, “Historical” and “Simulator”. Both read a data file, but simulator was designed to simulate the real time behaviour for a given file (with some differences since the TWS API retrieved values every 5 seconds, whereas the historical data files only stored the prices for minute intervals. The user in either case can use the “Demo” tab if the “demoData.txt” file is in the same folder with the executable jar, or search for a file with the “Open File(s) tab”. 

The interface is simple. The functionality is described at the “Instructions.png” file for the simulator  mode. The historical mode is the same except of the slider window. 

The added lines were used to implement some functionality which has been removed at the present. The project may be updated at some time to include some of it. 

The “src” folder contains the code (The NodeManager class is not currently used).

Since the group I worked was a small one and I was the only one working on the program, comments are rare. If you are interested feel free to contact me.
