package ru.holyway.georeminder.entity;

import org.telegram.telegrambots.api.objects.Location;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class UserTask {

    @Id
    private String id;

    private Number userID;

    private String message;

    private Location location;

    private Long notifyTime = System.currentTimeMillis();

    private Long chatID;

    private String targetPlace;

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public String getTargetPlace() {
        return targetPlace;
    }

    public void setTargetPlace(String targetPlace) {
        this.targetPlace = targetPlace;
    }

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public enum TaskType {
        SIMPLE,
        PLACE
    }

    public enum EventType {
        SINGLE,
        EVENT
    }

    public UserTask(String id, Number userID, String message, Location location, Long chatID) {
        this.id = id;
        this.userID = userID;
        this.message = message;
        this.location = location;
        this.chatID = chatID;
    }

    public UserTask(Number userID, String message, Location location, Long chatID) {
        this.userID = userID;
        this.message = message;
        this.location = location;
        this.chatID = chatID;
        this.id = UUID.randomUUID().toString();
    }

    public UserTask() {
        this.id = UUID.randomUUID().toString();
        this.taskType = TaskType.SIMPLE;
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }

    public Number getUserID() {
        return userID;
    }

    public void setUserID(Number userID) {
        this.userID = userID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Long notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Long getChatID() {
        return chatID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTask userTask = (UserTask) o;
        return Objects.equals(id, userTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
