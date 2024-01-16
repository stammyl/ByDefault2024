# ByDefault
 Docker Management System
--Client Application (client-app):

Contains the graphical user interface (GUI).
Hosts a RestApiClient to interact with the REST API.

--Server Application (server-app):

Houses the monitoring logic and a database thread.
Includes a MonitorThread to observe Docker instances.
Utilizes a DatabaseThread to manage database updates based on monitored data.

--REST API Application (restapi-app):

Acts as the backend server.
Listens for REST API hits from client applications.
Includes an ExecutorThread responsible for handling various tasks triggered by REST API requests.
Workflow:

++Client Application Workflow (client-app):

Users interact with the graphical user interface.
GUI communicates with the RestApiClient to send requests to the REST API.
Requests may include commands to start or stop Docker instances.

++Server Application Workflow (server-app):

MonitorThread continuously monitors the status of Docker instances.
Monitored data is updated in a shared data structure or database.
DatabaseThread reads from the shared data structure or database and updates the backend storage.

++REST API Application Workflow (restapi-app):

Listens for incoming REST API requests.
ExecutorThread processes incoming requests.
Executes actions such as starting or stopping Docker instances based on the REST API hits.

Communication:

Client to Server (client-app to server-app):

GUI in the client app communicates with the RestApiClient.
RestApiClient sends REST API requests to the backend server (server-app).


All 3 apps have to run independently for testing** All must be run by navigating in the head folder and run with different cmd terminals
"mvn clean install exec:java"