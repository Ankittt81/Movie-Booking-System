package com.system.design.moviebooking.config;

import com.system.design.moviebooking.dto.movieAndTheatre.MovieRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.MovieResponseDto;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreRequestDto;
import com.system.design.moviebooking.dto.movieAndTheatre.TheatreResponseDto;
import com.system.design.moviebooking.dto.seatAndShow.ShowRequestDto;
import com.system.design.moviebooking.model.enums.Role;
import com.system.design.moviebooking.model.enums.TheatreStatus;
import com.system.design.moviebooking.model.mainEntity.User;
import com.system.design.moviebooking.repository.UserRepository;
import com.system.design.moviebooking.service.IMovieService;
import com.system.design.moviebooking.service.IShowService;
import com.system.design.moviebooking.service.ITheatreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Dev-only: runs once at startup when app.seed.enabled=true
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String ADMIN_EMAIL = "admin@bms.com";
    private static final String PARTNER_EMAIL = "partner@bms.com";
    private static final String CUSTOMER_EMAIL = "customer@bms.com";

    // show start times; slots are 4 hours apart so shows in one theatre never overlap
    private static final LocalTime[] SLOTS = {
            LocalTime.of(10, 30), LocalTime.of(14, 30), LocalTime.of(18, 30), LocalTime.of(22, 0)
    };
    private static final int DAYS_AHEAD = 3;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final IMovieService movieService;
    private final ITheatreService theatreService;
    private final IShowService showService;
    private final String seedPassword;

    public DataSeeder(UserRepository userRepository,
                      BCryptPasswordEncoder passwordEncoder,
                      IMovieService movieService,
                      ITheatreService theatreService,
                      IShowService showService,
                      @Value("${app.seed.password:Password@123}") String seedPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.movieService = movieService;
        this.theatreService = theatreService;
        this.showService = showService;
        this.seedPassword = seedPassword;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("Seed data already present, skipping");
            return;
        }

        User admin = createUser("Admin", ADMIN_EMAIL, Role.ADMIN, Role.PARTNER, Role.USER);
        User partner = createUser("Partner", PARTNER_EMAIL, Role.PARTNER, Role.USER);
        createUser("Customer", CUSTOMER_EMAIL, Role.USER);

        List<Long> movieIds = seedMovies();

        // two approved theatres with shows, one left PENDING to test the admin approval screen
        Long theatreA = seedTheatre(partner, "PVR Cinemas", "Sector 18", "Noida", true);
        Long theatreB = seedTheatre(partner, "INOX Metro", "Connaught Place", "Delhi", true);
        seedTheatre(partner, "Cinepolis Square", "Sector 62", "Noida", false);

        // theatre A: movies 0-3, theatre B: movies 2-5, one slot per movie
        seedShows(theatreA, partner.getId(), movieIds.subList(0, 4), 8, 12, new BigDecimal("250"));
        seedShows(theatreB, partner.getId(), movieIds.subList(2, 6), 6, 10, new BigDecimal("320"));

        log.info("Seed complete. Logins: {}, {}, {} / password: {}",
                ADMIN_EMAIL, PARTNER_EMAIL, CUSTOMER_EMAIL, seedPassword);
    }

    private User createUser(String name, String email, Role... roles) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(seedPassword));
        user.getRole().addAll(List.of(roles));
        return userRepository.save(user);
    }

    private List<Long> seedMovies() {
        List<Long> ids = new ArrayList<>();
        ids.add(movie("Midnight Express Run", "Action", "English", 128, LocalDate.now().minusDays(20),
                "A courier races across the city to deliver a package before dawn."));
        ids.add(movie("Chai Aur Chaos", "Comedy", "Hindi", 142, LocalDate.now().minusDays(12),
                "Three roommates run a failing tea stall and make it everyone's problem."));
        ids.add(movie("The Last Signal", "Thriller", "English", 115, LocalDate.now().minusDays(8),
                "A radio operator picks up a distress call from a place that does not exist."));
        ids.add(movie("Dil Ka Darwaza", "Love", "Hindi", 155, LocalDate.now().minusDays(5),
                "Two strangers keep missing each other across one monsoon season."));
        ids.add(movie("Echoes of the Valley", "Mystery", "English", 133, LocalDate.now().minusDays(3),
                "A journalist returns to her hometown to uncover a decades-old disappearance."));
        ids.add(movie("Veer Ki Kahani", "Patriot", "Hindi", 160, LocalDate.now().minusDays(1),
                "The story of a young soldier and the village that raised him."));
        return ids;
    }

    private Long movie(String title, String genre, String language, int duration,
                       LocalDate releaseDate, String description) {
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle(title);
        request.setGenre(genre);
        request.setLanguage(language);
        request.setDuration(duration);
        request.setReleaseDate(releaseDate);
        request.setDescription(description);
        request.setPoster("https://placehold.co/300x450?text=" + title.replace(" ", "+"));
        MovieResponseDto saved = movieService.createMovie(request);
        return saved.getId();
    }

    private Long seedTheatre(User owner, String name, String address, String city, boolean approve) {
        TheatreRequestDto request = new TheatreRequestDto();
        request.setName(name);
        request.setAddress(address);
        request.setCity(city);
        request.setPhone("9876543210");
        request.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        TheatreResponseDto saved = theatreService.create(request, owner.getId());
        if (approve) {
            theatreService.changeStatus(saved.getId(), TheatreStatus.ACTIVE);
        }
        return saved.getId();
    }

    private void seedShows(Long theatreId, Long ownerId, List<Long> movieIds,
                           int rows, int seatsPerRow, BigDecimal price) {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < movieIds.size(); i++) {
            for (int day = 0; day < DAYS_AHEAD; day++) {
                LocalDateTime start = LocalDate.now().plusDays(day).atTime(SLOTS[i]);
                if (!start.isAfter(now)) {
                    continue; // skip slots that have already passed today
                }
                ShowRequestDto request = new ShowRequestDto();
                request.setTheatreId(theatreId);
                request.setMovieId(movieIds.get(i));
                request.setStartTime(start);
                request.setTicketPrice(price);
                request.setRowCount(rows);
                request.setSeatsPerRow(seatsPerRow);
                showService.createShow(request, ownerId);
            }
        }
    }
}