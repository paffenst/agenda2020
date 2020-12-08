package agenda.mvc;

import agenda.service.TaskService;
import agenda.service.TopicService;
import agenda.service.dto.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import javax.persistence.EntityNotFoundException;
//import javax.validation.constraints.Null;

@Controller
public class TaskController extends AbstractController {

  @Autowired
  private TopicService topicService;

  @Autowired
  private TaskService taskService;

  /**
   * Ertellt das Formular zur Erfassung eines neuen Tasks.
   */
  @GetMapping("/topics/{uuid}/createTask")
  public String getTaskCreationView(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("newTask", new TaskDto(null, "", topic, null, null));
    return "task-creation";
  }

  /**
   * Verarbeitet die Erstellung eines Tasks.
   */
  @PostMapping("/topics/{uuid}/createTask")
  public String handleTaskCreation(Model model, Authentication auth, @PathVariable("uuid") String uuid,
      @ModelAttribute("newTask") TaskDto newTask, RedirectAttributes redirectAttributes) {
    try {
      taskService.createTask(uuid, newTask.getTitle(), auth.getName(), newTask.getShortDescription(),
          newTask.getLongDescription());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + uuid + "/createTask";
    }
    redirectAttributes.addFlashAttribute("success", "Task \"" + newTask.getTitle() + "\" erstellt.");
    return "redirect:/topics/" + uuid + "/manage";
  }

  /**
   * Bearbeitet die Aufforderung auf das Loeschen eines Tasks.
   */
  @PostMapping("tasks/{id}/delete")
  public String handleDeletion(Authentication auth, @PathVariable("id") Long id, RedirectAttributes redirectAttrib) {
    TaskDto task = taskService.getManagedTask(id, auth.getName()); // Benoetigt, um UUID des Topics zu holen.

    try {
      taskService.deleteTask(id, auth.getName());
    } catch (Exception e) {
      redirectAttrib.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + task.getTopic().getUuid() + "/manage";
    }
    redirectAttrib.addFlashAttribute("success", "Task wurde vom Topic gelöscht.");
    return "redirect:/topics/" + task.getTopic().getUuid() + "/manage";
  }

  /**
   * Erstellt die Taskansicht für Abonnenten.
   */
  @GetMapping("tasks/{id}")
  public String getSubscriberTaskView(Model model, Authentication auth, @PathVariable("id") Long id) {
    TaskDto task = taskService.getTask(id, auth.getName());
    StatusDto status = taskService.getTask(id, auth.getName()).getStatus();
    model.addAttribute("task", task);
    model.addAttribute("status", status);
    return "task";
  }

  /**
   * Erstellt die Taskansicht für den Verwalter/Ersteller eines Topics.
   */
  @GetMapping("tasks/{id}/manage")
  public String getManagerTaskView(Model model, Authentication auth, @PathVariable("id") Long id) {
    OwnerTaskDto task = taskService.getManagedTask(id, auth.getName());
    List<StatusDto> statusComment = task.getStatusComment();
    model.addAttribute("task", task);
    model.addAttribute("statusComment", statusComment);
    return "task-management";
  }

  /**
   * Verarbeitet die Aktualisierung eines Tasks.
   */
  @PostMapping("tasks/{id}/manage")
  public String handleDescriptionUpdate(@PathVariable("id") Long id, Authentication auth,
      RedirectAttributes redirectAttributes, @ModelAttribute("task") TaskDto task,
      @RequestHeader(value = "referer", required = true) String referer) {
    try {
      taskService.updateTask(id, auth.getName(), task.getShortDescription(), task.getLongDescription());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success", "Task-Beschreibung geändert.");
    return "redirect:" + referer;
  }

  /**
   * Verarbeitet das Hinzufügen bzw. die Änderung eines Kommentars zu einem Task.
   */
  @PostMapping("tasks/{id}")
  public String handleCommentUpdate(@PathVariable("id") Long id, Authentication auth,
      @ModelAttribute("task") TaskDto task, @ModelAttribute("status") StatusDto status,
      RedirectAttributes redirectAttributes, @RequestHeader(value = "referer", required = true) String referer) {
    try {
      taskService.addCommentToTask(id, auth.getName(), status.getComment());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success", "Kommentar wurde gespeichert!");
    return "redirect:" + referer;
  }

  /**
   * Verarbeitet die Markierung eines Tasks als "Done".
   */
  @PostMapping("tasks/{id}/check")
  public String handleTaskChecking(Authentication auth, @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer) {
    taskService.checkTask(id, auth.getName());
    return "redirect:" + referer;
  }

  /**
   * Verarbeitet die Markierung eines Tasks als "Done".
   */
  @PostMapping("tasks/{id}/reset")
  public String handleTaskReset(Authentication auth, @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer) {
    taskService.resetTask(id, auth.getName());
    return "redirect:" + referer;
  }

  /**
   * Erstellt die Übersicht aller Tasks abonnierter Topics für einen Anwender.
   */
  @GetMapping("tasks")
  public String getSubscriberTaskListView(Model model, Authentication auth) {
    List<SubscriberTaskDto> tasks = taskService.getSubscribedTasks(auth.getName());
    model.addAttribute("tasks", tasks);
    return "task-listview";
  }
}
