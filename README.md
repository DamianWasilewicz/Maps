
#Maps 3 + 4 #
## ABOUT ##
Maps implements panning, zooming, and shortest-path finding functionality between street intersections in Providence, centered around Brown University. 
You can highlight shortest path between any two locations selected by clicking. We cut down loading time by dividing the map into 1000+ tiles and utilizing front-end caching for faster loading of the map upon rescaling
We run a Java backend to find the shortest path between nodes, using MySQL and Google Guava caching to operate on over 1.3 million real paths in Providence

Landing Page centered at Brown University campus:
![Alt text](./images/Overview.png?raw==true "Overview")

Path finding:
![Alt text](./images/Route.png?raw==true "Path")

Panning:
![Alt text](./images/Pan.png?raw==true "Pan")

Zooming in:
![Alt text](./images/"Zoom In".png?raw==true "Zoom in")

Zooming out:
![Alt text](./images/"Zoom Out".png?raw==true "Zoom out")

## Design Overview ##
___

### React Components Overview ###

**Maps.js:**|**Route.js**|**Canvas.js**|**CheckinBox**
------------|------------|------------| ---------------- |
Stores most of the frontend logic. Communicates with the serves, delegates tasks to various components.| Controls the textbox and button elements by which a user can request a short route to display on the map| The canvas component renders the map. Initial view set on Brown's campus. | This components renders user checkin inputted by the server run by CheckinThread. 

### Program Overview  ###
* **Drawing the Map:** we use a canvas element with state references representing map bounds to draw all ways founds in 
  the connected database that are within those bounds. To optimize performance we define a constant c and segment the map into 
  tiles, with dimensions (c by c). The ways in each tile are cached with a guavecache in the backend, and when passed to the
  react component they are once again cached in the frontend. This allows us to draw the map relatively quickly. 
* **Zooming:** an event listener set to mouse wheel zooms in and out of a given map view by resetting the canvas bound state references and redrawing all ways in the 
  new domain. 
* **Panning:** an event listener set to mouseDown records a coordinate, then a second event listener set to mouseUp records a second coordinate. 
  The change in latitude is the difference between the first and second coordinates' latitude, and the change in longitude follows the same logic.
  On mouseUp we update the map bound state references by the change of latitude and change of longitude to acheive the desired vertical and horizontal shift, that constitute panning. 
* **Route:** Routes can be requested by either clicking on the map or inputting information into the textboxes. The 4 input textboxes alter the 
   state references used in a post request to the backend that is triggered by a magicButton. To request routes by clicking, double click any
   point to mark it selected (a red circle will appear around it) and then double click a second point on the map to display the shortest route between them.
* **Checkin server** The checkin server produces user checkin data. We render this data by mapping it to a component that on click requests our database to query specific user data
and display it via mapping specific user checkin data to another component, this time on click with the ability to delete user data (ideally).
  

## Handin Questions ##

---

### Division of Labor ###

* **Equally shared**
  * overall design and structure of program
  * general debugging
* **Nadav (ndruker)**
  * Dijkstra and general infrastructure from maps 1 + 2
  * backend commands, database queries
  * readme, src, documentation
* **Damian (dwasilew)**
  * extensible REPL from Maps 1+2
  * Maps.js general syntax and functionality
  * connecting components in react
  * debugged route from click

### Known Bugs ###

* **Bugs**
  * Route displays the corresponding path between two points in delay. 
  * Sometimes, the shortest path is not the rendered path (this is probably a problem with our previous implementation of maps 1+2)

### Building and Running with the GUI ###

* **to build:** *mvn package*
* **to run repl:** *./run --gui*
* **to run backend server:** *npm start*
* **to run the checkin server:** *we tested by running on our local machine: ./cs032_maps_location_tracking_py3 8080 5 -s*




#Maps 1 + 2 #
## Design Overview ##
___

### PROCEDURE ###

**Main:**|**Repl**|**CommandHandler**|
------------|------------|------------|
starts the program, runs the server, runs the repl, ready to execute the commands.| an infinite while runs while receiving input from the keyboard. The input is parsed for the command and arguments and then executed.| Sets up hashmap that maps commands to instances of a private class that represents each command.

### DATA STRUCTURES (and related) ###
* **KDTree:** models a generic KDTree of nodes of any dimension. Can search
  for nearest node from a specific location.
* **Graph:** models a generic Graph of nodes and edges using a cyclical generic pattern. Uses a modified dijkstra's
  algorithm, known as A*, to build and find the shortest path between two nodes.
*  **Database:** instantiated with and queries from a sqlite3 database.
  * **Caching:** we used the guava cache in order to optimize our program. The cache models
    a hashmap mapping between node.ids and a list of outgoing edges, such that when a request is made
    to the cache it either returns stored data or queries, stores, and returns new data.

### INTERFACES (and their concrete implementations) ###
* **Node**: models a generic Node with cyclical generic parameterization.
  * **GraphNode:** implements Node, used in the CommandHandler, can query for its outgoing
    GraphEdges and calculate haversine distances from itself to another node. Can also be used as a KDTreeNode
    due to its previously defined HasCoordinates attributes.
* **Edge**: models a generic Edge with cyclical generic parameterization.
  * **GraphEdge:** implements Edge, used in Routes class in CommandHandler.
* **Commandable:** represents a command, has an execute method.
  * **Map:** establishes a connection with and queries data from the database to make nodes
    and construct a KDTree.
  * **Ways:** queries for all the edges that are within a rectangular parameter between two coordinates (northwest, southeast).
  * **Nearest:** asks the KDTree to find the nearest node in the database to a specific location.
  * **Route:** finds the nodes associated with src and target in the KDTree and runs A* on the two
    nodes, which returns the shortest route.
    * *given 2 sets of coordinates*, it runs nearest on each one to find the nodes to
      run A* on.
    * *given 2 sets of street names*, the database is queried to find the nodes at the
      intersection of the street names.


## Handin Questions ##

---

### Division of Labor ###

* **equally shared**
  * overall design (had similar stars implementation for CommandHandler)
  * junit/system tests
  * general debugging
* **nadav (ndruker)**
  * generic KDTree from Stars
  * SQL and querying information from database
  * wrote generic graph related classes
  * drafted dijkstra's/A* algorithm
* **eva (elau5)**
  * extensible REPL from Stars
  * wrote classes for repl commands in CommandHandler using query results
  * debugged dijkstra's/A* algorithm (and other commands)
  * checkstyle, documentation, readme

### Testing and Known Bugs ###

* **System Tests:**
  * incorrect commands, invalid arguments, didn't load data first
  * (provided by TAs) 5 commands with small maps (run with *./cs32-test tests/ta/maps/**.test -t 60*)
  * (we wrote) 5 commands with big maps

* **Junit Tests:** (run with *mvn package*)
  * We tested rigorously using random output and discovered some correctness issues when testing on the big dataset.
    In particular, we've noticed specific instances where dijkstra path did not match aStar path. We managed to isolate a
    few of these cases, debug them ourselves and with the help of tas, but we couldn't pinpoint the exact issue.
    We suspect that it has something to do with our implementation of KDTree.neighbors() from stars. We've included one of
    these cases as a systems test titled route_coords_big_db_isolated_case.

* **Bugs**
  * The route is only displaying route after a second pair of coordinates is selected for clicking.
  * The checkin information doesn't render properly. 
  * we had many issues with useState and useEffect.  
  * We set up basically the entire framework for displaying the user data and the checkins, and were able to pass the information to the frontend - however, after many TA hour sessions and speaking with Tim about it, we were not able to resolve the issue with our state variable causing an “react prevents re-rendering too often issue”. Because of this nothing shows up in the checkin box or user table, but the information is being sent, and we would be happy to talk through our thought process for the implementation/what we think is going wrong/what we did get working.
### Building and Running (from command line) ###

* **to build:** *mvn package*
* **to run repl:** *./run*

