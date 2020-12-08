package agenda.mvc;

import agenda.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller-Klasse für die Landing-Page. Controller reagieren auf Aufrufe von
 * URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und stellen Daten
 * zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 * 
 * @author Pavel St. (paffen)
 */

@Controller
public class IndexController extends AbstractController {

  @Autowired
  TopicService topicService;

  /**
   * Erstellt die Landing-Page.
   */
  @GetMapping("/")
  public String getIndexView(Model model, Authentication auth) {
    model.addAttribute("registration", new Registration());
    return "index";
  }

  /**
   * Bearbeitet die Eingabe des Abo-Codes in der Indexseite.
   */
  @PostMapping("/register")
  public String handleRegistrationKey(@ModelAttribute("registration") Registration registration,
      RedirectAttributes redirectAttributes, Authentication auth) {
    String uuid = "";
    String key = registration.getKey();

    try {
      uuid = topicService.getTopicUuid(key, auth.getName());
      redirectAttributes.addFlashAttribute("success", "Topic ist verfügbar!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/"; // Zurueck zur urspruenglichen Seite.
    }

    return "redirect:/topics/" + uuid + "/register";
  }
}
