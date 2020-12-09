package agenda.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class TopicRepositoryTest {

        @Autowired
        TopicRepository topicRepository;

        @Autowired
        UserRepository userRepository;

        private final static String UUID_BASE = "12345678901234567890123456789012345";

        @Test
        public void topicRepositoryDeliversTopicsOrdered() {
                User user = new User("maxi", "Max Mustermann", "max123!M", false);
                userRepository.save(user);

                Topic a = new Topic(UUID_BASE + "1", "Javascript für Profis", user, "Eine Kurzbeschreibung.",
                                "Eine" + " Langbeschreibung, eine Langbeschreibung.");
                topicRepository.save(a);

                Topic b = new Topic(UUID_BASE + "2", "javascript für Profis", user, "Eine Kurzbeschreibung.",
                                "Eine" + " Langbeschreibung, eine Langbeschreibung.");
                topicRepository.save(b);

                Topic c = new Topic(UUID_BASE + "3", "Xxxxxxxxxxxxxxxx", user, "Eine Kurzbeschreibung.",
                                "Eine" + " Langbeschreibung, eine Langbeschreibung.");
                topicRepository.save(c);

                Topic d = new Topic(UUID_BASE + "4", "Aaaaaaaaaaaaaaaa", user, "Eine Kurzbeschreibung.",
                                "Eine" + " Langbeschreibung, eine Langbeschreibung.");
                topicRepository.save(d);

                Topic e = new Topic(UUID_BASE + "5", "Abaaaaaaaaaaaaaa", user, "Eine Kurzbeschreibung.",
                                "Eine" + " Langbeschreibung, eine Langbeschreibung.");
                topicRepository.save(e);

                List<Topic> topics = topicRepository.findByCreatorOrderByTitleAsc(user);

                assertEquals(5, topics.size());
                assertEquals(a, topics.get(2));
                assertEquals(b, topics.get(4));
                assertEquals(c, topics.get(3));
                assertEquals(d, topics.get(0));
                assertEquals(e, topics.get(1));
        }

}
