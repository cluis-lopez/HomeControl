# Home Control Project


![Diagram](https://github.com/cluis-lopez/HomeControl/blob/master/git_images/EsquemaHomeControl.jpg)

The control system is a simple thermostat application with logging capabilities and a modern, mobile & web based, User Interface:

- The system allows three operating modes:

  1. OFF: the boiler is OFF, environment is measured and registered but no action is taken.
  2. MANUAL: the user defines a fixed goal temperature for the environment and the controller powers ON-OFF the boiler to reach that goal.
  3. PROGRAMMED: the user defines a weekly calendar for the controller to act upon, i.e. weekdays from 07:00 til 09:00 and 19:00 til 23:00 temperature at 21 ºC, 17ºC otherwise; weekend days 21ºC from 08:00 until 23:00.


- A main controller (a Java daemon-like program) running in a Raspberry Pi monitors the environment measuring temperature and humidity conditions. Within certain periods, the controller wakes up and logs the state of the system in a flat file: current temperature and humidity, target temperature, state of the boiler (ON/OFF) and mode of operation. Depending on the operating mode, it retrieves the goal temperature and acts on the boiler to maintain it.

- The Java webapp, running over Tomcat also in the Raspberry, provides the user interface allowing the user to set the operational mode, the goal temperature (if MANUAL) or to program the weekly calendar when in PROGRAMMED mode. The webapp, permits also to retrieve and draw charts of historical data

The above system is reachable inside the home private network (192.168.1.255) without any special authorization as you need the home wifi credentials to gain access. 

For Internet access and control we use a gateway based on Pusher (cloud based web socket services) and an additional webapp hosted at Google App Engine:

- Pusher will maintain the chat-like message passing gateway between the internal server in the Raspberry and the external webapp at Google. In order to do so:

   - A listener Java app (Listener.java) running as a daemon in the Rasperry will open a channel with Pusher listening to messages comming from there. Those messages are triggered by a servlet running at Google requesting actions in the Raspberry.
   
   - The HTML5-Javascript client running in the user web browser (mostly mobile) will do the same after auth request. If authentication is successful, will also join the same "chat" channel on Pusher and willl send JSON requests to the "chat server" runnning on Google. Pusher will drive these requests to the Node.js mini-app in the Raspberry which in turn will invoke the internal services (the same Servlets used by the internal application).