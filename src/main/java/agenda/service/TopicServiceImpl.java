package agenda.service;

import agenda.common.UuidProviderImpl;
import agenda.persistence.*;
import agenda.service.dto.OwnerTopicDto;
import agenda.service.dto.SubscriberTopicDto;

import java.util.ArrayList;
//import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

@Component
@Transactional(rollbackFor = Exception.class)
public class TopicServiceImpl implements TopicService {

  private static final Logger LOG = LoggerFactory.getLogger(TopicServiceImpl.class);

  @Autowired
  private UuidProviderImpl uuidProvider;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private StatusRepository statusRepository;

  @Autowired
  private DtoMapper mapper;

  @Override
  public void updateTopic(String uuid, String login, String shortTopicDescription, String longTopicDescription) {
    Topic topic = topicRepository.getOne(uuid);
    User user = userRepository.getOne(login);
    UserAuthorizer userAuthorizer = new UserAuthorizer();

    if (!userAuthorizer.isUserCreator(topic, user)) {
      LOG.warn("Unberechtigte Änderung eines Topics durch {}", login);
    }

    if (shortTopicDescription.length() == 0 || longTopicDescription.length() == 0) {
      LOG.warn("Versuch, leere Kurz- oder Langbeschreibung für Topic {} zu übergeben.", uuid);
      throw new IllegalArgumentException("Sie dürfen keine leere Beschreibung abgeben.");
    }
    if (shortTopicDescription.length() < 16) {
      LOG.warn("Versuch, Kurzbeschreibung für Topic {} mit weniger als 16 Zeichen zu übergeben", uuid);
      throw new IllegalArgumentException("Ihre Kurbeschreibung muss mind. 16 Zeichen enthalten.");
    }
    if (shortTopicDescription.length() > 50) {
      LOG.warn("Versuch, Kurzbeschreibung für Topic {} mit mehr als 50 Zeichen zu übergeben", uuid);
      throw new IllegalArgumentException("Ihre Kurzbeschreibung darf nicht mehr als 50 Zeichen enthalten.");
    }
    if (longTopicDescription.length() < 24) {
      LOG.warn("Versuch, Langbeschreibung für Topic {} mit weniger als 24 Zeichen zu übergeben", uuid);
      throw new IllegalArgumentException("Ihre Langbeschreibung muss mind. 24 Zeichen enthalten");
    }
    if (longTopicDescription.length() > 300) {
      LOG.warn("Versuch, Langbeschreibung für Topic {} mit mehr als 300 Zeichen zu übergeben", uuid);
      throw new IllegalArgumentException("Ihre Langbeschreibung darf nicht mehr als 300 Zeichen enthalten.");
    }

    topic.setShortDescription(shortTopicDescription);
    topic.setLongDescription(longTopicDescription);
    LOG.info("Topic-Kurzbeschreibung für Topic {} geändert", shortTopicDescription);
    LOG.debug("Parameter für Topic-Kurbeschreibung-Änderung: {}, {}, {}", uuid, shortTopicDescription, login);
    LOG.info("Topic-Langbeschreibung für Topic {} geändert", longTopicDescription);
    LOG.debug("Parameter für Topic-Langbeschreibung-Änderung: {}, {}, {}", uuid, longTopicDescription, login);
  }

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public String createTopic(String title, String login, String shortTopicDescription, String longTopicDescription) {
    LOG.info("Erstelle Topic: \"{}\".", title);
    LOG.debug("Erstelle Topic mit Parametern: {}, {}", title, login);
    LOG.debug("Erstellte Topic hat Kurzbeschreibung:\n" + shortTopicDescription);
    LOG.debug("Erstellte Topic hat Langbeschreibung:\n" + longTopicDescription);

    // Titelcheck konform mit Topic.java (aus 'persistence') notNull & min. 10
    // Zeichen
    // sowie unter 60 Zeichen. Validierung Topic & Task aus Meilenstein2.
    if (title.length() < 10) {
      LOG.debug("Titel nicht in Ordnung", title);
      throw new ValidationException("Titel muss mind. 10 Zeichen haben.");
    }
    if (title.length() > 60) {
      LOG.debug("Titel nicht in Ordnung", title);
      throw new ValidationException("Titel darf max. 60 Zeichen haben.");
    }

    if (shortTopicDescription.length() < 16) {
      LOG.debug("ShortDescription '{}' für Topic zu kurz", shortTopicDescription);
      throw new ValidationException("Kurzbeschreibung muss mind. 16 Zeichen haben.");
    }
    if (longTopicDescription.length() < 24) {
      LOG.debug("LongDescription '{}' für Topic zu kurz", longTopicDescription);
      throw new ValidationException("Langbeschreibung muss mind. 16 Zeichen haben.");
    }

    String uuid = uuidProvider.getRandomUuid();
    User creator = userRepository.findById(login).get();
    Topic topic = new Topic(uuid, title, creator, shortTopicDescription, longTopicDescription);
    topicRepository.save(topic);
    return uuid;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void deleteTopic(String topicUuid, String login) {

    Topic topic = topicRepository.getOne(topicUuid);
    User user = userRepository.getOne(login);
    UserAuthorizer userAuthorizer = new UserAuthorizer();

    if (!userAuthorizer.isUserCreator(topic, user)) {
      LOG.warn("Unberechtiges Löschen eines Topics durch {}", login);
      throw new AccessDeniedException("Nur Topic-Eigner dürfen ihre Topics löschen.");
    }
    if (topic.getSubscriber().size() > 0) {
      LOG.warn("Löschung des abonnierten Topics {} verweigert.", topicUuid);
      throw new AccessDeniedException("Topics mit Abonennten dürfen nicht gelöscht werden.");
    }
    topicRepository.delete(topic);
    LOG.info("Topic '{}' von {} wurde gelöscht.", topicUuid, login);
    LOG.debug("Topic-Löschung mit Parametern: {}, {}", topicUuid, login);
  }

  @Override
  @PreAuthorize("#login==authentication.name")
  public List<OwnerTopicDto> getManagedTopics(String login, String search) {
    User creator = userRepository.findById(login).get();
    List<Topic> managedTopics = topicRepository.findByCreatorOrderByTitleAsc(creator);
    List<OwnerTopicDto> result = new ArrayList<>();
    for (Topic topic : managedTopics) {
      result.add(mapper.createManagedDto(topic));
    }
    result.removeIf(topic -> !topic.getTitle().toLowerCase().contains(search.toLowerCase())
        && !topic.getShortTopicDescription().toLowerCase().contains(search.toLowerCase())
        && !topic.getCreator().getLogin().contains(search.toLowerCase()));
    LOG.info("Eigene Topics nach Suchkriterium '{}' geholt", search);
    LOG.debug("Eingesetzte Parameter: {}, {}", login, search);

    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTopicDto getManagedTopic(String topicUuid, String login) {
    Topic topic = topicRepository.getOne(topicUuid);
    return mapper.createManagedDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTopicDto getTopic(String uuid, String login) {
    Topic topic = topicRepository.getOne(uuid);
    return mapper.createDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void subscribe(String topicUuid, String login) {
    LOG.info("Abonniere Topic: {}.", topicUuid);
    LOG.debug("Abonniere Topic mit Parametern: {}, {}", topicUuid, login);
    Topic topic = topicRepository.getOne(topicUuid);
    User user = userRepository.getOne(login);

    if (topic.getSubscriber().contains(user)) {
      LOG.warn("Zugriff auf bereits abonnierten Topic {}", topic.getUuid());
      LOG.warn("Zugriff über Registrierseite auf bereits abonnierten Topic durch {}", login);
      throw new IllegalArgumentException("Sie haben dieses Topic bereits abonniert!");
    }
    if (topic.getCreator().equals(user)) {
      LOG.warn("Zugriff auf selbst erstellten Topic {}", topic.getUuid());
      LOG.warn("Zugriff über Registrierseite auf selbst erstellten Topic durch {}", login);
      throw new IllegalArgumentException("Sie können Ihr eigenes Topic nicht abonnieren!");
    }

    topic.register(user);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void unsubscribe(String topicUuid, String login) {
    LOG.info("Abonniere Topic: {}.", topicUuid);
    LOG.debug("Abonniere Topic mit Parametern: {}, {}", topicUuid, login);
    Topic topic = topicRepository.getOne(topicUuid);
    User user = userRepository.getOne(login);

    if (!topic.getSubscriber().contains(user)) {
      LOG.warn("De-Abonnieren eines nicht-abonnierten Topics {}", topic.getUuid());
      LOG.warn("De-Abonnieren durch {}", login);
      throw new IllegalArgumentException("Sie haben dieses Topic bisher nicht abonniert!");
    }
    if (topic.getCreator().equals(user)) {
      LOG.warn("De-Abonnieren eines eigenen Topics {}", topic.getUuid());
      LOG.warn("De-Abonnieren durch {}", login);
      throw new IllegalArgumentException("Sie können Ihr eigenes Topic nicht de-abonnieren!");
    }

    topic.removeUser(user);

    for (Task task : topic.getTasks()) {
      if (statusRepository.findByUserAndTask(user, task) != null) {
        statusRepository.delete(statusRepository.findByUserAndTask(user, task));
        LOG.info("Status von {} mit De-Abonnieren gelöscht", user);
      }
    }
    LOG.info("De-Abonnieren des Topics {}", topic.getUuid());
    LOG.info("De-Abonnieren durch {}", user);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTopicDto> getSubscriptions(String login, String search) {
    User subscriber = userRepository.findById(login).get();

    List<SubscriberTopicDto> result = new ArrayList<>();
    for (Topic topic : topicRepository.findAllBySubscriberOrderByTitleAsc(subscriber)) {
      result.add(mapper.createDto(topic));
    }
    result.removeIf(topic -> !topic.getTitle().toLowerCase().contains(search.toLowerCase())
        && !topic.getShortTopicDescription().toLowerCase().contains(search.toLowerCase())
        && !topic.getCreator().getLogin().contains(search.toLowerCase()));
    LOG.info("Abonnierte Topics nach Suchkriterium '{}' geholt", search);
    LOG.debug("Eingesetzte Parameter: {}, {}", login, search);
    return result;
  }

  @Override
  public String getTopicUuid(String key, String login) {
    LOG.info("Uuid auflösen für Key {}", key);
    if (key.length() < 8) {
      throw new ValidationException("Abo-Code ist zu kurz!");
    }

    Topic topic = topicRepository.findByUuidEndingWith(key);
    if (topic == null) {
      LOG.warn("Zugriff auf nicht existierenden Topic");
      throw new ValidationException("Kein Topic mit diesem Schlüssel gefunden!");
    }
    if (topic.getSubscriber().contains(userRepository.getOne(login))) {
      LOG.warn("Zugriff auf bereits abonnierten Topic {}", topic.getUuid());
      LOG.warn("Zugriff über Indexseite auf bereits abonnierten Topic durch {}", login);
      throw new IllegalArgumentException("Sie haben dieses Topic bereits abonniert!");
    }
    if (topic.getCreator().getLogin().equals(login)) {
      LOG.warn("Zugriff auf selbst erstellten Topic {}", topic.getUuid());
      LOG.warn("Zugriff über Indexseite auf selbst erstellten Topic durch {}", login);
      throw new IllegalArgumentException("Sie können Ihr eigenes Topic nicht abonnieren!");
    }

    return topic.getUuid();
  }

}
