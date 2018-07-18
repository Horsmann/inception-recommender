/*******************************************************************************
 * Copyright 2018
 * Language Technology Lab
 * University of Duisburg-Essen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unidue.ltl.recommender.server;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import de.unidue.ltl.recommender.server.repository.Repository;
import de.unidue.ltl.recommender.server.tc.prediction.Predictor;
import de.unidue.ltl.recommender.server.train.InceptionRecommenderModel;
import de.unidue.ltl.recommender.server.train.Trainer;

@RunWith(SpringRunner.class)
@WebMvcTest(RequestController.class)
public class RequestControllerTest
{

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Repository rep;
    @MockBean
    private Trainer trainer;
    @MockBean
    private Predictor predictor;
    
    @Test
    public void trainRequest() throws Exception
    {
        String trainRequest = FileUtils
                .readFileToString(new File("src/test/resources/jsonTrainRequest.txt"), "utf-8");

        mockMvc.perform(MockMvcRequestBuilders.post("/train")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(trainRequest))
                .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }
    
    @Test
    public void predictRequest() throws Exception
    {
        InceptionRecommenderModel model = Mockito.mock(InceptionRecommenderModel.class);
        Mockito.when(rep.getModel(Mockito.anyString())).thenReturn(model);
        Mockito.doNothing().when(predictor).predict(Mockito.any(), Mockito.any());
        Mockito.when(predictor.getResultsAsJson()).thenReturn("Test");
        
        String predictRequest = FileUtils
                .readFileToString(new File("src/test/resources/jsonPredictionRequest.txt"), "utf-8");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/predict")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(predictRequest))
                .andExpect(MockMvcResultMatchers.status()
                .isOk())
                .andReturn();
        
        String contentAsString = result.getResponse().getContentAsString();
        assertEquals("Test", contentAsString);
    }

}
