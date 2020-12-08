package agenda.service;

import agenda.service.dto.OwnerTopicDto;
import agenda.service.dto.SubscriberTopicDto;
import java.util.List;

/**
 * Serviceklasse für Verarbeitung von Topics.
 * 
 * @author Pavel St (paffen)
 */
public interface TopicService {

  /**
   *
   * Soll Topicbeschreibungen updaten
   *
   * @param topicUuid
   * @param login
   * @param shortTopicDescription
   * @param longTopicDescription
   */
  public void updateTopic(String topicUuid, String login, String shortTopicDescription, String longTopicDescription);

  /**
   * Erstellt ein neues Topic.
   */
  String createTopic(String title, String login, String shortTopicDescription, String longTopicDescription);

  /**
   * Loescht einen Topic.
   */
  void deleteTopic(String topicUuid, String login);

  /**
   * Zugriff auf ein eigenes Topic.
   */
  OwnerTopicDto getManagedTopic(String topicUuid, String login);

  /**
   * Zugriff auf alle eigenen Topics.
   */
  List<OwnerTopicDto> getManagedTopics(String login, String search);

  /**
   * Zugriff auf ein abonniertes Topic.
   */
  SubscriberTopicDto getTopic(String topicUuid, String login);

  /**
   * Zugriff auf alle abonnierten Topics.
   */
  List<SubscriberTopicDto> getSubscriptions(String login, String search);

  /**
   * Abonnieren eines Topics.
   */
  void subscribe(String topicUuid, String login);

  /**
   * De-Abonnieren eines Topics.
   */
  void unsubscribe(String topicUuid, String login);

  /**
   * Liefert die Topic-Uuid anhand des Abo-Codes für noch nicht abonnierte Topics.
   */
  String getTopicUuid(String key, String login);
}
