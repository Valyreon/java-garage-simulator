# Java Garage Simulator

### Purpose
This project was made as a part of a course in Java at my University. It demonstrates my knowledge in Threads, UI designing and general Java and OOP programming.

### Requirements description
For this project it was neccessary to make a Garage simulator. Application should consist of two parts: User part and Admin part. Administrator part should be a GUI application where an admin can add and park arbitrary number of vehicles in the garage, based on which the current state of the garage is generated. The garage can have **n** number of platforms where every platform has the following shape:

![Image of garage platform matrix](https://i.ibb.co/yVyvdTn/garage.png "Garage Matrix")

Grey squares represent parking spaces and white squares represent the path. On the left side vehicles can enter the platform from the platform below or go from this platform to the platform below. On the right side vehicles can enter the current platform from the platform above, or leave the current platform and go to the platform above. 

Each vehicle should respect the following rules: vehicles can only drive on the right side on the road. Garage should keep track of the number of free spaces. If all platforms are filled, garage shouldn't let more vehicles in the garage. If the platform is filled, a vehicle should just go straight (left) to the next platform, and if it's not then it should go around the platform looking for an empty space. If it doesn't find one, it should continue to the next platform.

Every vehicle should have a name, chasis number, engine number, photo and plate number. Types of vehicles are motocycle, car and van. Car should have a member which represents number of doors and vans should have carry capacity. Garage is also used by public service vehicles, so car, van and motocycle can be police cars, van and car can be medical vehicles and van can also be a firefighter vehicle. Each of these vehicles can have it's emergency light rotation on. If the emergency is on, these cars need to be able to overpass regular cars and cars without rotation enabled. Police cars also have a file which representes a list of plate numbers of wanted cars.

#### Admin part

Admin part form should minimally contain dropdown menu with each type of vehicle, dropdown for selecting current platform, button for adding new vehicles, table with all cars already added to a certain platform and a button for starting User part of the application. There should also be options for editing and deleting vehicles in the garage. There should be a separate form for adding new vehicles to the garage that is generated based on the vehicle type selected in the dropdown menu. Number of platforms in the garage should be read from some outside settings file. When exiting admin part, application needs to serialize te garage in a file *garage.ser*, which is deserialized every time the application is started and used for filling the tables.

#### User part

User part of the application is a GUI application which represents a simulation of vehicle movements in the garage. When user application is started, user sets a minimal number of vehicles set in each platform of the garage. Form for user part of application should have a text area where the current state of the garage will be generated, button for simulation start, and a button for making a new randomly generated vehicle enter the garage. After user starts the simulation, application uses vehicles saved in the file *garage.ser* to fill the platforms. If some platform in the saved file has less vehicles than the minimum the user inputed earlier, application should generate random vehicles to make up the difference. Type of vehicle, as well as if it's a civilian or public department vehicle is randomly generated with 90% chance of a civilian vehicle.

#### Simulation

After all vehicles are set up, and simulation is starting, a matrix is generated in the designated text area which represents the state of the currently selected platform in the dropdown box. Empty parking spaces are indicated with a **\***, **V** is used for vehicles, **H** is used for medical vehicles, **P** for police vehicles and **F** for firefighter vehicles. If emergency rotation is on for a vehicle then **R** is added to the vehicles symbol. At the beginning of simulation 15% of vehicles parked on each platform should start leaving the garage. User can use the button for adding new random vehicles to make a new randomly generated vehicle enter the garage and start looking for a parking spot. During the moving of cars through the garage, there should be 10% chance of a crash happening. In that case a police, firefighter and medical vehicle should enter the garage and move to the scene of the accident. Traffic is blocked on that platform until an investigation is performed. Investigation lasts 3-10 seconds. A police car moving through the garage should check for wanted vehicles. If it finds one, it writes a report (this lasts 3 to 5 seconds), and then the wanted vehicle follows the police vehicle out of the garage. In every report, there should be vehicle names, time, and photos, and they should be saved in binary file.

Garage should also keep records of times when the vehicles entered and left the garage and bill them for the time spent parked. Police, medical and firefighter vehicles are not billed. Bills should be written to a text file somewhere in the system.

Simulation ends when all vehicles leaving the garage have left it and all the added cars are parked. Garage state is then again serialized in *garage.ser* file.

### Implementation details

Every vehicle in the simulation is a separate thread with algorithms for movement through the matrices representing the platforms of the garage. Platforms are implemented as 8x10 *Vehicle* class matrices, using polymorphism. Every type of vehicle has to implement the abstract *Vehicle* class. When a vehicle (thread) needs to move, it looks at the spot in the matrix it needs to go to, and if there is a car there, then it *waits* for it and there is a chance for collision. Each time a vehicle successfully moves it invokes *notifyAll()* method.

Moving of public department vehicles when the emergeny rotation is on is implemented on separate matrices, one additional matrix for every platform. When drawing the matrix in the designated text area, application first checks if there are vehicles in specified spot in the emergency matrix, and if there are, it draws their symbol, so i looks like that cars are overpassing other cars.

### Tools

For the development of this project, IntelliJ Community was as IDE. Java 8 was used for compiling and running the application. GUI was designed using JavaFX.

### Screenshots

![Image of admin part of the app](https://raw.githubusercontent.com/Valyreon/java-garage-simulator/master/adminpart.PNG?token=AEJXIZKCGM33POERVVZPLG25DXCAY)
![Image of add vehicle form](https://raw.githubusercontent.com/Valyreon/java-garage-simulator/master/addcar.PNG?token=AEJXIZNSNJ7OGH4MWK5RAHC5DXCAS)
![Image of user part of the app](https://raw.githubusercontent.com/Valyreon/java-garage-simulator/master/clientpart.PNG?token=AEJXIZMKOTNBUCRC4MT7HPC5DXCA6)
