# Overview
This project is a microservices back-end for a music application like Spotify. 
The specific domain of this project is to manage the artist, album, and song database.

# Data model

## Artist
An Artist has a name and only a name.

## Song
A song MAY be associated to one or more Albums but is always associated to an Artist.
A song has a title, length (a Duration object), and a release date.

## Album
An album is a named collection of songs.  A song may be present on more than one album or it may be
a single release, in which case it is associated directly with the artist.

An album has a title and a release date.

# Requirements

The microservice must support the following operations on each domain object
    - GET
    - PUT (update)
    - POST (create)
    - DELETE

Each write operation must notify connected clients via Websockets.

Each write operation must post an internal message on a JMS broker to notify other
components of the broader system of the change.

Deleting an Artist cascade deletes all of the Songs and Albums associated with that artist.

There must be a search API that substring matches a text input string to the artist name, song title, and album title fields and returns the objets that matched.

There must be a list API to list all of the albums for an artist.

There must be a list API to list all of the songs for an artist.

There must be a list API to list all of the artists.

There must be a list API to list all of the songs on an album.

The list APIs must support paging.

# Architecture

- The project must use H2 as a database
- The project must use Spring Boot
- The project must use Spring's Repository pattern and JPA
- The system should start its own lightweight internal message broker
- The system must have a service tier rather than using repositories directly from REST controllers.
- The system must have separate DTO objects used in the API and translate those objects to JPA entities using ModelMapper.