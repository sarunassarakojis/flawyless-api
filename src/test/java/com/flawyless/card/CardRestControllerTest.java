package com.flawyless.card;

import com.flawyless.ApplicationLauncher;
import com.flawyless.controller.CardController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

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
        ResultActions resultActions = mockMvc.perform(get(CardController.CARD_API));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(testCards.size())));
        assertCardContentCorrectness(testCards, resultActions);
    }

    @Test
    public void attemptToReadSingleCard() throws Exception {
        Card testCard = testCards.get(0);

        mockMvc.perform(get(CardController.CARD_API + "/" + testCard.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is((int) testCard.getId())))
                .andExpect(jsonPath("$.summary", is(testCard.getSummary())))
                .andExpect(jsonPath("$.description", is(testCard.getDescription())));
    }

    @Test
    public void readCardThatDoesNotExist() throws Exception {
        long lastCardId = testCards.get(testCards.size() - 1).getId();

        mockMvc.perform(get(CardController.CARD_API + "/" + (lastCardId + 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void attemptToCreateNewCard() throws Exception {
        String cardJson = jsonify(new Card("sample_summary", "sample_description"));

        mockMvc.perform(post(CardController.CARD_API)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(cardJson))
                .andExpect(status().isCreated());
    }

    private static void assertCardContentCorrectness(List<Card> expectedCards, ResultActions resultActions) throws Exception {
        for (int i = 0, n = expectedCards.size(); i < n; i++) {
            Card card = expectedCards.get(i);

            resultActions.andExpect(jsonPath(createJsonPath(i, "id"), is((int) card.getId())))
                    .andExpect(jsonPath(createJsonPath(i, "summary"), is(card.getSummary())))
                    .andExpect(jsonPath(createJsonPath(i, "description"), is(card.getDescription())));
        }
    }

    private static String createJsonPath(int objectIndex, String fieldName) {
        return String.format("$[%d].%s", objectIndex, fieldName);
    }

    private String jsonify(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();

        messageConverter.write(o, MediaType.APPLICATION_JSON, outputMessage);

        return outputMessage.getBodyAsString();
    }
}
