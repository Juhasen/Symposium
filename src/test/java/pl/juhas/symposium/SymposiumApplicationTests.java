package pl.juhas.symposium;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.juhas.symposium.enums.Country;
import pl.juhas.symposium.enums.Role;
import pl.juhas.symposium.model.*;
import pl.juhas.symposium.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    private List<Role> roles = new ArrayList<>();

    private List<Country> countries = new ArrayList<>();

    @BeforeEach
    void setUp() {
        log.info("Setting up the test environment.");

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
        participant.setRole(Role.DOCTOR);
        participant.setCountry(Country.POLAND);
        participantRepository.save(participant);

        presentation = new Presentation();
        presentation.setStartTime(LocalDateTime.of(2025, 4, 29, 10, 0));
        presentation.setConferenceHall(conferenceHall);
        presentation.setParticipant(participant);
        presentation.setTopic(topic);
        presentationRepository.save(presentation);

        log.info("Test environment set up successfully.");
    }

    @Test
    void contextLoads() {
        assertThat(conferenceHallRepository).isNotNull();
        assertThat(participantRepository).isNotNull();
        assertThat(presentationRepository).isNotNull();
        assertThat(topicRepository).isNotNull();
        log.info("All repositories are loaded successfully.");
    }

    @Test
    void testShowAllParticipants() {
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
                log.info("Participant: " + participant.getFirstName() +
                        " " +
                        participant.getLastName() +
                        " " +
                        participant.getEmail()));
        log.info("Test for showing all participants passed successfully.");
    }


}
