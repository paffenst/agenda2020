package agenda.service.dto;

/**
 * Transferobjekt für Tasks mit StatusInformationen, die spezifisch für
 * Abonnenten des Topics sindsind. Transferobjekte sind Schnittstellenobjekte
 * der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen
 * können.
 * 
 * @see TaskDto
 * 
 * @author Pavel St. (paffen)
 */
public class SubscriberTaskDto extends TaskDto {

  private StatusDto status;

  public SubscriberTaskDto(Long taskId, String title, SubscriberTopicDto topicDto, StatusDto status,
      String shortDescription, String longDescription) {
    super(taskId, title, topicDto, shortDescription, longDescription);
    this.status = status;
  }

  public StatusDto getStatus() {
    return status;
  }

  public void setStatus(StatusDto status) {
    this.status = status;
  }
}
