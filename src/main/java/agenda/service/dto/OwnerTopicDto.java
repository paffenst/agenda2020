package agenda.service.dto;

/**
 * Transferobjekt für Topics mit Metadaten, die nur für Verwalter eines Topics
 * (d.h. Eigentümer des Topics) sichtbar sind. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so
 * dass Änderungen an den Transferobjekten die Überprüfungen der Geschäftslogik
 * nicht umgehen können.
 * 
 * @see SubscriberTopicDto
 * 
 * @author Pavel St. (paffen)
 */
public class OwnerTopicDto extends SubscriberTopicDto {
  int subscriberCount;

  public OwnerTopicDto(String uuid, UserDisplayDto user, String title, String shortTopicDescription,
      String longTopicDescription, int subscriberCount) {
    super(uuid, user, title, shortTopicDescription, longTopicDescription);
    this.subscriberCount = subscriberCount;
  }

  // Eingabe des Abo-Codes, von Thymeleaf bearbeitet
  public String getKey() {
    return getUuid().substring(28);
  }

  public int getSubscriberCount() {
    return subscriberCount;
  }

  public void setSubscriberCount(int subscriberCount) {
    this.subscriberCount = subscriberCount;
  }
}
