package agenda.service;

import agenda.service.dto.OwnerTaskDto;
import agenda.service.dto.SubscriberTaskDto;
import java.util.List;

/**
 * Serviceklasse für Verarbeitung von Tasks.
 * 
 * @author Pavel St. (paffen)
 */
public interface TaskService {

  /**
   * Erstellt einen neuen Task.
   */
  Long createTask(String topicUuid, String title, String login, String shortDescription, String longDescription);

  /**
   * Aendert einen bestehenden Task.
   */
  void updateTask(Long id, String login, String shortDescription, String longDescription);

  /**
   * Loescht einen Task
   */
  void deleteTask(Long id, String login);

  /**
   * Setzt den Status eines Task zurück.
   */
  void resetTask(Long id, String name);

  /**
   * Zugriff auf einen Task (priviligierte Sicht für Ersteller des Topics).
   */
  OwnerTaskDto getManagedTask(Long taskId, String login);

  /**
   * Zugriff auf alle Tasks eines eigenen Topics.
   */
  List<OwnerTaskDto> getManagedTasks(String topicUuid, String login);

  /**
   * Zugriff auf einen Task (Abonnentensicht).
   */
  SubscriberTaskDto getTask(Long taskId, String login);

  /**
   * Zugriff auf alle Tasks abonnierter Topics.
   */
  List<SubscriberTaskDto> getSubscribedTasks(String login);

  /**
   * Zugriff auf alle Tasks eines abonnierten Topics.
   */
  List<SubscriberTaskDto> getTasksForTopic(String topicUuid, String login);

  /**
   * Markiert einen Task für einen Abonnenten als "done".
   */
  void checkTask(Long taskId, String login);

  /**
   * Fügt einem Task einen Kommentar eines Abonnentens hinzu.
   */
  void addCommentToTask(Long taskId, String login, String comment);
}
