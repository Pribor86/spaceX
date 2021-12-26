<h2>Warp core matter/antimatter intermix regulator</h2>


I know it sounds complicated, but there is nothing to it. To make spaceship go fast, you need a warp engine. And the heart of the warp engine is the matter/antimatter reaction chamber where both substances are mixed together in equal ratio. Matter reacts with antimatter and this creates energy. But it is important to keep them mixed at (close to) equal proportions and to make sure we don't add too little or too much of both at any moment in time. Fortunately the engine we have is pretty tolerant and can handle quite large deviations before it blows up.

Your goal is to run the engine and keep it running for at least minute to pass. If you can get reliable successful runs, we want to see your code. Share your git repository with us.

To keep things regulated you have access to the following REST API's:

<b>Start</b> - This fires up the engine. And gives you access code to other apis. Run this first.
<b>Status</b> - This gives you the status of the engine. Don't call this api more than once per second. There are two parameters in response:
<br><b>intermix</b> - number between 0 - 1, if it is less than 0.5, it means more antimatter than matter is being injected, if it's more than 0.5 it means more matter than antimatter is being injected. Your goal is to keep it close to 0.5
<br><b>flow rate</b> - this shows how much of both matter and antimatter is being injected into the chamber. It can be OPTIMAL, meaning flow rate is good, HIGH, meaning too much of components are being injected or LOW, meaning not enough is being injected. Keep this in mind when making adjustments.
Adjust matter/antimatter - You can pass your adjustment to these apis. You can make both positive and negative adjustments. Make sure you don't pass values greater than 0.2 to the api.