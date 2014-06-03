#Why you need a Mobile Key

When the mobile client connects to the server you need to identify and recognize each mobile device at each connection. For this purpose you need to generate a Mobile Key which is Unique and can be stored together with additional data about the mobile device (f.e. operating system/version, total connections...) 


Small example: It's possible to Up-vote an Article written by a user (woosh! I really like this article!). For this purpose you just need to create a new Vote-Node which has a relationship to the Article (n->1). And the problem starts here: you can't see who created this Vote and the user can create as many votes as he like.


Of course you can create a new User for each mobile device and set the owner-Relationship to this node. But that's not what you want in the mobile Developement. The mobile application is in the first place register-free! Otherwise the user is discouraged and is stopping to use or even removes your application.


Thats why the client needs to generate a Unique Mobile Key! The Mobile Key is generated at the first time by the server using your application. The Key will be stored inside the client and checked for validity each time the application starts.


Lets go back to the example: The User creates a Vote-Node. This time you send your Mobile Key along with the data (Otherwise you can't create a new Vote-Node from the application). To make sure that this Mobile Key creates only one Vote-Node with this specific article the server hash or concatenate both uuid's and generate a new unique id which is connected to the article and your mobile key. This can be stored inside a unqiue field (in the Vote-Node). Now when the same mobile key try's to create a new Vote with the same article then the same id will be generated and since this field is unique means that no new Vote will be created.


Later, when the user wants to create a new account. You just need to add the relationship (1->n) between the Mobile Key and the User.


-Alex