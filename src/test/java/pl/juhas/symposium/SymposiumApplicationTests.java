package pl.juhas.symposium;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import pl.juhas.symposium.dto.ParticipantDTO;
import pl.juhas.symposium.dto.PresentationDTO;
import pl.juhas.symposium.dto.SpeakerStatsDTO;
import pl.juhas.symposium.enums.Country;
import pl.juhas.symposium.enums.Role;
import pl.juhas.symposium.model.*;
import pl.juhas.symposium.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private final int ROLES_COUNT = 3;
    private final int COUNTRIES_COUNT = 5;
    private final List<Country> countries = new ArrayList<>();

    @BeforeEach
    void setUp() {
        roles.add(Role.STUDENT);
        roles.add(Role.DOCTOR);
        roles.add(Role.ORGANIZER);

        countries.add(Country.POLAND);
        countries.add(Country.USA);
        countries.add(Country.CANADA);
        countries.add(Country.UK);
        countries.add(Country.AUSTRALIA);

        presentationRepository.deleteAll();
        conferenceHallRepository.deleteAll();
        topicRepository.deleteAll();
        participantRepository.deleteAll();

        hotelRepository.deleteAll();

        Hotel hotel = new Hotel();
        hotel.setName("Grand Hotel");
        hotel.setAddress("123 Main St");
        hotelRepository.save(hotel);

        conferenceHall = new ConferenceHall();
        conferenceHall.setName("Main Hall");
        conferenceHall.setHotel(hotel);
        conferenceHallRepository.save(conferenceHall);

        participant = new Participant();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        participant.setEmail("john.doe@gmail.com");
        participant.setCountry(Country.POLAND);
        participantRepository.save(participant);


        Topic topic = new Topic();
        topic.setName("AI in Healthcare");
        topic.setPresenters(Set.of(participant));
        topicRepository.save(topic);


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
            participant.setRole(roles.get(i % ROLES_COUNT));
            participant.setCountry(countries.get(i));
            participantRepository.save(participant);
        }
        List<ParticipantDTO> participants = participantRepository.findAllAsDto();
        assertThat(participants).isNotEmpty();
        assertThat(participants.size()).isEqualTo(6);
        participants.forEach(participantDTO ->
                log.info(participantDTO.toString())
        );
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
            participant.setRole(roles.get(i % ROLES_COUNT));
            participant.setCountry(countries.get(i % COUNTRIES_COUNT));
            participantRepository.save(participant);
        }

        List<ParticipantDTO> participants = participantRepository.findAllParticipantsOrderedByRoles();

        Map<Role, List<ParticipantDTO>> groupedByRole = participants.stream()
                .filter(p -> p.role() != null)
                .collect(Collectors.groupingBy(ParticipantDTO::role));

        // Wyświetlanie pogrupowanych uczestników
        groupedByRole.forEach((role, participantList) -> {
            log.info("Role: {}", role);
            participantList.forEach(participant -> log.info(" - {}", participant));
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
            participant.setRole(roles.get(i % 3));
            participant.setCountry(countries.get(i % 5));
            participantRepository.save(participant);
        }

        List<ParticipantDTO> participants = participantRepository.findAllAsDto();

        // Mapowanie i grupowanie uczestników według krajów
        Map<Country, List<ParticipantDTO>> groupedByCountry = participants.stream()
                .collect(Collectors.groupingBy(ParticipantDTO::country));

        // Wyświetlanie wyników
        groupedByCountry.forEach((country, participantList) -> {
            log.info("Country: {}", country);
            participantList.forEach(participantDTO -> {
                log.info(participantDTO.toString());
            });
        });
    }


    //4. Wyświetl listę tematów prezentacji.
    @Test
    void testShowAllPresentationTopics() {
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

        List<PresentationDTO> presentations = presentationRepository.findAllAsDto();
        assertThat(presentations).isNotEmpty();

        presentations.forEach(presentationDTO ->
                log.info(presentationDTO.toString()));
    }

    //5. Wyświetl użytkownika z największą liczbą prezentacji.
    @Test
    void testFindParticipantWithMostPresentations() {
        log.info("------------testFindParticipantWithMostPresentations------------");

        // Inicjalizacja drugiego prezentera
        Participant otherParticipant = new Participant();
        otherParticipant.setFirstName("Jane");
        otherParticipant.setLastName("Smith");
        otherParticipant.setEmail("jane.smith@example.com");
        otherParticipant.setRole(Role.STUDENT);
        otherParticipant.setCountry(countries.get(0));
        participantRepository.save(otherParticipant);

        int totalPresentations = 5;

        for (int i = 0; i < totalPresentations; i++) {
            Topic topic = new Topic();
            topic.setName("Topic " + i);
            topicRepository.save(topic);

            Presentation presentation = new Presentation();
            presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 10 + i, 0));
            presentation.setConferenceHall(conferenceHall);
            presentation.setTopic(topic);

            List<Participant> students = new ArrayList<>();
            for (int j = 0; j < 23; j++) {
                Participant student = new Participant();
                student.setFirstName("Student" + i + "_" + j);
                student.setLastName("Last" + j);
                student.setEmail("student" + i + "_" + j + "@example.com");
                student.setRole(Role.STUDENT);
                student.setCountry(countries.get(i % COUNTRIES_COUNT));
                participantRepository.save(student);
                students.add(student);
            }

            Set<Participant> presenters = new HashSet<>();
            if (i < 3) {
                presenters.add(participant);
            } else {
                presenters.add(otherParticipant);
            }
            topic.setPresenters(presenters);
            topicRepository.save(topic);

            // Dodaj obu do listy uczestników
            students.add(participant);
            students.add(otherParticipant);
            presentation.setParticipants(students);
            presentationRepository.save(presentation);
        }

        List<SpeakerStatsDTO> topSpeakers = presentationRepository.findTopSpeaker(Pageable.ofSize(1));

        assertThat(topSpeakers).isNotEmpty();
        topSpeakers.forEach(speaker ->
                log.info("Speaker: {} {} with {} presentations", speaker.firstName(), speaker.lastName(), speaker.presentationCount()));

        SpeakerStatsDTO top = topSpeakers.getFirst();
        assertThat(top.firstName()).isEqualTo("John");
        assertThat(top.lastName()).isEqualTo("Doe");
        assertThat(top.presentationCount()).isEqualTo(4L);
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

    @Test
    void testShouldNotBeMoreThanOnePresentationAtSameTopicAndTime() {
        log.info("------------testShouldNotBeMoreThanOnePresentationAtSameTopicAndTime------------");

        // Create Conference Hall
        ConferenceHall conferenceHall = new ConferenceHall();
        conferenceHall.setName("Main Hall");
        conferenceHallRepository.save(conferenceHall);

        // Create one topic
        Topic topic = new Topic();
        topic.setName("Topic 1");
        topicRepository.save(topic);

        LocalDateTime startTime = LocalDateTime.of(2025, 4, 29, 10, 0);

        // First presentation
        Presentation presentation1 = new Presentation();
        presentation1.setStartTime(startTime);
        presentation1.setConferenceHall(conferenceHall);
        presentation1.setTopic(topic);

        // Second presentation with the same topic and time
        Presentation presentation2 = new Presentation();
        presentation2.setStartTime(startTime);
        presentation2.setConferenceHall(this.conferenceHall); // optional, could be different
        presentation2.setTopic(topic);

        // Save first presentation
        presentationRepository.save(presentation1);

        // Saving the second should fail due to same topic + same time
        assertThrows(DataIntegrityViolationException.class, () -> {
            presentationRepository.save(presentation2);
        }, "A presentation with the same topic and time should not be allowed.");

        log.info("Presentations with the same topic and time were not allowed — test passed.");
    }

    @Test
    void testShouldNotBeMoreThanOnePresentationAtSameConferenceHallAndTime() {
        log.info("------------testShouldNotBeMoreThanOnePresentationAtSameConferenceHallAndTime------------");

        // Create Conference Hall
        ConferenceHall conferenceHall = new ConferenceHall();
        conferenceHall.setName("Main Hall");
        conferenceHallRepository.save(conferenceHall);

        // Create one topic
        Topic topic = new Topic();
        topic.setName("Topic 1");
        topicRepository.save(topic);

        // Create one topic
        Topic another_topic = new Topic();
        another_topic.setName("Topic 2");
        topicRepository.save(another_topic);

        LocalDateTime startTime = LocalDateTime.of(2025, 4, 29, 10, 0);

        // First presentation
        Presentation presentation1 = new Presentation();
        presentation1.setStartTime(startTime);
        presentation1.setConferenceHall(conferenceHall);
        presentation1.setTopic(topic);

        // Second presentation with the same topic and time
        Presentation presentation2 = new Presentation();
        presentation2.setStartTime(startTime);
        presentation2.setConferenceHall(conferenceHall); // same conference hall
        presentation2.setTopic(another_topic);

        // Save first presentation
        presentationRepository.save(presentation1);

        // Saving the second should fail due to same topic + same time
        assertThrows(DataIntegrityViolationException.class, () -> {
            presentationRepository.save(presentation2);
        }, "A presentation with the same conference hall and time should not be allowed.");

        log.info("Presentations with the same conference hall and time were not allowed — test passed.");
    }

    @Test
    void testTwoSameTopicsCantExist() {
        log.info("------------testTwoSameTopicsCantExist------------");
        // Create one topic
        Topic topic = new Topic();
        topic.setName("Topic 1");
        topicRepository.save(topic);

        // Create one topic
        Topic another_topic = new Topic();
        another_topic.setName("Topic 1");

        // Saving the second should fail due to same topic + same time
        assertThrows(DataIntegrityViolationException.class, () -> {
            topicRepository.save(another_topic);
        }, "A topic with the same name should not be allowed.");

        log.info("Topics should be unique — test passed.");
    }

    @Test
    void testShouldPresentationsNotExistWithSameTopics() {
        log.info("------------testShouldPresentationsNotExistWithSameTopics------------");

        // Create one topic
        Topic topic = new Topic();
        topic.setName("Topic 1");
        topicRepository.save(topic);

        // First presentation
        Presentation presentation1 = new Presentation();
        presentation1.setTopic(topic);

        // Second presentation with the same topic and time
        Presentation presentation2 = new Presentation();
        presentation2.setTopic(topic);

        // Save first presentation
        presentationRepository.save(presentation1);

        // Saving the second should fail due to same topic + same time
        assertThrows(DataIntegrityViolationException.class, () -> {
            presentationRepository.save(presentation2);
        }, "A presentation with the same topic should not be allowed.");

        log.info("Presentations with the same topics were not allowed — test passed.");
    }


}
