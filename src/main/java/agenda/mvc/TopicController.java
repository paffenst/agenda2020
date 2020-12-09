package agenda.mvc;

import agenda.service.TaskService;
import agenda.service.TopicService;
import agenda.service.dto.OwnerTopicDto;
//import agenda.service.dto.SubscriberTaskDto;
import agenda.service.dto.SubscriberTopicDto;
import agenda.service.dto.UserDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller-Klasse für alle Interaktionen, die die Anzeige und Verwaltung von
 * Topics betrifft. Controller reagieren auf Aufrufe von URLs. Sie benennen ein
 * View-Template (Thymeleaf-Vorlage) und stellen Daten zusammen, die darin
 * dargestellt werden. Dafür verwenden Sie Methoden der Service-Schicht.
 *
 * @author Pavel St. (paffenst)
 */
@Controller
public class TopicController extends AbstractController {

  @Autowired
  private TopicService topicService;

  @Autowired
  private TaskService taskService;

  /**
   * Änderunt die TopicBezeichnungen
   *
   */

  @PostMapping("/topics/{uuid}/manage")
  public String handleUpdate(@PathVariable("uuid") String uuid, @ModelAttribute("topic") SubscriberTopicDto topic,
      @RequestHeader(value = "referer", required = true) String referer, Authentication auth,
      RedirectAttributes redirectAttributes) {
    try {
      topicService.updateTopic(uuid, auth.getName(), topic.getShortTopicDescription(), topic.getLongTopicDescription());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success",
        "Topicbeschreibung " + topic.getShortTopicDescription() + " geändert.");
    return "redirect:" + referer;
  }

  /**
   * Erstellt die Übersicht über alle Topics des Anwenders, d.h. selbst erzeugte
   * und abonnierte.
   */
  @GetMapping("/topics")
  public String getTopicListView(Model model, Authentication auth,
      @RequestParam(name = "search", required = false, defaultValue = "") String search) {
    model.addAttribute("managedTopics", topicService.getManagedTopics(auth.getName(), search));
    model.addAttribute("topics", topicService.getSubscriptions(auth.getName(), search));
    model.addAttribute("search", new Search());
    return "topic-listview";
  }

  /**
   * Erstellt das Formular zum Erstellen eines Topics.
   */
  @GetMapping("/topics/create")
  public String getTopicCreationView(Model model, Authentication auth) {
    model.addAttribute("newTopic", new SubscriberTopicDto(null, null, "", null, null));
    return "topic-creation";
  }

  /**
   * Nimmt den Formularinhalt vom Formular zum Erstellen eines Topics entgegen und
   * legt einen entsprechendes Topic an. Kommt es dabei zu einer Exception, wird
   * das Erzeugungsformular wieder angezeigt und eine Fehlermeldung eingeblendet.
   * Andernfalls wird auf die Übersicht der Topics weitergeleitet und das Anlegen
   * in einer Einblendung bestätigt.
   */
  @PostMapping("/topics")
  public String handleTopicCreation(Model model, Authentication auth,
      @ModelAttribute("newTopic") SubscriberTopicDto topic, RedirectAttributes redirectAttributes) {
    try {
      topicService.createTopic(topic.getTitle(), auth.getName(), topic.getShortTopicDescription(),
          topic.getLongTopicDescription());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/create";
    }
    redirectAttributes.addFlashAttribute("success", "Topic " + topic.getTitle() + " angelegt.");
    return "redirect:/topics";
  }

  /**
   * Erzeugt Anzeige eines Topics mit Informationen für den Ersteller.
   */
  @GetMapping("/topics/{uuid}/manage")
  public String createTopicManagementView(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("tasks", taskService.getManagedTasks(uuid, auth.getName()));
    return "topic-management";
  }

  /**
   * Erzeugt Anzeige für die Nachfrage beim Abonnieren eines Topics.
   */
  @GetMapping("/topics/{uuid}/register")
  public String getTaskRegistrationView(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    return "topic-registration";
  }

  /**
   * Nimmt das Abonnement (d.h. die Bestätigung auf die Nachfrage) entgegen und
   * erstellt ein Abonnement.
   */
  @PostMapping("/topics/{uuid}/register")
  public String handleTaskRegistration(Model model, Authentication auth, @PathVariable("uuid") String uuid,
      RedirectAttributes redirectAttributes) {
    try {
      topicService.subscribe(uuid, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/";
    }
    return "redirect:/topics/" + uuid;
  }

  /**
   * Bearbeitet die Aufforderung auf das Loeschen eines Topics.
   */
  @PostMapping("topics/{topicUuid}/delete")
  public String handleDeletion(Authentication auth, @PathVariable("topicUuid") String topicUuid,
      RedirectAttributes redirectAttrib) {
    try {
      topicService.deleteTopic(topicUuid, auth.getName());
    } catch (Exception e) {
      redirectAttrib.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics";
    }
    redirectAttrib.addFlashAttribute("success", "Topic wurde gelöscht.");
    return "redirect:/topics";
  }

  /**
   * Erstellt Übersicht eines Topics für einen Abonennten.
   */
  @GetMapping("/topics/{uuid}")
  public String createTopicView(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("tasks", taskService.getTasksForTopic(uuid, auth.getName()));
    return "topic";
  }

  /**
   * Öffnet die Sicht zur Abbestellung eines Topics.
   */
  @GetMapping("/topics/{uuid}/unsubscribe")
  public String getTopicUnsubscriptionView(Model model, Authentication auth, @PathVariable("uuid") String uuid,
      RedirectAttributes redirectAttributes) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    return "topic-unsubscription";
  }

  /**
   * Bearbeitet Anfrage auf Abmeldung von einem Topic.
   */
  @PostMapping("/topics/{uuid}/unsubscribe")
  public String handleTopicUnsubscription(Authentication auth, @PathVariable("uuid") String uuid,
      RedirectAttributes redirectAttributes) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    try {
      topicService.unsubscribe(uuid, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics";
    }
    redirectAttributes.addFlashAttribute("success", "Sie sind vom Topic abgemeldet!");
    return "redirect:/topics";
  }

  /**
   * Erstellt Übersicht über die Abonnenten eines Topics
   */
  @GetMapping("/topics/{uuid}/subscribers")
  public String createSubscribersView(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    List<UserDisplayDto> subscribers = topic.getSubscribers();
    model.addAttribute("subscribers", subscribers);
    return "topic-subscriber-listview";

  }

}
