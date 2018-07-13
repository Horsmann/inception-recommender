package de.unidue.ltl.recommender.server.train;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.unidue.ltl.recommender.server.InceptionRequest;
import de.unidue.ltl.recommender.server.model.Model;
import de.unidue.ltl.recommender.server.model.ModelRepository;

@RestController
public class RequestController
{

    @Autowired
    ModelRepository repository;

    @RequestMapping(value = "/train", method = RequestMethod.POST)
    public ResponseEntity<String> executeTraining(@RequestBody InceptionRequest inceptionReq)
    {

        Model trainedModel = null;
        Trainer t = new InceptionTrainer();
        try {
            trainedModel = t.train(inceptionReq);
            repository.addModel(trainedModel);
        }
        catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(
                "No errors occured - model located at ["
                        + trainedModel.getFileSystemLocation().getAbsolutePath() + "]",
                HttpStatus.OK);
    }

    @RequestMapping(value = "/predict", method = RequestMethod.POST)
    public ResponseEntity<String> executePrediction(@RequestBody InceptionRequest inceptionReq)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response)
        throws IOException
    {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}