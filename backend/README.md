# README #

This application represents an API for a tennis player entity.

## Technologies used ##

* Spring Boot
* Java 17
* Lombok
* MapStruct
* Spring Data
* H2 Database

##### Technical explanation #####

We have ```Player``` model which contains different mandatory and optional fields, and also a list of previous results,
which is mapped as a ```@OneToMany``` relationship since a result can be associated only with a specific player but a
player
can have multiple results. Then we have 2 types of ```@ManyToMany``` relationships: ```Tournament``` and ```Racquets```
since
a tournament can be added to multiple players and also a player can participate to multiple tournaments (same for
racquets).

Then we have a JpaRepository for every model that we are using in this application. =
The next layer used is the service one, where repository and mapper is injected and we provide the method to be used
in controller.
In controller, we define the rest api for all the necessary operations (GET, POST, DELETE).

The application provides some integration test that covers different flows.

### What's the application about? ###

```
Player 
Build a REST API to show the CRUD operation for tennis players.
The Player has mandatory fields:
• Name
• Rank
• birthdate
• birthplace
• turnedPro
• weight
• height
It can have also optional fields:
• coach
• stats
• previousResults
• tournaments
• racquets
Each player can participate to different tournaments, have different racquets and have multiple previous results:

```

### Constraints ###

* For the Player, Tournament, Surface, TournamentType, name field is unique
* For the Racquet, brand and model fields are mandatory.

### How do I get set up? ###

* To install the dependencies open the project and run ```mvn clean install```
  or use reload maven project option from IntelliJ
* Run the ```DemoApplication.java``` class as a Java application.
* If you don't open the project, open a console after you set java path in your system.
  Use ```javac DemoApplication.java``` to compile the code and then ```java DemoApplication``` to run the app.

### Example to TEST the application ###

* Load all players in the system
  ```curl -X GET http://localhost:8085/api/players```
* Load all players in the system based on specific query
  ```curl -X GET "http://localhost:8085/api/players?query=name:Alin"```
* Load player by id
  ```curl -X GET http://localhost:8085/api/players/1```
* Load all tournaments in the system (used in FE)
  ```curl -X GET http://localhost:8085/api/tournaments```
* Load all racquets in the system (used in FE)
  ```curl -X GET http://localhost:8085/api/racquets```
* Delete player by id
  ```curl -X DELETE http://localhost:8085/api/players/2```
* Check if the player was deleted
  ```curl -X GET http://localhost:8085/api/players/2```

* Add a new player

```
curl -d '{
    "name": "Tekmetric Player",
    "rank": 1,
    "birthdate": "30-08-2023",
    "birthplace": "Romania",
    "turnedPro": "30-08-2023",
    "weight": 75.00,
    "height": 182.00,
    "coach": "Tekmetric Coach",
    "stats": {
        "aces": 6,
        "doubleFaults": 2,
        "wins": 3,
        "losses": 1,
        "tournamentsPlayed": 2
    },
    "previousResults": [
        {
            "points": 6,
            "opponentName": "Tekmetric Opponent",
            "opponentPoints": 4
        }
    ],
    "tournaments": [
        {
            "id": 1
        },
         {
            "id": 2
        }
    ], 
    "racquets": [
       {
           "id": 1
       }
       
    ]
}
' -H "Content-Type: application/json" -X POST http://localhost:8085/api/players
```

* Update a player record

```
curl -d '
    {
        "id": 1,
        "name": "Alin Bizau",
        "rank": 1,
        "birthdate": "22-06-1990",
        "birthplace": "Romania",
        "turnedPro": "11-06-2009",
        "weight": 72.0,
        "height": 179.0,
        "coach": "Roger Coach",
        "stats": {
            "id": 1,
            "aces": 4,
            "doubleFaults": 2,
            "wins": 4,
            "losses": 1,
            "tournamentsPlayed": 2
        },
        "previousResults": [
            {
                "id": 1,
                "points": 1,
                "opponentName": "Carlos Alcaraz",
                "opponentPoints": 6
            }
        ],
        "tournaments": [
            {
                "id": 1,
                "name": "US Open",
                "city": "New York",
                "country": "USA",
                "prizeMoney": 1500000.0,
                "date": "09-10-2023",
                "surface": {
                    "id": 1,
                    "name": "HARD"
                }
            },
            {
                "id": 2,
                "name": "Wimbledon",
                "city": "London",
                "country": "UK",
                "prizeMoney": 1500000.0,
                "date": "07-10-2023",
                "surface": {
                    "id": 3,
                    "name": "GRASS"
                }
            }
        ],
        "racquets": [
            {
                "id": 1,
                "brand": "Babolat",
                "model": "Pure Aero",
                "weight": 300,
                "headSize": 100
            },
            {
                "id": 2,
                "brand": "Head",
                "model": "Speed",
                "weight": 310,
                "headSize": 98
            }
        ]
    }
' -H "Content-Type: application/json" -X POST http://localhost:8085/api/players
```

### Who do I talk to? ###

* alinbizau93@gmail.com

#### Import project into IDE

- Project root is located in `backend` folder

#### Build and run your app

- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

#### Test that your app is running

- `curl -X GET   http://localhost:8080/api/welcome`

#### After finishing the goals listed below create a PR

### Goals

1. Design basic CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single
   item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

#### H2 Configuration

- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
