
#1. drop any existing tables with same name before creating new tables
mysql CS144 < drop.sql

#2. create new tables
mysql CS144 < create.sql

#3. build and run parser to generate .csv files
ant run-all

#4. sort to remove possible duplicate
sort -u sellers.csv > sellers_sorted.csv;
sort -u bidders.csv > bidders_sorted.csv;
sort -u categories.csv > categories_sorted.csv;

#5 load .csv files into the tables
mysql CS144 < load.sql

#6 remove all temporary files
rm bidders.csv;
rm sellers.csv;
rm categories.csv;
rm bids.csv;
rm itemList.csv;
rm sellers_sorted.csv;
rm bidders_sorted.csv;
rm categories_sorted.csv;
