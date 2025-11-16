# Get All Leagues
curl -X GET http://localhost:8080/api/leagues
# Get Single League
curl -X GET http://localhost:8080/api/leagues/1
# Get Single League By Name
curl -X GET http://localhost:8080/api/leagues/byName/League1
# Create League
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League1", "location": "Richmond, VA", "skillLevel":"Competitive", "teams": [{"name":"Team1", "players": "Kevin Hall, Joe Hall"},{"name":"Team2", "players": "James Hall, Sue Hall"}]}' http://localhost:8080/api/leagues
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League2", "location": "Henrico, VA", "skillLevel":"Competitive", "teams": [{"name":"Team3", "players": "Chris Hall, Cara Hall"},{"name":"Team4", "players": "Joan Hall, John Hall"}]}' http://localhost:8080/api/leagues
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name": "League3", "location": "Henrico, VA", "skillLevel":"Intermediate", "teams": [{"name":"Team5", "players": "Chris Hall, Kevin Hall"},{"name":"Team6", "players": "Mary Hall, Jake Hall"}]}' http://localhost:8080/api/leagues
# Update League
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name": "League3B", "location": "Henrico, VA", "skillLevel":"Beginner", "teams": []}' http://localhost:8080/api/leagues/7
# Partial Update League
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"name": "League2Patch"}' http://localhost:8080/api/leagues/4
# Delete League
curl -u user:testPassword -X DELETE http://localhost:8080/api/leagues/1

# Get All Teams
curl -X GET http://localhost:8080/api/teams
# Get Single Team
curl -X GET http://localhost:8080/api/teams/2
# Get Single Team By Name
curl -X GET http://localhost:8080/api/teams/byName/Team1
# Create Team
# Create Team without League
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name":"Team7", "players": "Spencer Hall, Robert Hall"}' http://localhost:8080/api/teams
# Create Team with League
curl -u user:testPassword -X POST -H "Content-Type: application/json" -d '{"name":"Team8", "players": "Celeste Hall, Allen Hall", "league":4}' http://localhost:8080/api/teams
# Update Team
curl -u user:testPassword -X PUT -H "Content-Type: application/json" -d '{"name":"Team7B", "players": "Spencer Hall, Robert Hall, Wes Hall", "league":4}' http://localhost:8080/api/teams/10
# Partial Update Team
curl -u user:testPassword -X PATCH -H "Content-Type: application/json" -d '{"name":"Team8Patch"}' http://localhost:8080/api/teams/11
# Delete Team
curl -u user:testPassword -x DELETE http://localhost:8080/api/teams/3




