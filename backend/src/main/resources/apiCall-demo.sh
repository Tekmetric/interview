# Get All Leagues - Empty
curl -X GET http://localhost:8080/api/leagues
printf "\n"
# Get Single League - Empty
curl -X GET http://localhost:8080/api/leagues/1
printf "\n"
# Create Leagues with Team
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League1", "location": "Richmond, VA", "skillLevel":"Competitive", "teams": [{"name":"Team1", "players": "Kevin Hall, Joe Hall"},{"name":"Team2", "players": "James Hall, Sue Hall"}]}' http://localhost:8080/api/leagues
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League2", "location": "Henrico, VA", "skillLevel":"Competitive", "teams": [{"name":"Team3", "players": "Chris Hall, Cara Hall"},{"name":"Team4", "players": "Joan Hall, John Hall"}]}' http://localhost:8080/api/leagues
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League3", "location": "Henrico, VA", "skillLevel":"Intermediate", "teams": [{"name":"Team5", "players": "Chris Hall, Kevin Hall"},{"name":"Team6", "players": "Mary Hall, Jake Hall"}]}' http://localhost:8080/api/leagues

# Create League Error - same row exists
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League3", "location": "Henrico, VA", "skillLevel":"Intermediate", "teams": [{"name":"Team5", "players": "Chris Hall, Kevin Hall"},{"name":"Team6", "players": "Mary Hall, Jake Hall"}]}' http://localhost:8080/api/leagues
# Create League Error with Teams with ids
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League4", "location": "Henrico, VA", "skillLevel":"Beginner", "teams": [{"id":1, "name":"Team5", "players": "Chris Hall, Kevin Hall"},{"id":2, "name":"Team6", "players": "Mary Hall, Jake Hall"}]}' http://localhost:8080/api/leagues

# Get All Leagues
printf "\n"
curl -X GET http://localhost:8080/api/leagues
# Get Single League
printf "\n"
curl -X GET http://localhost:8080/api/leagues/1
# Get Single League By Name
printf "\n"
curl -X GET http://localhost:8080/api/leagues/byName/League1
# Update League
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name": "League3B", "location": "Henrico, VA", "skillLevel":"Beginner", "teams": []}' http://localhost:8080/api/leagues/3
# Update League With Team
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name": "League3BAgain", "location": "Henrico, VA", "skillLevel":"Beginner", "teams": [{"id":1, "name":"Team5", "players": "Chris Hall, Kevin Hall"}]}' http://localhost:8080/api/leagues/3
# Update League with Team Error - Team not found
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name": "League3B", "location": "Henrico, VA", "skillLevel":"Beginner", "teams": [{"id":20, "name":"Team5", "players": "Chris Hall, Kevin Hall"}]}' http://localhost:8080/api/leagues/1
# Update League Error - League not found
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name": "League3B", "location": "Henrico, VA", "skillLevel":"Beginner", "teams": [{"id":20, "name":"Team5", "players": "Chris Hall, Kevin Hall"}]}' http://localhost:8080/api/leagues/11

# Get Single League
printf "\n"
curl -X GET http://localhost:8080/api/leagues/3

# Partial Update League
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"name": "League2Patch"}' http://localhost:8080/api/leagues/2
# Partial Update League With Team Error - Team not found
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"teams": [{"id":20, "name":"Team5", "players": "Chris Hall, Kevin Hall"}]}' http://localhost:8080/api/leagues/1
# Partial Update League With Team Error - Team id cannot be missing
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"teams": [{"name":"Team5", "players": "Chris Hall, Kevin Hall"}]}' http://localhost:8080/api/leagues/1
# Partial Update League with Team
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"teams": [{"id":2, "name":"Team5", "players": "Chris Hall, Kevin Hall"}]}' http://localhost:8080/api/leagues/1
#Partial Update League Error - League not found
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"name": "League2Patch"}' http://localhost:8080/api/leagues/22

# Delete League
printf "\n"
curl -u user:testPassword -X DELETE http://localhost:8080/api/leagues/1
# Delete League Error - League not found
printf "\n"
curl -u user:testPassword -X DELETE http://localhost:8080/api/leagues/11

# Get All Leagues
printf "\n"
curl -X GET http://localhost:8080/api/leagues

# Get All Teams
printf "\n"
printf "\n"
curl -X GET http://localhost:8080/api/teams
# Get Single Team
printf "\n"
curl -X GET http://localhost:8080/api/teams/2

# Create Team without League
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name":"Team7", "players": "Spencer Hall, Robert Hall"}' http://localhost:8080/api/teams
# Create Team Error - Same row exists
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name":"Team7", "players": "Spencer Hall, Robert Hall"}' http://localhost:8080/api/teams
# Create Team with League
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name":"Team8", "players": "Celeste Hall, Allen Hall", "league":2}' http://localhost:8080/api/teams
# Create Team with League Error - League not found
printf "\n"
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name":"Team8", "players": "Celeste Hall, Allen Hall", "league":20}' http://localhost:8080/api/teams

# Get All Teams
printf "\n"
curl -X GET http://localhost:8080/api/teams
# Get Single Team By Name
printf "\n"
curl -X GET http://localhost:8080/api/teams/byName/Team1

# Update Team
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name":"Team7B", "players": "Spencer Hall, Robert Hall, Wes Hall", "league":4}' http://localhost:8080/api/teams/1
# Update Team Error - Team not found
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name":"Team7BFail", "players": "Spencer Hall, Robert Hall, Wes Hall", "league":4}' http://localhost:8080/api/teams/20
# Update Team Error - League not found
printf "\n"
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name":"Team7BFail", "players": "Spencer Hall, Robert Hall, Wes Hall", "league":20}' http://localhost:8080/api/teams/1

# Get Single Team By Name
printf "\n"
curl -X GET http://localhost:8080/api/teams/byName/Team7B

# Partial Update
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"name":"Team8Patch"}' http://localhost:8080/api/teams/6
# Partial Update Team Error - Team not found
printf "\n"
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"name":"Team8Patch"}' http://localhost:8080/api/teams/22

# Delete Team
printf "\n"
curl -u user:testPassword -X DELETE http://localhost:8080/api/teams/3
# Delete Team Error - Team not found
printf "\n"
curl -u user:testPassword -X DELETE http://localhost:8080/api/teams/25

# Get All Teams
printf "\n"
curl -X GET http://localhost:8080/api/teams
printf "\n"

