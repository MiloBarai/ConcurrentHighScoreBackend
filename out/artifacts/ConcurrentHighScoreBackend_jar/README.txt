Compiled with java 12. REQUIRES JAVA 10+ due to the user of "var" when programming.

To run the program:
java -jar .\ConcurrentHighScoreBackend.jar "<port>"
(if no port is provided 8080 is used)

Application overview:

The main class is in the Application class creating a Server class and starting it.
The Server class simply creates an httpServer on the localhost with the input port.
    Executor FixedThreadPool has been used for this server and set to 10.
    There would need to be some type of analysis if 10 is enough or more/less should be set
    for better performance.
    FixedThreadPool was used to avoid the cost of creating the threads and setting a 
    max for how many threads can be created for the incoming calls.
    
The Server has a context of "/" tied to a HttpRequestHandler, which handles all incoming requests.
    It would had been perfered to be able to set dynamic links instead. (eg. context("/<userId>/login")).
    But the HTTP create context did not support this and it had to be created manually which is what the HttpRequestHandler does.

Some simple input validation has been added here as well to quickly respond when incorrect calls are made.
	An improvement could be made in this class. The paths to the requests are hardcoded into separate variables. 
	These could be put in a map/list together with the method to handle the request instead.

The handler transfer all input controlled calls to the ServerController which in turn performs the actions.

Session handling is done through a singleton so that all threads use the same instance and sessions are stored in a hashmap
	with the session id as the key for quick access. (The session contains userId and expiration date). 
	When a session is created it is added to a LinkedList which is used as a queue for cleaning up expired sessions.
	The session key is unique however in a real world application it would had been considerably more secure implementation.
	(i hope :] )

Highscore handling is also done through a singleton. The storing of values has been done in an ArrayList. 
	since there were only 15 values stored the data structure of choice did not have a large impact on overall performance.

Looking forwards to hearing from you and getting some feedback!