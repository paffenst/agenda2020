package agenda.service;

import agenda.common.StatusEnum;
import agenda.persistence.Status;
import agenda.persistence.StatusRepository;
import agenda.persistence.Task;
import agenda.persistence.TaskRepository;
import agenda.persistence.Topic;
import agenda.persistence.TopicRepository;
import agenda.persistence.User;
import agenda.persistence.UserRepository;
import agenda.service.dto.OwnerTaskDto;
import agenda.service.dto.SubscriberTaskDto;

import java.util.*;

import org.apache.commons.collections4.SetUtils;
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
public class TaskServiceImpl implements TaskService {

  private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StatusRepository statusRepository;

  @Autowired
  private DtoMapper mapper;

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public Long createTask(String uuid, String titel, String login, String shortDescription, String longDescription) {
    LOG.info("Erstelle Task \"{}\"", titel);
    LOG.debug("Erstelle Task mit Parametern: {}, \"{}\", {}", uuid, titel, login);
    LOG.debug("Erstellter Task hat Kurzbeschreibung:\n" + shortDescription);
    LOG.debug("Erstellter Task hat Langbeschreibung:\n + " + longDescription);

    if (titel.length() < 8) {
      LOG.debug("Fehlerhafter Titel", titel);
      throw new ValidationException("Titel muss mindestens 8 Zeichen lang sein.");
    }
    if (titel.length() > 32) {
      LOG.debug("Fehlerhafter Titel", titel);
      throw new ValidationException("Titel darf höchstens 32 Zeichen lang sein.");
    }
    if (shortDescription.length() < 8) {
      LOG.debug("ShortDescription '{}' für Task zu kurz", shortDescription);
      throw new ValidationException("Kurzbeschreibung für Task muss mind. 8 Zeichen enthalten");
    }
    if (shortDescription.length() < 16) {
      LOG.debug("LongDescription '{}' für Task zu kurz", longDescription);
      throw new ValidationException("Langbeschreibung für Task muss mind. 16 Zeichen enthalten");
    }

    User user = userRepository.getOne(login);
    Topic topic = topicRepository.findById(uuid).get();
    if (!user.equals(topic.getCreator())) {
      LOG.warn("Unberechtigte Erstellung eines Tasks durch {}", login);
      throw new AccessDeniedException("Unberechtigter Zugriff auf das Topic.");
    }
    Task task = new Task(topic, titel, shortDescription, longDescription);
    taskRepository.save(task);
    return task.getId();
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void updateTask(Long id, String login, String shortDescription, String longDescription) {
    Task task = taskRepository.getOne(id);
    User user = userRepository.getOne(login);
    UserAuthorizer userAuthorizer = new UserAuthorizer();

    if (!userAuthorizer.isUserCreator(task, user)) {
      LOG.warn("Unberechtigte Änderung eines Tasks durch {}", login);
    }

    if (shortDescription.length() == 0 || longDescription.length() == 0) {
      LOG.warn("Versuch, leere Kurz- oder Langbeschreibung für Task '{}' zu übergeben.", id);
      throw new IllegalArgumentException("Sie dürfen keine leere Beschreibung abgeben.");
    }
    if (shortDescription.length() < 8) {
      LOG.warn("Versuch, Kurzbeschreibung für Task '{}' mit weniger als 8 Zeichen zu übergeben", id);
      throw new IllegalArgumentException("Ihre Kurbeschreibung muss mind. 8 Zeichen enthalten.");
    }
    if (shortDescription.length() > 50) {
      LOG.warn("Versuch, Kurzbeschreibung für Task '{}' mit mehr als 50 Zeichen zu übergeben", id);
      throw new IllegalArgumentException("Ihre Kurzbeschreibung darf nicht mehr als 50 Zeichen enthalten.");
    }
    if (longDescription.length() < 16) {
      LOG.warn("Versuch, Langbeschreibung für Task '{}' mit weniger als 16 Zeichen zu übergeben", id);
      throw new IllegalArgumentException("Ihre Langbeschreibung muss mind. 16 Zeichen enthalten");
    }
    if (longDescription.length() > 300) {
      LOG.warn("Versuch, Langbeschreibung für Task '{}' mit mehr als 300 Zeichen zu übergeben", id);
      throw new IllegalArgumentException("Ihre Langbeschreibung darf nicht mehr als 300 Zeichen enthalten.");
    }

    task.setShortDescription(shortDescription);
    task.setLongDescription(longDescription);

    LOG.info("Task-Kurzbeschreibung für Task '{}' gesetzt", shortDescription);
    LOG.debug("Parameter für Erstellung der Task-Kurbeschreibung: {}, {}, {}", id, shortDescription, login);
    LOG.info("Task-Langbeschreibung für Task '{}' gesetzt", longDescription);
    LOG.debug("Parameter für Erstellung der Task-Langbeschreibung: {}, {}, {}", id, longDescription, login);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void addCommentToTask(Long taskId, String login, String comment) {
    LOG.info("Kommentar '{}' wird an Task {} gesetzt", comment, taskId);
    LOG.warn("Kommentarsetzung mit Parametern: {}, {}, '{}'", taskId, login, comment);

    Task task = taskRepository.getOne(taskId);
    if (task == null) {
      LOG.warn("Kommentarsetzung auf einem nicht vorhandenem Task");
      throw new NoSuchElementException("Den zu kommentierten Task existiert nicht.");
    }

    User user = userRepository.getOne(login);
    if (user.equals(task.getTopic().getCreator())) {
      LOG.warn("Unberechtigte Kommentarsetzung eines Tasks durch {}", login);
      throw new ValidationException("Sie dürfen keine Kommentare zu ihren eigenen Tasks erstellen!");
    }

    Status status = statusRepository.findByUserAndTask(user, task);
    if (status == null) {
      status = getOrCreateStatus(taskId, login);
    }

    if (comment == "" || comment == null) {
      LOG.warn("Leerer Kommentar wurde übergeben");
      throw new IllegalArgumentException("Sie dürfen keinen leeren Kommentar abgeben!");
    }
    if (comment.length() < 3) {
      LOG.warn("Kommentar mit weniger als 3 Zeichen wurde übergeben: '{}'", comment);
      throw new IllegalArgumentException("Ihr Kommentar muss mindestens 3 Zeichen enthalten!");
    }
    if (comment.length() > 500) {
      LOG.warn("Kommentar mit mehr als 500 Zeichen wurde übergeben: '{}", comment);
      throw new IllegalArgumentException("Ihr Kommentar darf nicht mehr als 500 Zeichen enthalten!");
    }

    status.setComment(comment);
    LOG.info("Kommentar '{}' für Task {} erfolgreich gesetzt", comment, taskId);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void deleteTask(Long id, String login) {
    Task task = taskRepository.getOne(id);
    User user = userRepository.getOne(login);
    UserAuthorizer userAuthorizer = new UserAuthorizer();

    if (!userAuthorizer.isUserCreator(task, user)) {
      LOG.warn("Unberechtigtes Lösches eines Tasks durch {}", login);
      throw new AccessDeniedException("Unberechtigte Löschung eines Tasks.");
    }
    taskRepository.delete(task);
    LOG.info("Task {} wurde gelöscht", id);
    LOG.debug("Task-Löschung mit Parametern {}, {}", id, login);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTaskDto getTask(Long taskId, String login) {
    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User user = userRepository.getOne(login);
    if (!(topic.getCreator().equals(user) || topic.getSubscriber().contains(user))) {
      LOG.warn("Anwendungsfehler oder unerlaubter Zugriff von {} auf Task {} abgewehrt", login, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    Status status = getOrCreateStatus(taskId, login);
    return mapper.createReadDto(task, status);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTaskDto getManagedTask(Long taskId, String login) {
    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User createdBy = topic.getCreator();

    if (!login.equals(createdBy.getLogin())) {
      LOG.warn("Anwendungsfehler oder unerlaubter Zugriff vom Nicht-Ersteller {} auf Task {} " + "abgewehrt", login,
          taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }

    return mapper.createManagedDto(task);
  }

  /**
   * Liste fuer die Ausgabe der Tasks eines Topics. Aufgerufen von der Seite, die
   * alle Tasks zu einem Topic ausgibt.
   */
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getSubscribedTasks(String login) {
    User user = userRepository.getOne(login);
    Collection<Topic> topics = user.getSubscriptions();
    return extracted(user, topics);
  }

  /**
   * Liste fuer die Ausgabe der Tasks von allen Topics. Aufgerufen von der Seite,
   * die alle Tasks ausgibt.
   */
  private List<SubscriberTaskDto> extracted(User user, Collection<Topic> topics) {
    LOG.info("Erstelle Liste fuer {}", user);
    LOG.debug("Erstelle Liste mit Parametern: {}, {}", user, topics);

    Collection<Status> status = user.getStatus();
    Map<Task, Status> statusForTask = new HashMap<>();
    for (Status currentStatus : status) {
      statusForTask.put(currentStatus.getTask(), currentStatus);
    }

    // Fehlende Status hinzufuegen.
    for (Topic t : topics) {
      for (Task task : t.getTasks()) {
        if (statusForTask.get(task) == null) {
          Status createdStatus = getOrCreateStatus(task.getId(), user.getLogin());
          statusForTask.put(task, createdStatus);
        }
      }
    }

    // Tasks abhaengig vom Status in separaten Listen hinzufuegen. Danach beide
    // Listen sortieren
    // und dann zusammenfuehren.
    List<SubscriberTaskDto> result = new ArrayList<>();
    List<SubscriberTaskDto> completedTasks = new ArrayList<>();

    for (Topic t : topics) {
      for (Task task : taskRepository.findAllByOrderByTitleAsc()) {
        if (t.getTasks().contains(task)) {
          if (statusForTask.get(task) != null && statusForTask.get(task).getStatus() == StatusEnum.FERTIG) {
            completedTasks.add(mapper.createReadDto(task, statusForTask.get(task)));
          } else {
            Status createdStatus = getOrCreateStatus(task.getId(), user.getLogin());
            statusForTask.put(task, createdStatus);
            result.add(mapper.createReadDto(task, createdStatus));
          }
        }
      }
    }

    Collections.sort(result, new TaskComparator<>());
    Collections.sort(completedTasks, new TaskComparator<>());
    result.addAll(completedTasks);
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getTasksForTopic(String uuid, String login) {
    User user = userRepository.getOne(login);
    Topic topic = topicRepository.getOne(uuid);
    return extracted(user, SetUtils.hashSet(topic));
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void checkTask(Long taskId, String login) {
    LOG.info("Checke Task mit ID={}", taskId);
    LOG.debug("Checke Task mit Parametern: {}, {}", taskId, login);
    Status status = getOrCreateStatus(taskId, login);
    status.setStatus(StatusEnum.FERTIG);
    LOG.info("Status von Task {} und Anwender {} gesetzt auf {}", status.getTask(), status.getUser(),
        status.getStatus());
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void resetTask(Long taskId, String login) {
    LOG.info("Zurücksetze Task mit ID={}", taskId);
    LOG.debug("Zurücksetze Task mit Parametern: {}, {}", taskId, login);
    Status status = getOrCreateStatus(taskId, login);
    status.setStatus(StatusEnum.NEU);
    LOG.info("Status von Task {} und Anwender {} gesetzt auf {}", status.getTask(), status.getUser(),
        status.getStatus());
  }

  /**
   * Liste fuer die Ausgabe der Tasks eines Topics. Aufgerufen von der Topic-Seite
   * des Erstellers.
   */
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<OwnerTaskDto> getManagedTasks(String uuid, String login) {
    LOG.info("Taskliste fuer {} erstellt", uuid);
    LOG.debug("Taskliste erstellt mit Parameter: {}, {}: ", uuid, login);

    List<OwnerTaskDto> result = new ArrayList<>();
    Topic topic = topicRepository.getOne(uuid);

    for (Task task : taskRepository.findAllByOrderByTitleAsc()) {
      if (topic.getTasks().contains(task)) {
        result.add(mapper.createManagedDto(task));
      }
    }
    return result;
  }

  private Status getOrCreateStatus(Long taskId, String login) {
    User user = userRepository.getOne(login);
    Task task = taskRepository.getOne(taskId);
    Status status = statusRepository.findByUserAndTask(user, task);
    if (status == null) {
      status = new Status(task, user);
      statusRepository.save(status);
    }
    return status;
  }
}
