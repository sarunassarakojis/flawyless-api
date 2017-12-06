package com.flawyless.card;

import com.flawyless.ApplicationLauncher;
import com.flawyless.controller.ControllerConstants;
import com.flawyless.model.Card;
import com.flawyless.repository.CardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationLauncher.class)
@WebAppConfiguration
public class CardRestControllerTest {

    private MockMvc mockMvc;
    private HttpMessageConverter<Object> messageConverter;
    private List<Card> testCards;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    void setMessageConverter(HttpMessageConverter<Object>[] messageConverters) {
        this.messageConverter = Arrays.stream(messageConverters)
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElseThrow(() -> new AssertionError("No JSON message converter found, aborting..."));
    }

    @Before
    public void prepareTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testCards = new ArrayList<>(6);

        cardRepository.deleteAllInBatch();
        testCards.add(cardRepository.save(new Card("TEST_SUMMARY_0", "TEST_DESCRIPTION_0")));
        testCards.add(cardRepository.save(new Card("TEST_SUMMARY_1")));
    }

    @Test
    public void attemptToReadAllCards() throws Exception {
        ResultActions resultActions = mockMvc.perform(get(ControllerConstants.CARD_API_URL));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(testCards.size())));
        assertCardContentCorrectness(testCards, resultActions);
    }

    @Test
    public void attemptToReadSingleCard() throws Exception {
        Card testCard = testCards.get(0);

        ResultActions resultActions = mockMvc.perform(get(ControllerConstants.CARD_API_URL + "/" + testCard.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        assertCardContentCorrectness(testCard, resultActions);
    }

    @Test
    public void readCardThatDoesNotExist() throws Exception {
        long lastCardId = testCards.get(testCards.size() - 1).getId();

        mockMvc.perform(get(ControllerConstants.CARD_API_URL + "/" + (lastCardId + 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void attemptToCreateNewCard() throws Exception {
        Card testCard = new Card("sample_summary", "sample_description");

        ResultActions resultActions = mockMvc.perform(post(ControllerConstants.CARD_API_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonify(testCard)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        assertCardContentCorrectness(testCard, resultActions);
    }

    @Test
    public void attemptToUpdateCard() throws Exception {
        Card testCard = testCards.get(0);

        testCard.setDescription(testCard.getDescription() + System.currentTimeMillis());
        ResultActions resultActions = mockMvc.perform(put(ControllerConstants.CARD_API_URL + "/" + testCard.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonify(testCard)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        assertCardContentCorrectness(testCard, resultActions);
    }

    @Test
    public void attemptToDeleteCard() throws Exception {
        Card testCard = testCards.get(0);

        mockMvc.perform(delete(ControllerConstants.CARD_API_URL + "/" + testCard.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(get(ControllerConstants.CARD_API_URL + "/" + testCard.getId()))
                .andExpect(status().isNotFound());
    }

    private static void assertCardContentCorrectness(Card expectedCard, ResultActions resultActions) throws Exception {
        resultActions.andExpect(jsonPath(createJsonPath("summary"), is(expectedCard.getSummary())))
                .andExpect(jsonPath(createJsonPath("description"), is(expectedCard.getDescription())));
    }

    private static void assertCardContentCorrectness(List<Card> expectedCards, ResultActions resultActions) throws Exception {
        for (int i = 0, n = expectedCards.size(); i < n; i++) {
            Card card = expectedCards.get(i);

            resultActions.andExpect(jsonPath(createJsonPath(i, "summary"), is(card.getSummary())))
                    .andExpect(jsonPath(createJsonPath(i, "description"), is(card.getDescription())));
        }
    }

    private static String createJsonPath(int objectIndex, String fieldName) {
        return String.format("$[%d].%s", objectIndex, fieldName);
    }

    private static String createJsonPath(String fieldName) {
        return String.format("$.%s", fieldName);
    }

    private String jsonify(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();

        messageConverter.write(o, MediaType.APPLICATION_JSON, outputMessage);

        return outputMessage.getBodyAsString();
    }
}
