mysql -u cs144 CS144 < drop.sql
mysql -u cs144 CS144 < create.sql

# Run parser
ant run-all

# Remove duplicates from .dat files
touch data.tmp

for file in "parsed"/*.dat
do
    sort -u $file > data.tmp 
    cat data.tmp > $file
done

rm data.tmp

# Load data into db
mysql -u cs144 CS144 < load.sql

# Remove .dat files directory
rm -rf parsed/

