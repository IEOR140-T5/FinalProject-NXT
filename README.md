FinalProject - Milestone 2
============

1.    Introduction -- Prototype of Bomb Disposal Robot. 

1.1. Name of the assignment: Milestone 2 -- Scanner beacon bearing and wall distance accuracy. Due 10/31.

1.2. Team Number 5. Team members include Corey Short (Mechanical), Khoa Tran (Programmer), Peter Nguyen (Programmer),
     and Trevor Davenport (Report).

1.3. Approximate number of person hours spent on the project: 20+ Hours.

2.    Experimental work  (if any): ![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/chart-part1.png)

2.1. Experiment description and purpose: The purpose of this Milestone is to successfully modify and test the Scanner class
                                         to return an array of Beacon bearings. The purpose of this project is to create a basis
                                         for our final project which includes being able to detect a bomb.

2.2. Listing of data: Within our project folder you will find 3 separate Excel Files (Beacon Scanning Chart, Wall Distance Tests, and a
                      Final Scanning Test). When using the coordinates (30,20) we found that there was a difference of roughly 5.36%.
                      When using the coordinates (240, 210) the discrepancies were nearly 14.29%.

2.3. Calculations and analysis:    

                                   (20,30)
                                   1. tan(a1) = 30/20 ===> arctan(a1) = 56 degrees
                                   2. tan(a2) = (241-30)/20 ===> arctan(a2) = 84 degrees ===> -84 degrees
                                   
                                   (240,210)
                                   1. tan(a1) = 210/240 ===> arctan(a1) = 41 degrees
                                   2. tan(a2) = (241-210)/240 ===> arctan(a2) = 7.3 degrees

2.4. How results were used in your code: The results were used in our Scanner.java class. Based on our calculations, 
                                         we used them to determine the Standard Deviation for the angles (a1,a2).

3.     The most interesting/challenging/difficult parts of the project: The most difficult part of this Milestone was building
                                                                        the robot and hanlding all of the error checking.

4.    Links to source code and JavaDocs: Steam/Team5/FinalPoject-NXT
