# README #
This application represents an API for a tennis player entity.

## Technologies used ##

* Spring Boot
* Java 17
* Lombok
* MapStruct
* Spring Data
* H2 Database
* Spring Boot Actuator

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
In controller, we define the rest api endpoints for all the necessary operations (GET, POST, DELETE).

The application provides some integration tests that cover different flows.
Also, we can check the status on the application and DB using the actuator url (````http://localhost:8085/actuator/health/````)

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

* For the Player, Tournament, Surface, TournamentType, the name field is unique
* For the Racquet, brand and model, the fields are mandatory.

### Example to TEST the application ###
To test if the app is up and healthy: ```http://localhost:8085/actuator/health```.

To test the functionalities, the following options can be used:
1. Using the frontend that I've created
2. Using the integration tests provided in ```PlayerApplicationTest``` 
3. Using this commands below:

   * Load all players in the system
     ```curl -X GET http://localhost:8085/api/players```
   * Load one/all players in the system based on specific query
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
            },
             {  
                "points": 3,
                "opponentName": "Rafael Nadal",
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

#### Import project into IDE

- Project root is located in `backend` folder

#### Build and run your app
- To install the dependencies open the project and run ```mvn clean install```
  or use reload maven project option from IntelliJ
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

### Who do I talk to? ###
* alinbizau93@gmail.com
