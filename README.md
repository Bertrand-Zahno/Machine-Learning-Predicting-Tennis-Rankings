# Capstone Project 

This is the Capstone Project of the EPFL Extension School Course: "Applied Data Science: Machine Learning", implemented by Bertrand Zahno. 
The topic is about predicting new rankings of tennis players within Switzerland.

Swisstennis is the umbrella organization which registers all the matches of all the players during the year and
releases new rankings every 6 months calculated by a published formula. The goal of this project is to challenge
the published rankings with the given formula (which is obviously not 100% accurate) and do better calculations or 
predictions with machine learning methods.

All the match results are published by Swisstennis at: https://www.swisstennis.ch/player-search for each player individually.
There is no official given dataset to directly test on. Therefore the data had to be collected and assembled by
screen scraping the Swisstennis webpages. 


The programming for this screen scraping part was done in Java and can be found in the folder "java-screen-scrap".

So this Capstone project consists of two (self-contained) subprojects:
- the java runner responsible to collect all the necessary data
- the jupyter notebook which imports the data as csv files and does the further investigations. All the details are in the 
jupyter notebook "Capstone-Project-Bertrand-Zahno.ipynb".


### Java Runner Project
This project is organized as JUnit Tests which call the main classes to collect and process the data. 


The starting point was: https://www.swisstennis.ch/player-search?last_name=&first_name=&licence_number= where in theory
one could page through all the pages and retrieve all the players data. Unfortunately those pages contain "wholes" and
all the players needs to be parsed in order to retrieve alle the opponents of the players and finally a complete list can be established. Then for each player which has a unique ID, get the corresponding result page. For instance to get all the results of Roger Federer, the page https://www.swisstennis.ch/user/1758/results-summary can be read. 

The program parses each html file and extracts the results and stores it in a local database. From there two files are exported to be used in the jupyter notebook:
- players-2-2019.csv: contains players data.
- matches-2-2019.csv: contains each match played during one season (1 year)

Thoses files are in the directory "./03 data".

Technologies used: Spring boot (JPA) to write to the MariaDB database, JSoup to access the webpage and to parse the html.


### Directory structure:
./01_java-screen-scrap : the java programm to collect and assemble the data

./02_samples           : some samples of Swisstennis html files

./03_image             : the images referenced in the jupyter notebook

./04_data              : contains the input data for the periodes

./                     : main notebook and readme file


 