package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/dataNotFoundException")
    public void throwDataNotFoundException() {
        throw new DataNotFoundException("Data not found");
    }

    @GetMapping("/dataNotCorrectException")
    public void throwDataNotCorrectException() {
        throw new DataNotCorrectException("Data not correct");
    }

    @GetMapping("/notCorrectRequestException")
    public void throwNotCorrectRequestException() {
        throw new NotCorrectRequestException("Request not correct");
    }
}
