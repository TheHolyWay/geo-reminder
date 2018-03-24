package ru.holyway.georeminder.data;

import org.springframework.data.repository.CrudRepository;
import ru.holyway.georeminder.entity.UserTask;

import java.util.List;

public interface UserTaskRepository extends CrudRepository<UserTask, String> {
}
