package com.interview.config;

import com.interview.model.Account;
import com.interview.model.Event;
import com.interview.model.Performer;
import com.interview.model.Role;
import com.interview.model.Venue;
import com.interview.repository.AccountRepository;
import com.interview.repository.EventRepository;
import com.interview.repository.PerformerRepository;
import com.interview.repository.RoleRepository;
import com.interview.repository.VenueRepository;
import com.interview.service.EventSearchService;
import com.interview.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private PerformerRepository performerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventSearchService searchService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (accountRepository.count() > 0) {
            System.out.println("Data already seeded, skipping...");
            return;
        }

        // Create roles
        Role adminRole = new Role("ADMIN");
        adminRole = roleRepository.save(adminRole);

        Role userRole = new Role("USER");
        userRole = roleRepository.save(userRole);

        // Create default admin account
        Account admin = new Account();
        admin.setFirstName("Lingchao");
        admin.setLastName("Kong");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setPhone("123-456-7890");
        admin.getRoles().add(adminRole);
        accountRepository.save(admin);

        // Create 20 user accounts
        createUserAccount("John", "Smith", "john.smith@example.com", "555-0101", userRole);
        createUserAccount("Emma", "Johnson", "emma.johnson@example.com", "555-0102", userRole);
        createUserAccount("Michael", "Williams", "michael.williams@example.com", "555-0103", userRole);
        createUserAccount("Sophia", "Brown", "sophia.brown@example.com", "555-0104", userRole);
        createUserAccount("James", "Jones", "james.jones@example.com", "555-0105", userRole);
        createUserAccount("Olivia", "Garcia", "olivia.garcia@example.com", "555-0106", userRole);
        createUserAccount("William", "Martinez", "william.martinez@example.com", "555-0107", userRole);
        createUserAccount("Ava", "Rodriguez", "ava.rodriguez@example.com", "555-0108", userRole);
        createUserAccount("David", "Davis", "david.davis@example.com", "555-0109", userRole);
        createUserAccount("Isabella", "Miller", "isabella.miller@example.com", "555-0110", userRole);
        createUserAccount("Robert", "Wilson", "robert.wilson@example.com", "555-0111", userRole);
        createUserAccount("Mia", "Moore", "mia.moore@example.com", "555-0112", userRole);
        createUserAccount("Daniel", "Taylor", "daniel.taylor@example.com", "555-0113", userRole);
        createUserAccount("Charlotte", "Anderson", "charlotte.anderson@example.com", "555-0114", userRole);
        createUserAccount("Matthew", "Thomas", "matthew.thomas@example.com", "555-0115", userRole);
        createUserAccount("Amelia", "Jackson", "amelia.jackson@example.com", "555-0116", userRole);
        createUserAccount("Joseph", "White", "joseph.white@example.com", "555-0117", userRole);
        createUserAccount("Harper", "Harris", "harper.harris@example.com", "555-0118", userRole);
        createUserAccount("Christopher", "Martin", "christopher.martin@example.com", "555-0119", userRole);
        createUserAccount("Evelyn", "Thompson", "evelyn.thompson@example.com", "555-0120", userRole);

        // Create venues
        Venue msgVenue = createVenue("Madison Square Garden", "4 Pennsylvania Plaza", "New York", "NY", "10001", 20000);
        Venue sofiVenue = createVenue("SoFi Stadium", "1001 Stadium Dr", "Inglewood", "CA", "90301", 70000);
        Venue attVenue = createVenue("AT&T Stadium", "1 AT&T Way", "Arlington", "TX", "76011", 80000);
        Venue unitedVenue = createVenue("United Center", "1901 W Madison St", "Chicago", "IL", "60612", 23500);
        Venue chasevenue = createVenue("Chase Center", "1 Warriors Way", "San Francisco", "CA", "94158", 18064);

        // Create performers
        Performer taylorSwift = createPerformer("Taylor Swift", "Pop", "American singer-songwriter known for narrative songs about her personal life");
        Performer beyonce = createPerformer("Beyoncé", "R&B/Pop", "American singer, songwriter, and actress, one of the world's best-selling music artists");
        Performer edSheeran = createPerformer("Ed Sheeran", "Pop/Folk", "English singer-songwriter known for his acoustic pop songs");
        Performer theWeeknd = createPerformer("The Weeknd", "R&B/Pop", "Canadian singer, songwriter, and record producer known for his distinctive voice");
        Performer coldplay = createPerformer("Coldplay", "Alternative Rock", "British rock band known for their atmospheric sound and worldwide tours");

        // Create sample events
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        createEvent("Spring Boot Workshop", "Learn the fundamentals of Spring Boot framework",
                    "2025-11-15 10:00:00", "San Francisco, CA", formatter, chasevenue, null);
        createEvent("Java Conference 2025", "Annual Java developers conference with industry experts",
                    "2025-12-01 09:00:00", "New York, NY", formatter, msgVenue, null);
        createEvent("Microservices Meetup", "Discussion on microservices architecture patterns",
                    "2025-11-20 18:30:00", "Austin, TX", formatter, null, null);
        createEvent("Taylor Swift - The Eras Tour", "Experience the magic of Taylor Swift performing hits from all her albums",
                    "2025-11-28 19:30:00", "MetLife Stadium, East Rutherford, NJ", formatter, null, Set.of(taylorSwift));
        createEvent("Taylor Swift - The Eras Tour", "Taylor Swift live in concert with special guests",
                    "2025-12-05 19:30:00", "SoFi Stadium, Los Angeles, CA", formatter, sofiVenue, Set.of(taylorSwift));
        createEvent("Beyoncé - Renaissance World Tour", "Queen Bey brings the Renaissance experience to life",
                    "2025-12-10 20:00:00", "AT&T Stadium, Arlington, TX", formatter, attVenue, Set.of(beyonce));
        createEvent("Ed Sheeran - Mathematics Tour", "Ed Sheeran performs acoustic and electric hits",
                    "2025-11-25 19:00:00", "Madison Square Garden, New York, NY", formatter, msgVenue, Set.of(edSheeran));
        createEvent("The Weeknd - After Hours Tour", "The Weeknd live with stunning visual effects",
                    "2025-12-08 20:00:00", "United Center, Chicago, IL", formatter, unitedVenue, Set.of(theWeeknd));
        createEvent("NBA: Lakers vs Warriors", "Los Angeles Lakers take on Golden State Warriors in Western Conference showdown",
                    "2025-11-22 19:30:00", "Crypto.com Arena, Los Angeles, CA", formatter, null, null);
        createEvent("NBA: Celtics vs Heat", "Boston Celtics vs Miami Heat - Eastern Conference rivalry game",
                    "2025-11-26 20:00:00", "TD Garden, Boston, MA", formatter, null, null);

        // Additional events
        createEvent("NBA: Nets vs Knicks", "Brooklyn Nets vs New York Knicks - Battle for New York",
                    "2025-12-03 19:00:00", "Barclays Center, Brooklyn, NY", formatter, null, null);
        createEvent("NBA: Mavericks vs Suns", "Dallas Mavericks vs Phoenix Suns featuring Luka Doncic",
                    "2025-12-07 20:30:00", "American Airlines Center, Dallas, TX", formatter, null, null);
        createEvent("NBA: Bucks vs 76ers", "Milwaukee Bucks vs Philadelphia 76ers - Championship contenders clash",
                    "2025-12-12 19:30:00", "Fiserv Forum, Milwaukee, WI", formatter, null, null);
        createEvent("NFL: Chiefs vs Bills", "Kansas City Chiefs vs Buffalo Bills - AFC Championship rematch",
                    "2025-11-24 16:25:00", "Arrowhead Stadium, Kansas City, MO", formatter, null, null);
        createEvent("NFL: Cowboys vs Eagles", "Dallas Cowboys vs Philadelphia Eagles - NFC East rivalry",
                    "2025-12-01 13:00:00", "AT&T Stadium, Arlington, TX", formatter, attVenue, null);
        createEvent("NFL: 49ers vs Seahawks", "San Francisco 49ers vs Seattle Seahawks - NFC West showdown",
                    "2025-12-14 17:15:00", "Levi's Stadium, Santa Clara, CA", formatter, null, null);
        createEvent("MLS Cup Final 2025", "Major League Soccer Championship Final",
                    "2025-12-09 15:00:00", "BMO Stadium, Los Angeles, CA", formatter, null, null);
        createEvent("UFC 300", "Ultimate Fighting Championship - Stacked fight card",
                    "2025-11-30 22:00:00", "T-Mobile Arena, Las Vegas, NV", formatter, null, null);
        createEvent("Drake - It's All a Blur Tour", "Drake live with special surprise guests",
                    "2025-12-15 20:00:00", "Scotiabank Arena, Toronto, ON", formatter, null, null);
        createEvent("Coldplay - Music of the Spheres", "Coldplay's spectacular world tour experience",
                    "2025-12-18 19:30:00", "Rose Bowl, Pasadena, CA", formatter, null, Set.of(coldplay));
        createEvent("Billie Eilish - Hit Me Hard and Soft Tour", "Billie Eilish performing new and classic hits",
                    "2025-12-20 20:00:00", "Chase Center, San Francisco, CA", formatter, chasevenue, null);
        createEvent("React Summit 2025", "The biggest React conference of the year",
                    "2025-11-18 09:00:00", "Austin Convention Center, Austin, TX", formatter, null, null);
        createEvent("AWS re:Invent 2025", "Amazon Web Services annual cloud computing conference",
                    "2025-12-02 08:00:00", "Venetian Convention Center, Las Vegas, NV", formatter, null, null);
        createEvent("DockerCon 2025", "Container technology conference and expo",
                    "2025-11-30 09:00:00", "Seattle Convention Center, Seattle, WA", formatter, null, null);
        createEvent("KubeCon 2025", "Cloud Native Computing Foundation flagship conference",
                    "2025-12-11 08:30:00", "McCormick Place, Chicago, IL", formatter, unitedVenue, null);
        createEvent("GitHub Universe 2025", "GitHub's annual developer conference",
                    "2025-11-21 09:00:00", "Yerba Buena Center, San Francisco, CA", formatter, chasevenue, null);
        createEvent("Harry Styles - Love On Tour", "Harry Styles performing worldwide hits",
                    "2025-12-22 19:30:00", "Madison Square Garden, New York, NY", formatter, msgVenue, null);
        createEvent("Adele - Weekends with Adele", "Adele's exclusive Las Vegas residency",
                    "2025-12-28 20:00:00", "The Colosseum at Caesars Palace, Las Vegas, NV", formatter, null, null);
        createEvent("Bruno Mars - 24K Magic Tour", "Bruno Mars live in concert",
                    "2025-12-30 20:00:00", "Park MGM, Las Vegas, NV", formatter, null, null);
        createEvent("NHL: Rangers vs Islanders", "New York Rangers vs New York Islanders - Battle of New York on ice",
                    "2025-12-06 19:00:00", "Madison Square Garden, New York, NY", formatter, msgVenue, null);

        // Pre-populate Redis cache with all events
        System.out.println("Pre-populating Redis cache with events...");
        eventService.getEventsCursorPaginated(null, 10);
        System.out.println("Redis cache populated successfully!");

        // Index events to OpenSearch
        System.out.println("Indexing events to OpenSearch...");
        try {
            searchService.indexAllEvents();
            System.out.println("OpenSearch indexing completed successfully!");
        } catch (Exception e) {
            System.out.println("Warning: OpenSearch indexing failed - " + e.getMessage());
            System.out.println("The application will continue, but OpenSearch search may not be available.");
        }

        System.out.println("Data seeding completed successfully! Created " + roleRepository.count() +
                          " roles, " + accountRepository.count() + " accounts, " + eventRepository.count() +
                          " events, " + venueRepository.count() + " venues, and " + performerRepository.count() + " performers.");
    }

    private Venue createVenue(String name, String address, String city, String state, String zipCode, Integer capacity) {
        Venue venue = new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setCity(city);
        venue.setState(state);
        venue.setZipCode(zipCode);
        venue.setCapacity(capacity);
        return venueRepository.save(venue);
    }

    private Performer createPerformer(String name, String genre, String bio) {
        Performer performer = new Performer();
        performer.setName(name);
        performer.setGenre(genre);
        performer.setBio(bio);
        return performerRepository.save(performer);
    }

    private void createEvent(String name, String description, String dateStr, String location,
                            DateTimeFormatter formatter, Venue venue, Set<Performer> performers) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setEventDate(LocalDateTime.parse(dateStr, formatter));
        event.setLocation(location);

        if (venue != null) {
            event.setVenue(venue);
        }

        if (performers != null && !performers.isEmpty()) {
            event.setPerformers(new HashSet<>(performers));
        }

        eventRepository.save(event);
    }

    private void createUserAccount(String firstName, String lastName, String email, String phone, Role role) {
        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode("password123"));
        account.setPhone(phone);
        account.getRoles().add(role);
        accountRepository.save(account);
    }
}