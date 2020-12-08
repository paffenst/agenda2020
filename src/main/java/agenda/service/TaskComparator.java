package agenda.service;

//simport agenda.service.dto.SubscriberTaskDto;

import agenda.service.dto.TaskDto;
import java.util.Comparator;

/*
 * Hilfsklasse zur Sortierung von Objekten der Klasse TaskDto oder tiefer.
 */
public class TaskComparator<T extends TaskDto> implements Comparator<T> {

    @Override
    public int compare(T task1, T task2) {
        return task1.getTitle().compareTo(task2.getTitle());
    }
}