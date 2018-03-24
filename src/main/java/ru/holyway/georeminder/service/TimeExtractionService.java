package ru.holyway.georeminder.service;

import java.util.Date;

public interface TimeExtractionService {

    Date extractTime(String message);
}
