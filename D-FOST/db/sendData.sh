#!/bin/bash
# My first script

mongoimport --host cse120appcluster-shard-00-01-buzvh.mongodb.net:27017 --db TestDB --collection documents --type json --file ~/Documents/college/CSE/120/TK-DJ/db/data.json --jsonArray --authenticationDatabase admin --ssl --username dkumar7 --password WeBetterWinThisShit
