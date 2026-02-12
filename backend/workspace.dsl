workspace "Event Management System" "Architecture for the Event Management REST API with Spring Boot" {

    model {
        # Define people/actors
        adminUser = person "Admin User" "Administrator who manages events, venues, and performers"
        regularUser = person "Regular User" "End user who views and searches events"

        # Define the main software system
        eventManagementSystem = softwareSystem "Event Management System" "Manages events, venues, performers, and user accounts with search and caching capabilities" {

            # Define containers within the system
            springBootApp = container "Spring Boot Application" "Provides REST API for event management with JWT authentication" "Java 21, Spring Boot 3.2.0" {

                # Controllers
                authController = component "AuthController" "Handles user authentication and JWT token generation" "Spring MVC @RestController"
                eventController = component "EventController" "Provides REST endpoints for event CRUD operations" "Spring MVC @RestController"
                accountController = component "AccountController" "Manages user accounts and profiles" "Spring MVC @RestController"

                # Security
                securityFilter = component "JwtAuthenticationFilter" "Validates JWT tokens and sets security context" "Spring Security Filter"

                # Services
                eventService = component "EventService" "Business logic for event management and caching" "Spring @Service"
                accountService = component "AccountService" "Business logic for account management and authentication" "Spring @Service"
                eventSearchService = component "EventSearchService" "Handles OpenSearch integration for event search" "Spring @Service"

                # Repositories
                eventRepository = component "EventRepository" "JPA repository for Event entity persistence" "Spring Data JPA Repository"
                accountRepository = component "AccountRepository" "JPA repository for Account entity persistence" "Spring Data JPA Repository"
                venueRepository = component "VenueRepository" "JPA repository for Venue entity persistence" "Spring Data JPA Repository"
                performerRepository = component "PerformerRepository" "JPA repository for Performer entity persistence" "Spring Data JPA Repository"
            }

            h2Database = container "H2 Database" "Stores events, venues, performers, accounts, and roles" "H2 In-Memory Database"
            redisCache = container "Redis Cache" "Distributed cache for frequently accessed data" "Redis"
            openSearchEngine = container "OpenSearch" "Full-text search engine for events" "OpenSearch 2.x"
            swaggerUI = container "Swagger UI" "Interactive API documentation and testing interface" "OpenAPI 3.0"
        }

        # User to System relationships
        adminUser -> eventManagementSystem "Manages events, venues, and performers"
        regularUser -> eventManagementSystem "Views and searches events"

        # User to Container relationships
        adminUser -> springBootApp "Creates, updates, deletes events via" "HTTPS/JSON"
        regularUser -> springBootApp "Views and searches events via" "HTTPS/JSON"
        adminUser -> swaggerUI "Tests API endpoints via" "HTTPS"
        regularUser -> swaggerUI "Explores API documentation via" "HTTPS"

        # Container to Container relationships
        swaggerUI -> springBootApp "Makes API calls to" "HTTPS/JSON"
        springBootApp -> h2Database "Reads from and writes to" "JPA/JDBC"
        springBootApp -> redisCache "Caches data in" "Redis Protocol"
        springBootApp -> openSearchEngine "Performs full-text search via" "REST API"

        # User to Component relationships
        adminUser -> eventController "Manages events via" "HTTPS/JSON"
        regularUser -> eventController "Views events via" "HTTPS/JSON"
        adminUser -> accountController "Manages accounts via" "HTTPS/JSON"
        adminUser -> authController "Authenticates via" "HTTPS/JSON"
        regularUser -> authController "Authenticates via" "HTTPS/JSON"

        # Component to Component relationships (within Spring Boot App)
        eventController -> securityFilter "Protected by"
        accountController -> securityFilter "Protected by"

        eventController -> eventService "Uses"
        accountController -> accountService "Uses"
        authController -> accountService "Uses"

        eventService -> eventRepository "Reads and writes events using"
        eventService -> venueRepository "Reads venues using"
        eventService -> performerRepository "Reads performers using"
        eventService -> eventSearchService "Searches events using"

        accountService -> accountRepository "Reads and writes accounts using"

        # Component to Container relationships
        eventRepository -> h2Database "Reads and writes to"
        accountRepository -> h2Database "Reads and writes to"
        venueRepository -> h2Database "Reads and writes to"
        performerRepository -> h2Database "Reads and writes to"

        eventService -> redisCache "Caches data in"
        eventSearchService -> openSearchEngine "Performs full-text search using"
    }

    views {
        # System Context diagram
        systemContext eventManagementSystem "SystemContext" {
            include *
            autoLayout
        }

        # Container diagram
        container eventManagementSystem "Containers" {
            include *
            autoLayout
        }

        # Component diagram for Spring Boot Application
        component springBootApp "Components" {
            include *
            autoLayout
        }

        # Styling
        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
        }

        # Theme
        theme default
    }

    configuration {
        scope softwaresystem
    }

}
