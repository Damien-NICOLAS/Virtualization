# TpCloud

Code for Tutorial 2 Retrieval of information from server available in both Java and Python (2 codes). Rest of the project only available in Java. The python version can be found in the Virtualization Python file and the java project can be found in the Java file.

Part 1: Retrieval of data: this occurs in the class Monitor, we instanciate said monitor in the class manager main. Creation and Start of a container: this is implemented in the class Generator Main.

Part 2: Design and implementation of two applications to automatize the virtualization. CT generator : this application will be in charge of the creation of CT, randomly on the two servers with a proportion of 66% on server 3, and 33% on server 4, the servers attributed to our team. The condition to create a new CT is if the memory on both server is inferior to the memory allowed on each of them. This allowed memory had been calculated as 16% of the total memory of the server. The creation of a new CT is driven by a statistic distribution with an exponential law and a lambda of 30s.After the creation, it is started.In order to start the CT, we have to take into account a small delay so that the CT has the time to be properly created and named. You can find all of this in the GeneratorMain class.

Cleaning: To avoid flooding our servers with a large amount of CTs while testing, we implemented a CleanProxMox class which we will run between each test to delete our CTs from the server.

Cluster manager: This application will periodically monitor server status, and acts on the deployed containers based on the following algorithm: As soon as the load of one of your CT exceeds 8% of the host memory (i.e. half the resources accessible on the server), perform load balancing between your servers ; As soon as the load of one of the servers exceeds 12% of the total load, stop the oldest CT to support the creation of new ones. To delete the oldest CT, we do it in the OffLoad() function in the controller class. We gather our CTs on a server and we compare their uptime. The one with the highest uptime is then stopped and deleted.
This will be implemented within the Analyze, Controller and Monitor classes. You will need to run the Manager Main class to see it.

For further explanations, refer to the Google doc of the laboratories -Lab 2 Parts 1 and 2
