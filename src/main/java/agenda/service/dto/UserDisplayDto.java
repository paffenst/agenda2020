package agenda.service.dto;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Anwendern.
 * Transferobjekte sind Schnittstellenobjekte der Geschäftslogik; Sie sind nicht
 * Teil des Modells, so dass Änderungen an den Transferobjekten die
 * Überprüfungen der Geschäftslogik nicht umgehen können.
 *
 * @author Pavel St. (paffen)
 */
public class UserDisplayDto {

  private String login = "";

  private int topicCount;

  private int subscriptionCount;

  private String name;

  public String getDisplayName() {
    return name;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public int getTopicCount() {
    return topicCount;
  }

  public int getSubscriptionCount() {
    return subscriptionCount;
  }

  public void setTopicCount(int topicCount) {
    this.topicCount = topicCount;
  }

  public void setSubscriptionCount(int subscriptionCount) {
    this.subscriptionCount = subscriptionCount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
