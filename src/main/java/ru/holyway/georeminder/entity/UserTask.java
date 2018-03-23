package ru.holyway.georeminder.entity;

import org.telegram.telegrambots.api.objects.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class UserTask {

    @Id
    private String id;

    private Integer userID;

    private String message;

    private Location location;

    private Long notifyTime = System.currentTimeMillis();

    private Long chatID;

    public UserTask(String id, Integer userID, String message, Location location, Long chatID) {
        this.id = id;
        this.userID = userID;
        this.message = message;
        this.location = location;
        this.chatID = chatID;
    }

    public UserTask(Integer userID, String message, Location location, Long chatID) {
        this.userID = userID;
        this.message = message;
        this.location = location;
        this.chatID = chatID;
        this.id = UUID.randomUUID().toString();
    }

    public UserTask() {
        this.id = UUID.randomUUID().toString();
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
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
