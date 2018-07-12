package de.unidue.ltl.recommender.server.train;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.unidue.ltl.recommender.core.train.TrainModel;
import de.unidue.ltl.recommender.server.InceptionRequest;

@RestController
public class TrainRequestController
{

    @RequestMapping(value = "/train")
    public ResponseEntity<String> executeTraining(@RequestBody InceptionRequest inceptionReq)
    {

        String cas = inceptionReq.getCAS();
        String typesystem = inceptionReq.getTypesystem();
        String layer = inceptionReq.getLayer();
        String target = inceptionReq.getTarget();
        File modelLocation = new File(FileUtils.getTempDirectory(), "tc-model" + layer);

        try {
            trainModel(cas, typesystem, layer, target, modelLocation);
        }
        catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(
                "No errors occured - model located at [" + modelLocation.getAbsolutePath() + "]",
                HttpStatus.OK);
    }

    private void trainModel(String cas, String typesystem, String layer, String target,
            File modelLocation)
        throws Exception
    {
        TrainModel model = new TrainModel();
        model.run(cas, typesystem, layer, target, modelLocation);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response)
        throws IOException
    {

        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}