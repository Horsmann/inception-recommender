package de.unidue.ltl.recommender.server;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainRequestController {

    @RequestMapping(value = "/train")
    public ResponseEntity <String> executeTraining(@RequestBody InceptionRequest inceptionReq) {

        String body = inceptionReq.getCAS() + " " + inceptionReq.getTarget();
        System.err.println("Body: " + body); 
        
        
        
        return new ResponseEntity<String>(body, HttpStatus.OK);
    }
    
    
    @ExceptionHandler
    void handleIllegalArgumentException(
      IllegalArgumentException e, HttpServletResponse response) throws IOException {
   
      response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}