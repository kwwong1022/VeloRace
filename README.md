# VeloRace
 
<p>VeloRace is an android application that allows cyclists to use their mobile phone as a bike computer.</p>

<p>The position of VeloRace is between advanced and professional use. It provides more information than the bike computer app on the market and takes advantage of the larger screens of mobile phones. Different sensors are also used to enhance the user experience significantly.</p>
<br>
This application has four main functions:
1. Bike computer to display the user's riding information 
2. Location guidance 
3. Torch function 
4. Fall Detection 
5. Ride Result
<br>
<h2>Bike Computer (Location Services, Accelerometer, Magnetic Field, Custom View)</h2>
<p>Riding information mainly uses google play location services to obtain most of the riding information.</p>

<img src="https://64.media.tumblr.com/8adc55729c44686884e4494bfe08213e/669a44cd861964d3-66/s540x810/a631a8a68b323a37bc78188896fcda3c31e56069.png" width="500px">

Riding information is divided into two parts: 
1. Calculated information is displayed on the left part. 
2. On the right part, it converts the obtained data and displays it in a graph, showing the changes in the data within 30 seconds. Then the user can get a more concrete idea from it.

<img src="https://64.media.tumblr.com/08bffff5c402d3459c70873eee398793/669a44cd861964d3-2e/s250x400/c7a4f0ff75e23dadcda36f30e8e05e4142047415.png" width="500px">

<p>Then it shows the current location of the user. This function combines an accelerometer, a magnetic field sensor, and Google Maps. The map will always be turned to the angle where the user is facing. The user can use it to evaluate the route.</p>

<img src="https://64.media.tumblr.com/188d392a1905aa4507d7c2c753e31d60/669a44cd861964d3-d0/s250x400/fcfa4a3653544035aca6f1228445521e529b6af8.png" width="500px">

<p>Secondly, the data from the accelerometer was then used to achieve the automatic start and pause function to increase the user experience. Using an accelerometer instead of a GPS can avoid the situation where the bike computer stops processing the user's riding information when the GPS data is not received.</p>
<br>

<h2>Location Guidance (Location Services)</h2>
<p>The second function is location guidance. Sometimes cyclists may not know their location when they go to unfamiliar places. With this function, cyclists can see the street name of their current riding location.</p>
<br>

<h2>Torch Function (Light Sensor, Proximity Sensor, Camera Services)</h2>
<p>The third is the torch function, which uses the flashlight from the camera service, which turns on when the user presses the button. However, it is dangerous for users to operate on the screen when riding. Therefore, by default, the application will automatically check the brightness of the environment and enable a swipe to turn on flashlight function when the brightness is too low.</p>
<br>

<h2>Fall Detection (Accelerometer)</h2>
<p>The fourth function is fall detection, which will trigger the phone call of the phone when the user has fallen during the ride. Users can also set up the emergency contact number in the setting.</p>

<img src="https://64.media.tumblr.com/34e50814c8e08b6b8a8989ce6d19b2a1/669a44cd861964d3-df/s250x400/242a89694885eb38b953ecfb37433e4b01f235f1.png" width="200px">

<p>At the bottom of the cycling activity, there is a bar that displays the user's current balance point. The color of the bar will change with the angle of inclination. When it reaches a dangerous angle, it will turn to red, and vibrate. The vibration will be transmitted to the user through the handle of the bicycle to warn the user.</p>
<br>

<h2>Ride Result (Custom View)</h2>
<p>The Last function is ride result, which generates a result graphic based on the ride information by using custom view.</p>

<img src="https://64.media.tumblr.com/b2ea34870b34a019d690b27e708ba28c/c464f9e897bfbe96-7f/s540x810/97d7fedb0c094190bfba33e3a8727dcd0e30e642.png" width="500px">

<p>The result graph contains 2 parts:
1. The first part is a graph on the top, which is based on the ride time, average speed and max speed. Those data are normalised to a bar to visualise the quality. There is also a pie chart visualising the normalised total ride quality.
2. The second part is a line chart on the bottom, which shows the speed and elevation during the ride.</p>

<h2>Download</h2>
./app-debug.apk
