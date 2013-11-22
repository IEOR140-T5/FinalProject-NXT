FinalProject - Milestone 2
============

1.    Introduction -- Prototype of Bomb Disposal Robot. 

1.1. Name of the assignment: Milestone 2 -- Scanner beacon bearing and wall distance accuracy. Due 10/31.

1.2. Team Number 5. Team members include Corey Short (Mechanical), Khoa Tran (Programmer), Peter Nguyen (Programmer),
     and Trevor Davenport (Report).

1.3. Approximate number of person hours spent on the project: 20+ Hours.

2.    Experimental work  (if any):


2.1. Experiment description and purpose: 

The purpose of this Milestone is to successfully modify and test the Scanner class to return an array of Beacon bearings. The purpose of this project is to create a basis for our final project which includes being able to detect a bomb.

Part 1: we place the robot at 2 different location (20, 30) and (210, 240). The scanner will scan trough a a range of 200 degrees and will find a bearing for that the light beacons are. Because the light scanner will sweep twice so we will actually have 2 values of each beacon bearing, then we will take the average of those values. The purpose of this part is to measure the bearing to each beacon.

![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/20-30.png)

![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/240-210.png)

![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/chart-part1.png)


Part 2: We place the robot at 3 difference location, 30cm, 90cm and 180 cm facing the Ox direction. At each location, the robot will record 2 values from the ultrasonic to the 2 sides. That will be added up to the total width of the hall. The purpose of this is to make sure the robot records the right value for the distance between two wall, also to make sure the distance calculation is correct and accurate.

![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/chart-part2.png)


Part 3: We place the robot at 2 difference location (20, 30) and (210, 240). For each of the location, we record the bearing for each beacons 8 times. The purpose of this experiment is to make sure we are having the right and consistant values for the same object after a continous times of scan.

![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/chart-part3.png)


2.2. Listing of data: 

Within our project folder you will find 3 separate Excel Files (Beacon Scanning Chart, Wall Distance Tests, and a Final Scanning Test). When using the coordinates (20,30) we found that there was a difference of degree is 3-4 degrees off. When using the coordinates (240, 210) the discrepancies were nearly 1-4 degrees off.

2.3. Calculations and analysis:    

Part 1: The angle is calculate based on the basic trigonometry from the illustration below:

![Chart](https://raw.github.com/IEOR140-T5/FinalProject-NXT/master/reports/trig.png)


                                   (20,30)
                                   1. tan(a1) = 30/20 ===> arctan(a1) = 56 degrees
                                   2. tan(a2) = (241-30)/20 ===> arctan(a2) = 84 degrees ===> -84 degrees
                                   
                                   (240,210)
                                   1. tan(a1) = 210/240 ===> arctan(a1) = 41 degrees
                                   2. tan(a2) = (241-210)/240 ===> arctan(a2) = 7.3 degrees

Unfortunately, the values that we got never be accurate. We have tried with different motor speeds range from 10-100. The reason is because at first location (20, 30), we can get the right value for the left beacon bearing because it is right next to the robot (a slightly different value). However, for the right beacon, the light sensor takes more time to get the value due to the distance from the robot to the right beacon. The same reason can be applied to the scan at location (210, 240) too. Because that point is far away from the robot so the reading needs a lagging time in order to travel back to the robot, but at the time that light sensor is at another angle. That explains why the data we got never be accurate.


Part 2: After getting the distance from both side by using UltraSonic detector, we add the value up to get the width of the hall. The values that we got is slightly different from the actually length (1 centimeter off). The reason is because we are not sure where is the exact point that the ultrasonic sends out the signal. Also when it turns to another side, the point that it starts sending signal again maybe a little off from the one that it sent signal previously. That's why we didn't get the accurate values for the distance when adding 2 values up.

Part 3: Part 3 we don't have the accurate values for the bearings. The reason is as same as first part, when we lightsensor sends and receives signal, it's a lag time between the two actions so it may be a slightly off result.


2.4. How results were used in your code: 

The results were used in our Scanner.java class. Based on our calculations, we used them to determine the Standard Deviation for the angles (a1,a2) which are the 2 values to the beacons. Also, we can know that the distance reader from the ultrasonic sensor is accurate and we can use it to detect or measure the distance to an object.

3. The most interesting/challenging/difficult parts of the project: 

The most difficult part of this Milestone was building the robot and hanlding all of the error checking. Meant that we had to do a lot of changes and adjustments for the hardward in order for the robot to get the right result. The speed and acceleration of the motor also plays an important role for the accuracy. The faster it is, the more inaccurate we get.

4.    Links to source code and JavaDocs: 

Steam/Team5/FinalPoject-NXT/Milestone 2/
