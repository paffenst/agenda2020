package agenda.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Topics. Repostory-Interfaces erben
 * eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man
 * einfach durch Benennung definierern. Spring Data ergänzt die
 * Implementierungen zur Laufzeit.
 *
 * @author Pavel St. (paffen)
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {

    /**
     * Findet alle Topics zu einem gegebenen Anwender.
     *
     * @param creator Anwender
     * @return
     */
    List<Topic> findByCreator(User creator);

    /**
     * Sortiert alphabetisch die Topicliste für Ersteller unter "Eigene Topics".
     */
    List<Topic> findByCreatorOrderByTitleAsc(User creator);

    /**
     * Sortiert alphabetisch die Topicliste für Ersteller unter "Abonnierte Topics".
     */
    List<Topic> findAllBySubscriberOrderByTitleAsc(User subscriber);

    int countByCreator(User user);

    Topic findByUuidEndingWith(String key);
}
