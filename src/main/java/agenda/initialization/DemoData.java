package agenda.initialization;

import agenda.common.SecurityHelper;
import agenda.service.TaskService;
import agenda.service.TopicService;
import agenda.service.UserService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initialisierung von Demo-Daten. Diese Komponente erstellt beim Systemstart
 * Anwender, Topics, Abonnements usw., damit man die Anwendung mit allen
 * Features vorführen kann.
 *
 * @author Pavel St. (paffen)
 */
@Component
@Profile("demo")
public class DemoData {

        private static final String LOGIN_FINE = "fine";

        private static final String LOGIN_ERNIE = "ernie";

        private static final String LOGIN_BERT = "bert";

        private static final Logger LOG = LoggerFactory.getLogger(DemoData.class);

        @Autowired
        UserService anwenderService;

        @Autowired
        TopicService topicService;

        @Autowired
        TaskService taskService;

        /**
         * Erstellt die Demo-Daten.
         */
        @PostConstruct
        @SuppressWarnings("unused")
        public void addData() {
                SecurityHelper.escalate(); // admin rights
                LOG.debug("Erzeuge Demo-Daten.");

                anwenderService.legeAn(LOGIN_FINE, "Fine", "Fine1234!", false);
                anwenderService.legeAn(LOGIN_ERNIE, "Ernie", "Ernie1234!", false);
                anwenderService.legeAn(LOGIN_BERT, "Bert", "Bert1234!", false);

                String htmlKursUuid = topicService.createTopic("HTML für Anfänger", LOGIN_FINE,
                                "Grundlagen von HTML lernen.",
                                "HTML in 1 Stunde vollständig lernen - HTML5 Online Kurs " + " Das wirst du lernen: "
                                                + " 1. Leeres HTML-Templete erstellen " + " 2. Link erstellen "
                                                + " 3. Eine HTML-Website  schreiben. ");

                topicService.subscribe(htmlKursUuid, LOGIN_ERNIE);
                topicService.subscribe(htmlKursUuid, LOGIN_BERT);
                Long linkErstellenTask = taskService.createTask(htmlKursUuid, "Link erstellen", LOGIN_FINE,
                                "Links erstellen einfach gemacht!",
                                "Für diesen Task erhalten Sie ein " + "15-minütiges Video und 30 min Bearbeitugnszeit");
                taskService.checkTask(linkErstellenTask, LOGIN_ERNIE);
                taskService.createTask(htmlKursUuid, "Leeres HTML-Template erstellen", LOGIN_FINE,
                                "Perfekt für den Einstieg in HTML!",
                                "Für diesen Task erhalten Sie ein " + "15-minütiges Video und 20 min Bearbeitungszeit");

                String cssKursUuid = topicService.createTopic("CSS für Fortgeschrittene", LOGIN_FINE,
                                "Webseiten eleganter gestyled.",
                                "In diesem Online-Kurs lernen wir einige hilfreiche Tricks , wie wir CSS Formatierungen nutzen können, um eine Webseite optisch zu beeinflussen.");

                String erniesKursUuid = topicService.createTopic("Ernies Backkurs", LOGIN_ERNIE,
                                "Backkurse für Kekse und Cupcakes",
                                "In Ernies Backkurs '' Schritt für Schritt '' werden einfache Kekse, Muffins, Cupcakes und Torten in traumhafte Kunstwerke verwandelt. "
                                                + " Der süße Backkurs ist für kleine und große Hobbybäcker, Anfänger und Fortgeschrittene geeignet.");

                taskService.createTask(erniesKursUuid, "Googlehupf backen", LOGIN_ERNIE,
                                "Ein tolles " + "Rezept für Backfreunde",
                                "Ernie zeigt euch, welche Zutaten Ihr braucht, um einen tollen"
                                                + " Googlehupf zu backen. Bitte nicht an Google weitergeben, sonst werde ich gefeuert.");
                Long affenMuffinTask = taskService.createTask(erniesKursUuid, "Affenmuffins backen", LOGIN_ERNIE,
                                "Ernies " + "leckere Muffins für Kinder",
                                "Ernie zeigt euch, welche Zutaten und Schritte Ihr" + " braucht.");
                topicService.subscribe(erniesKursUuid, LOGIN_BERT);
                taskService.checkTask(affenMuffinTask, LOGIN_BERT);

                taskService.addCommentToTask(affenMuffinTask, LOGIN_BERT, "Wir wollen einen Blueberry-Muffin Task!");
                taskService.addCommentToTask(linkErstellenTask, LOGIN_BERT, "Top");
                taskService.addCommentToTask(linkErstellenTask, LOGIN_ERNIE, "Schlecht erklärt, musste extra auf"
                                + " YouTube nochmal recherchieren. Für den nächsten Task bitte lauter sprechen und die komische"
                                + " Hintergrundmusik ausschalten. Fazit: 1 von 5 Sternen.");
        }

}
