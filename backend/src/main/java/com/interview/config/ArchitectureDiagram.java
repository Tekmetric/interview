package com.interview.config;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.export.mermaid.MermaidDiagramExporter;
import com.structurizr.model.*;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Structurizr Architecture Diagram Generator
 *
 * This class defines the Event Management System architecture using the C4 model:
 * - System Context: Shows the system and its users
 * - Container Diagram: Shows the high-level technology choices
 * - Component Diagram: Shows the internal structure of the Spring Boot application
 *
 * Run this class directly to generate architecture diagrams in multiple formats.
 */
public class ArchitectureDiagram {

    public static void main(String[] args) throws Exception {
        Workspace workspace = new Workspace("Event Management System",
            "Architecture for the Event Management REST API with Spring Boot");
        Model model = workspace.getModel();
        ViewSet views = workspace.getViews();

        // Define people/actors
        Person adminUser = model.addPerson("Admin User",
            "Administrator who manages events, venues, and performers");
        Person regularUser = model.addPerson("Regular User",
            "End user who views and searches events");

        // Define the main system
        SoftwareSystem eventManagementSystem = model.addSoftwareSystem("Event Management System",
            "Manages events, venues, performers, and user accounts with search and caching capabilities");

        adminUser.uses(eventManagementSystem, "Manages events, venues, and performers");
        regularUser.uses(eventManagementSystem, "Views and searches events");

        // Define containers (high-level components)
        Container springBootApp = eventManagementSystem.addContainer("Spring Boot Application",
            "Provides REST API for event management with JWT authentication",
            "Java 21, Spring Boot 3.2.0");

        Container h2Database = eventManagementSystem.addContainer("H2 Database",
            "Stores events, venues, performers, accounts, and roles",
            "H2 In-Memory Database");

        Container redisCache = eventManagementSystem.addContainer("Redis Cache",
            "Distributed cache for frequently accessed data",
            "Redis");

        Container openSearchEngine = eventManagementSystem.addContainer("OpenSearch",
            "Full-text search engine for events",
            "OpenSearch 2.x");

        Container swaggerUI = eventManagementSystem.addContainer("Swagger UI",
            "Interactive API documentation and testing interface",
            "OpenAPI 3.0");

        // Define relationships between containers
        adminUser.uses(springBootApp, "Creates, updates, deletes events via", "HTTPS/JSON");
        regularUser.uses(springBootApp, "Views and searches events via", "HTTPS/JSON");
        adminUser.uses(swaggerUI, "Tests API endpoints via", "HTTPS");
        regularUser.uses(swaggerUI, "Explores API documentation via", "HTTPS");

        swaggerUI.uses(springBootApp, "Makes API calls to", "HTTPS/JSON");
        springBootApp.uses(h2Database, "Reads from and writes to", "JPA/JDBC");
        springBootApp.uses(redisCache, "Caches data in", "Redis Protocol");
        springBootApp.uses(openSearchEngine, "Performs full-text search via", "REST API");

        // Define components within Spring Boot Application
        Component authController = springBootApp.addComponent("AuthController",
            "Handles user authentication and JWT token generation",
            "Spring MVC @RestController");

        Component eventController = springBootApp.addComponent("EventController",
            "Provides REST endpoints for event CRUD operations",
            "Spring MVC @RestController");

        Component accountController = springBootApp.addComponent("AccountController",
            "Manages user accounts and profiles",
            "Spring MVC @RestController");

        Component securityFilter = springBootApp.addComponent("JwtAuthenticationFilter",
            "Validates JWT tokens and sets security context",
            "Spring Security Filter");

        Component eventService = springBootApp.addComponent("EventService",
            "Business logic for event management and caching",
            "Spring @Service");

        Component accountService = springBootApp.addComponent("AccountService",
            "Business logic for account management and authentication",
            "Spring @Service");

        Component eventSearchService = springBootApp.addComponent("EventSearchService",
            "Handles OpenSearch integration for event search",
            "Spring @Service");

        Component eventRepository = springBootApp.addComponent("EventRepository",
            "JPA repository for Event entity persistence",
            "Spring Data JPA Repository");

        Component accountRepository = springBootApp.addComponent("AccountRepository",
            "JPA repository for Account entity persistence",
            "Spring Data JPA Repository");

        Component venueRepository = springBootApp.addComponent("VenueRepository",
            "JPA repository for Venue entity persistence",
            "Spring Data JPA Repository");

        Component performerRepository = springBootApp.addComponent("PerformerRepository",
            "JPA repository for Performer entity persistence",
            "Spring Data JPA Repository");

        // Define relationships between components
        adminUser.uses(eventController, "Manages events via", "HTTPS/JSON");
        regularUser.uses(eventController, "Views events via", "HTTPS/JSON");
        adminUser.uses(accountController, "Manages accounts via", "HTTPS/JSON");
        adminUser.uses(authController, "Authenticates via", "HTTPS/JSON");
        regularUser.uses(authController, "Authenticates via", "HTTPS/JSON");

        eventController.uses(securityFilter, "Protected by");
        accountController.uses(securityFilter, "Protected by");

        eventController.uses(eventService, "Uses");
        accountController.uses(accountService, "Uses");
        authController.uses(accountService, "Uses");

        eventService.uses(eventRepository, "Reads and writes events using");
        eventService.uses(venueRepository, "Reads venues using");
        eventService.uses(performerRepository, "Reads performers using");
        eventService.uses(eventSearchService, "Searches events using");
        eventService.uses(redisCache, "Caches data in");

        accountService.uses(accountRepository, "Reads and writes accounts using");

        eventRepository.uses(h2Database, "Reads and writes to");
        accountRepository.uses(h2Database, "Reads and writes to");
        venueRepository.uses(h2Database, "Reads and writes to");
        performerRepository.uses(h2Database, "Reads and writes to");

        eventSearchService.uses(openSearchEngine, "Performs full-text search using");

        // Create views

        // 1. System Context Diagram
        SystemContextView contextView = views.createSystemContextView(eventManagementSystem,
            "SystemContext", "System Context diagram for Event Management System");
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();
        contextView.setPaperSize(PaperSize.A5_Landscape);

        // 2. Container Diagram
        ContainerView containerView = views.createContainerView(eventManagementSystem,
            "Containers", "Container diagram for Event Management System");
        containerView.addAllContainers();
        containerView.addAllPeople();
        containerView.setPaperSize(PaperSize.A4_Landscape);

        // 3. Component Diagram
        ComponentView componentView = views.createComponentView(springBootApp,
            "Components", "Component diagram for Spring Boot Application");
        componentView.addAllComponents();
        componentView.addAllPeople();
        componentView.setPaperSize(PaperSize.A3_Landscape);

        // Apply styling
        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
        styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
        styles.addElementStyle(Tags.CONTAINER).background("#438dd5").color("#ffffff");
        styles.addElementStyle(Tags.COMPONENT).background("#85bbf0").color("#000000");
        styles.addRelationshipStyle(Tags.RELATIONSHIP).routing(Routing.Orthogonal);

        // Export diagrams to various formats
        exportDiagrams(workspace);

        System.out.println("Architecture diagrams generated successfully!");
        System.out.println("Check the 'diagrams' directory for the generated files.");
    }

    private static void exportDiagrams(Workspace workspace) throws Exception {
        // Create diagrams directory
        File diagramsDir = new File("diagrams");
        if (!diagramsDir.exists()) {
            diagramsDir.mkdirs();
        }

        // Export to PlantUML
        C4PlantUMLExporter plantUMLExporter = new C4PlantUMLExporter();
        workspace.getViews().getSystemContextViews().forEach(view -> {
            try {
                Diagram diagram = plantUMLExporter.export(view);
                writeToFile("diagrams/system-context.puml", diagram.getDefinition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workspace.getViews().getContainerViews().forEach(view -> {
            try {
                Diagram diagram = plantUMLExporter.export(view);
                writeToFile("diagrams/containers.puml", diagram.getDefinition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workspace.getViews().getComponentViews().forEach(view -> {
            try {
                Diagram diagram = plantUMLExporter.export(view);
                writeToFile("diagrams/components.puml", diagram.getDefinition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Export to Mermaid
        MermaidDiagramExporter mermaidExporter = new MermaidDiagramExporter();
        workspace.getViews().getSystemContextViews().forEach(view -> {
            try {
                Diagram diagram = mermaidExporter.export(view);
                writeToFile("diagrams/system-context.mmd", diagram.getDefinition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workspace.getViews().getContainerViews().forEach(view -> {
            try {
                Diagram diagram = mermaidExporter.export(view);
                writeToFile("diagrams/containers.mmd", diagram.getDefinition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workspace.getViews().getComponentViews().forEach(view -> {
            try {
                Diagram diagram = mermaidExporter.export(view);
                writeToFile("diagrams/components.mmd", diagram.getDefinition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Export workspace to JSON
        WorkspaceUtils.saveWorkspaceToJson(workspace, new File("diagrams/workspace.json"));
    }

    private static void writeToFile(String filename, String content) throws Exception {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
        System.out.println("Generated: " + filename);
    }
}
