package agenda.service.dto;

import java.util.List;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Topics. Transferobjekte
 * sind Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des
 * Modells, so dass Änderungen an den Transferobjekten die Überprüfungen der
 * Geschäftslogik nicht umgehen können.
 * 
 * @see OwnerTopicDto
 * 
 * @author Pavel St. (paffen)
 */
public class SubscriberTopicDto {
  private final String uuid;
  private final UserDisplayDto creator;
  private String title;
  private final String shortTopicDescription;
  private final String longTopicDescription;
  private List<UserDisplayDto> subscribers;

  /**
   * Konstruktor.
   */
  public SubscriberTopicDto(String uuid, UserDisplayDto creator, String title, String shortTopicDescription,
      String longTopicDescription) {
    this.uuid = uuid;
    this.creator = creator;
    this.title = title;
    this.shortTopicDescription = shortTopicDescription;
    this.longTopicDescription = longTopicDescription;

  }

  public String getUuid() {
    return uuid;
  }

  public UserDisplayDto getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getShortTopicDescription() {
    return shortTopicDescription;
  }

  public String getLongTopicDescription() {
    return longTopicDescription;
  }

  public void setSubscribers(List<UserDisplayDto> subscribers) {
    this.subscribers = subscribers;
  }

  public List<UserDisplayDto> getSubscribers() {
    return subscribers;
  }

}
