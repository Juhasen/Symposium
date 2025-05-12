package pl.juhas.symposium;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import pl.juhas.symposium.dto.SpeakerStatsDTO;
import pl.juhas.symposium.enums.Country;
import pl.juhas.symposium.enums.Role;
import pl.juhas.symposium.model.*;
import pl.juhas.symposium.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class SymposiumApplicationTests {

    @Autowired
    private ConferenceHallRepository conferenceHallRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private ConferenceHall conferenceHall;
    private Participant participant;
    private Presentation presentation;

    private final List<Role> roles = new ArrayList<>();

    private final List<Country> countries = new ArrayList<>();

    @BeforeEach
    void setUp() {
        roles.add(Role.STUDENT);
        roles.add(Role.DOCTOR);
        roles.add(Role.SPEAKER);
        roles.add(Role.ORGANIZER);
        roles.add(Role.ADMIN);

        countries.add(Country.POLAND);
        countries.add(Country.USA);
        countries.add(Country.CANADA);
        countries.add(Country.UK);
        countries.add(Country.AUSTRALIA);

        presentationRepository.deleteAll();
        conferenceHallRepository.deleteAll();
        participantRepository.deleteAll();
        topicRepository.deleteAll();
        hotelRepository.deleteAll();

        Hotel hotel = new Hotel();
        hotel.setName("Grand Hotel");
        hotel.setAddress("123 Main St");
        hotelRepository.save(hotel);

        conferenceHall = new ConferenceHall();
        conferenceHall.setName("Main Hall");
        conferenceHall.setHotel(hotel);
        conferenceHallRepository.save(conferenceHall);

        Topic topic = new Topic();
        topic.setName("AI in Healthcare");
        topicRepository.save(topic);

        participant = new Participant();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        participant.setEmail("john.doe@gmail.com");
        participant.setRole(Role.SPEAKER);
        participant.setCountry(Country.POLAND);
        participantRepository.save(participant);

        presentation = new Presentation();
        presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 9, 0));
        presentation.setConferenceHall(conferenceHall);
        presentation.setParticipants(List.of(participant));
        presentation.setTopic(topic);
        presentationRepository.save(presentation);
    }

    @Test
    void contextLoads() {
        assertThat(conferenceHallRepository).isNotNull();
        assertThat(participantRepository).isNotNull();
        assertThat(presentationRepository).isNotNull();
        assertThat(topicRepository).isNotNull();
        log.info("All repositories are loaded successfully.");
    }

    //1. Wyświetl listę wszystkich uczestników sympozjum.
    @Test
    void testShowAllParticipants() {
        log.info("------------testShowAllParticipants------------");
        for (int i = 0; i < 5; i++) {
            Participant participant = new Participant();
            participant.setFirstName("Participant" + i);
            participant.setLastName("LastName" + i);
            participant.setEmail("participant" + i + "@gmail.com");
            participant.setRole(roles.get(i));
            participant.setCountry(countries.get(i));
            participantRepository.save(participant);
        }
        List<Participant> participants = participantRepository.findAll();
        assertThat(participants).isNotEmpty();
        assertThat(participants.size()).isEqualTo(6);
        assertThat(participants.getFirst().getFirstName()).isEqualTo("John");
        participants.forEach(participant ->
                log.info("Participant: {} {} {}", participant.getFirstName(), participant.getLastName(), participant.getEmail()));
        log.info("Test for showing all participants passed successfully.");
    }

    //2. Wyświetl listę uczestników z podziałem na lekarzy, studentów, organizatorów, itp.
    @Test
    void testShowParticipantsGroupedByRole() {
        log.info("------------testShowParticipantsGroupedByRole------------");
        for (int i = 0; i < 23; i++) {
            Participant participant = new Participant();
            participant.setFirstName("Participant" + i);
            participant.setLastName("LastName" + i);
            participant.setEmail("participant" + i + "@gmail.com");
            participant.setRole(roles.get(i % 5));
            participant.setCountry(countries.get(i % 5));
            participantRepository.save(participant);
        }

        List<Object[]> participants = participantRepository.findAllParticipantsWithRoles();

        // Mapowanie i grupowanie uczestników według ról
        Map<Role, List<Participant>> groupedByRole = participants.stream()
                .collect(Collectors.groupingBy(
                        obj -> (Role) obj[0], // Grupowanie według roli (pierwszy element tablicy)
                        Collectors.mapping(
                                obj -> (Participant) obj[1], // Mapowanie na uczestnika (drugi element tablicy)
                                Collectors.toList()
                        )
                ));

        // Wyświetlanie wyników
        groupedByRole.forEach((role, participantList) -> {
            log.info("Role: {}", role);
            participantList.forEach(participant ->
                    log.info("Participant: {} {} {}", participant.getFirstName(), participant.getLastName(), participant.getEmail()));
        });
    }

    //3. Wyświetl listę z podziałem na kraj pochodzenia.
    @Test
    void testShowParticipantsGroupedByCountry() {
        log.info("------------testShowParticipantsGroupedByCountry------------");
        for (int i = 0; i < 23; i++) {
            Participant participant = new Participant();
            participant.setFirstName("Participant" + i);
            participant.setLastName("LastName" + i);
            participant.setEmail("participant" + i + "@gmail.com");
            participant.setRole(roles.get(i % 5));
            participant.setCountry(countries.get(i % 5));
            participantRepository.save(participant);
        }

        List<Participant> participants = participantRepository.findAll();

        // Mapowanie i grupowanie uczestników według krajów
        Map<Country, List<Participant>> groupedByCountry = participants.stream()
                .collect(Collectors.groupingBy(Participant::getCountry));

        // Wyświetlanie wyników
        groupedByCountry.forEach((country, participantList) -> {
            log.info("Country: {}", country);
            participantList.forEach(participant ->
                    log.info("Participant: {} {} {}", participant.getFirstName(), participant.getLastName(), participant.getEmail()));
        });
    }

    //4. Wyświetl listę tematów prezentacji.
    @Test
    void testShowAllPresentationTopics(){
        log.info("------------testShowAllPresentationTopics------------");
        for (int i = 0; i < 5; i++) {
            Topic topic = new Topic();
            topic.setName("Topic" + i);
            topicRepository.save(topic);
            Presentation presentation = new Presentation();
            presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 10 + i, 0));
            presentation.setConferenceHall(conferenceHall);
            presentation.setParticipants(List.of(participant));
            presentation.setTopic(topic);
            presentationRepository.save(presentation);
        }

        List<Presentation> presentations = presentationRepository.findAll();
        assertThat(presentations).isNotEmpty();

        presentations.forEach(presentation ->
                log.info("Presentation: {} at {}", presentation.getTopic().getName(), presentation.getStartTime()));
    }

    //5. Wyświetl użytkownika z największą liczbą prezentacji.
    @Test
    void testFindParticipantWithMostPresentations() {
        log.info("------------testFindParticipantWithMostPresentations------------");
        // Add 5 additional presentations with John Doe as a speaker
        for (int i = 0; i < 5; i++) {
            Topic topic = new Topic();
            topic.setName("Topic " + i);
            topicRepository.save(topic);

            Presentation presentation = new Presentation();
            presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 10 + i, 0));
            presentation.setConferenceHall(conferenceHall);
            presentation.setTopic(topic);

            // Create 5 STUDENT participants
            List<Participant> students = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                Participant student = new Participant();
                student.setFirstName("Student" + i + "_" + j);
                student.setLastName("Last" + j);
                student.setEmail("student" + i + "_" + j + "@example.com");
                student.setRole(Role.STUDENT);
                student.setCountry(participant.getCountry()); // or any Country
                participantRepository.save(student);
                students.add(student);
            }

            // Add John Doe (speaker) and 5 students to the presentation
            List<Participant> allParticipants = new ArrayList<>(students);
            allParticipants.add(participant); // John Doe

            presentation.setParticipants(allParticipants);
            presentationRepository.save(presentation);
        }

        // Now John Doe has 1 (from @BeforeEach) + 5 = 6 presentations

        List<SpeakerStatsDTO> topSpeakers = presentationRepository.findTopSpeaker(Pageable.ofSize(10));

        assertThat(topSpeakers).isNotEmpty();
        topSpeakers.forEach(speaker ->
                log.info("Speaker: {} {} with {} presentations", speaker.firstName(), speaker.lastName(), speaker.presentationCount()));
        SpeakerStatsDTO top = topSpeakers.getFirst();
        assertThat(top.firstName()).isEqualTo("John");
        assertThat(top.lastName()).isEqualTo("Doe");
        assertThat(top.presentationCount()).isEqualTo(6L);
    }

    //6. Wyświetl liczbę prezentacji w każdej sali.
    @Test
    void testCountPresentationsInEachHall() {
        log.info("------------testCountPresentationsInEachHall------------");
        // Existing halls and presentations setup
        for (int i = 0; i < 5; i++) {
            Topic topic = new Topic();
            topic.setName("Topic" + i);
            topicRepository.save(topic);
            Presentation presentation = new Presentation();
            presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 10 + i, 0));
            presentation.setConferenceHall(conferenceHall);
            presentation.setParticipants(List.of(participant));
            presentation.setTopic(topic);
            presentationRepository.save(presentation);
        }

        // Create one more conference hall with 3 presentations
        ConferenceHall newHall = new ConferenceHall();
        newHall.setName("New Conference Hall");
        conferenceHallRepository.save(newHall);

        for (int i = 0; i < 3; i++) {
            Topic topic = new Topic();
            topic.setName("Topic" + (i + 5));
            topicRepository.save(topic);
            Presentation presentation = new Presentation();
            presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 15 + i, 0));
            presentation.setConferenceHall(newHall);
            presentation.setParticipants(List.of(participant));
            presentation.setTopic(topic);
            presentationRepository.save(presentation);
        }

        List<ConferenceHall> conferenceHalls = conferenceHallRepository.findAll();
        assertThat(conferenceHalls).isNotEmpty();

        conferenceHalls.forEach(hall -> {
            long count = presentationRepository.countPresentationsByConferenceHall(hall);
            log.info("Conference Hall: {} has {} presentations", hall.getName(), count);
        });
    }




}
