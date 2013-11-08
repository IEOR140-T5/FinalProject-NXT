FinalProject - Milestone 3
============

1.    Introduction -- Prototype of Bomb Disposal Robot. 

1.1. Name of the assignment:
     Milestone 3 Localization -- the goal of this milestone is to demonstrate and verify the accuracy of our Locator. 

1.2. Team Number 5. Team members include Corey Short (Mechanical), Khoa Tran (Programmer), Peter Nguyen (Programmer), and Trevor Davenport (Report).

1.3. Approximate number of person hours spent on the project: 20+ Hours.

2.    Experimental work  (if any):

2.1. Experiment description and purpose:
     Coding and experimental work in preparation for final project.
     Design a scanning strategy and verify the accuracy of the Locator and its Scanner.

2.2. Listing of data:
     Insert spreadsheet:

2.3. Calculations and analysis:
     Mean and standard deviation of angle between beacons, and x, y and also the
     heading error for the 32 observations at each location:

2.4. How results were used in your code: 
     The results were used to calculate the correct heading to set for position.

2.5. Classes and Responsibilties:
     
          Scanner Class -- The main class all our robot depends on. What we have been building on the entire course.
          Locator -- Our locating algorithm class. This class uses mathematical calculations to locate and scan for beacons.
          
          Class Dependency Diagram:
          
          
![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/MS3_class_diagram.png)      

2.6 Software design:
     The algorithm scans for beacons based on its current relative bearing position to the other.
     

3. The most interesting/challenging/difficult parts of the project: 
     The most challenging part was writing the algorithm to convert the 
     bearing distance to a heading for the position.

4.   Links to source code and JavaDocs: 

Steam/Team5/FinalPoject-NXT/Milestone 3/

