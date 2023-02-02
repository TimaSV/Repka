package searchengine.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrorController {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Exception> nullPointerException(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Exception("Данные поискового запроса" + " отсутствуют в базе сервера: "
                        + exception.getMessage()));
    }

}
