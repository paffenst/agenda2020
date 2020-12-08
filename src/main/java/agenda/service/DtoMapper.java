package agenda.service;

import agenda.common.StatusEnum;
import agenda.persistence.*;
import agenda.service.dto.OwnerTaskDto;
import agenda.service.dto.OwnerTopicDto;
import agenda.service.dto.StatusDto;
import agenda.service.dto.SubscriberTaskDto;
import agenda.service.dto.SubscriberTopicDto;
import agenda.service.dto.UserDisplayDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfskomponente zum Erstellen der Transferobjekte aus den Entities. Für diese
 * Aufgabe gibt es viele Frameworks, die aber zum Teil recht schwer zu verstehen
 * sind. Da das Mapping sonst zu viel redundantem Code führt, ist die
 * Zusammenführung aber notwendig.
 *
 * @author Pavel St. (paffen)
 */
@Component
public class DtoMapper {

  private static final Logger LOG = LoggerFactory.getLogger(DtoMapper.class);

  @Autowired
  private ModelMapper mapper;

  @Autowired
  private TopicRepository topicRepository;

  /**
   * Erstellt ein {@link UserDisplayDto} aus einem {@link User}.
   */
  public UserDisplayDto createDto(User user) {
    LOG.info("Erstelle UserDisplayDTO mit {}", user);
    LOG.debug("Erstelle UserDisplayDTO mit Parameter: {}", user);
    UserDisplayDto dto = mapper.map(user, UserDisplayDto.class);
    dto.setTopicCount(topicRepository.countByCreator(user));
    dto.setSubscriptionCount(user.getSubscriptions().size());
    return dto;
  }

  /**
   * Erstellt ein {@link SubscriberTopicDto} aus einem {@link Topic}.
   */
  public SubscriberTopicDto createDto(Topic topic) {
    LOG.info("Erstelle TopicDTO mit {}", topic);
    LOG.debug("Erstelle TopicDTO mit Parameter: {}", topic);
    UserDisplayDto creatorDto = createDto(topic.getCreator());
    SubscriberTopicDto topicDto = new SubscriberTopicDto(topic.getUuid(), creatorDto, topic.getTitle(),
        topic.getShortTopicDescription(), topic.getLongTopicDescription());
    List<UserDisplayDto> subscribers = new ArrayList<UserDisplayDto>();
    for (User user : topic.getSubscriber()) {
      subscribers.add(createDto(user));
    }
    topicDto.setSubscribers(subscribers);
    return topicDto;
  }

  /**
   * Erstellt ein {@link StatusDto} aus einem {@link Status}.
   */
  public StatusDto createDto(Status status) {
    LOG.info("Erstelle StatusDTO mit {}", status);
    LOG.debug("Erstelle StatusDTO mit Parameter: {}, {}", status, status.getComment());
    return new StatusDto(status.getStatus(), createDto(status.getUser()), status.getComment());
  }

  /**
   * Erstellt ein {@link SubscriberTaskDto} aus einem {@link Task} und einem
   * {@link Status}.
   */
  public SubscriberTaskDto createReadDto(Task task, Status status) {
    LOG.info("Erstelle SubscriberTaskDTO \"{}\"", task);
    LOG.debug("Erstelle SubscriberTaskDTO mit Parametern: {}, {}", task, status);
    Topic topic = task.getTopic();
    SubscriberTopicDto topicDto = createDto(topic);
    return new SubscriberTaskDto(task.getId(), task.getTitle(), topicDto, createDto(status), task.getShortDescription(),
        task.getLongDescription());
  }

  /**
   * Erstellt ein {@link OwnerTopicDto} aus einem {@link Topic}.
   */
  public OwnerTopicDto createManagedDto(Topic topic) {
    LOG.info("Erstelle OwnerTopicDto \"{}\"", topic);
    LOG.debug("Erstelle OwnerTopicDto mit Parameter: {}", topic);
    return new OwnerTopicDto(topic.getUuid(), createDto(topic.getCreator()), topic.getTitle(),
        topic.getShortTopicDescription(), topic.getLongTopicDescription(), topic.getSubscriber().size());
  }

  public OwnerTaskDto createManagedDto(Task task) {
    LOG.info("Erstelle OwnerTaskDto \"{}\"", task);
    LOG.debug("Erstelle OwnerTaskDto mit Parameter: {}", task);
    OwnerTaskDto ownerTaskDto = new OwnerTaskDto(task.getId(), task.getTitle(), createDto(task.getTopic()),
        task.getShortDescription(), task.getLongDescription());
    int erledigtStatusCount = 0;
    List<StatusDto> statusComment = new ArrayList<StatusDto>();
    for (Status status : task.getStatus())
      if (status.getComment() != "") {
        statusComment.add(createDto(status));
        if (status.getStatus().equals(StatusEnum.FERTIG)) {
          erledigtStatusCount++;
        }
        ownerTaskDto.setErledigtStatusCount(erledigtStatusCount);
        ownerTaskDto.setStatusComment(statusComment);
      }
    return ownerTaskDto;
  }
}
