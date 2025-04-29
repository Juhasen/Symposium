package pl.juhas.symposium;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import pl.juhas.symposium.repository.ConferenceHallRepository;
import pl.juhas.symposium.repository.ParticipantRepository;
import pl.juhas.symposium.repository.PresentationRepository;
import pl.juhas.symposium.repository.TopicRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@Rollback
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


    @Test
    void contextLoads() {
        assertThat(conferenceHallRepository).isNotNull();
        assertThat(participantRepository).isNotNull();
        assertThat(presentationRepository).isNotNull();
        assertThat(topicRepository).isNotNull();
        log.info("All repositories are loaded successfully.");
    }



}
