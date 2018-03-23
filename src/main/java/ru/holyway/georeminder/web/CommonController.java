package ru.holyway.georeminder.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by seiv0814 on 10-04-17.
 */
@RestController
public class CommonController {

    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity echo() {
        return new ResponseEntity(HttpStatus.OK);
    }

}
