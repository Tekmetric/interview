# About the project
This project shows an example of CRUD operations for a tennis player entity.

##### Technical explanation #####
We have 2 routes here: 1 is for displaying the list of player, with some basic action, delete/edit each row from the list
and a create new player button.

In Order to interact with our api, we are using axios and redux to cache our data and improve performance.
The flow is like this:
The app has a global store, which contains 3 arrays, players (which is the main data), tournaments and racquets.
When you open the players list page, React will call the api and load the list of players and also dispatch an action
for updating the current state(the list of players) through resolvers since the store is immutable.
When a new user is added or updated, after calling the create/update api,the app will emit an action CREATE/UPDATE
to update the store and if we have any changes, the UI will be updated, in our case, after saving a new player, the app
will navigate to players list and show the new data without calling again the api (the players list from the store was
updated with the player object from the create/update api response).

To manipulate the create/edit form, the app is using ```form hook``` (useForm())).

# Technologies used
* React
* React Redux
* TailwindCSS
* Axios

# Get started
* backend should be started. Follow instruction from backend README
* ```npm install```
* ```npm start```
* open the browser at ```http://localhost:3000```

# Use case
The main page will be displayed where players list will show and we can select to edit a player by clicking on
```pencil``` icon or delete one by clicking on ```delete``` icon.
By pressing delete, the app will ask you for a ```confirmation before``` the delete api will be called.

By pressing ```pencil``` icon, the app will navigate to a new route ```/details``` and there you can see the form with
the selected player data and after you update some of the field by pressing ```Save changes```
button the player record will be updated.

Clicking on the plus button will open the same route ```/details``` but the form will be empty for you to create a new
player.

### Who do I talk to? ###

* alinbizau93@gmail.com
