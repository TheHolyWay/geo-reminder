package ru.holyway.georeminder.data;

import org.springframework.data.repository.CrudRepository;
import ru.holyway.georeminder.entity.UserTask;

public interface UserTaskRepository extends CrudRepository<UserTask, String> {

}
