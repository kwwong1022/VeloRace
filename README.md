# VeloRace
 
VeloRace is an android application that allows cyclists to use their mobile phone as a bike computer.

The position of VeloRace is between advanced and professional use. It provides more information than the bike computer app on the market and takes advantage of the larger screens of mobile phones. Different sensors are also used to enhance the user experience significantly.

This application now has four main functions:
1. Bike computer to display the user's riding information 
2. Location guidance 
3. Torch function 
4. Fall Detection 
5. Ride Result

<h2>Bike Computer (Location Services, Accelerometer, Magnetic Field, Custom View)</h2>
Riding information mainly uses google play location services to obtain most of the riding information. 

Riding information is divided into two parts: 
1. Calculated information is displayed on the left part. 
2. On the right part, it converts the obtained data and displays it in a graph, showing the changes in the data within 30 seconds. Then the user can get a more concrete idea from it.

Then it shows the current location of the user. Sometimes the cyclist may not know the road ahead. This function combines an accelerometer, a magnetic field sensor, and Google Maps. The map will always be turned to the angle where the user is facing. The user can use it to evaluate the route.

Secondly, the data from the accelerometer was then used to achieve the automatic start and pause function to increase the user experience. Using an accelerometer instead of a GPS can avoid the situation where the bike computer stops processing the user's riding information when the GPS data is not received.

<h2>Location Guidance (Location Services)</h2>
The second function is location guidance. Sometimes cyclists may not know their location when they go to unfamiliar places. With this function, cyclists can see the street name of their current riding location.

<h2>Torch Function (Light Sensor, Proximity Sensor, Camera Services)</h2>
The third is the torch function, which uses the flashlight from the camera service, which turns on when the user presses the button. However, it is dangerous for users to operate on the screen when riding. Therefore, by default, the application will automatically check the brightness of the environment and enable a swipe to turn on flashlight function when the brightness is too low.

<h2>Fall Detection (Accelerometer)</h2>
The fourth function is fall detection, which will trigger the phone call of the phone when the user has fallen during the ride. Users can also set up the emergency contact number in the setting.

At the bottom of the cycling activity, there is a bar that displays the user's current balance point. The color of the bar will change with the angle of inclination. When it reaches a dangerous angle, it will turn to red, and vibrate. The vibration will be transmitted to the user through the handle of the bicycle to warn the user.

<h2>Ride Result (Custom View)</h2>
The Last function is ride result, which generates a result graphic based on the ride information by using custom view. The result graph contains 2 parts:
1. The first part is a graph on the top, which is based on the ride time, average speed and max speed. Those data are normalised to a bar to visualise the quality. There is also a pie chart visualising the normalised total ride quality. 
2. The second part is a line chart on the bottom, which shows the speed and elevation during the ride.
