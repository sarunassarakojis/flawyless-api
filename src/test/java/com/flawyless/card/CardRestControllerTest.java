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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationLauncher.class)
@WebAppConfiguration
public class CardRestControllerTest {

    private MockMvc mockMvc;
    private HttpMessageConverter<?> messageConverter;
    private List<Card> testCards;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    void setMessageConverter(HttpMessageConverter<?>[] messageConverters) {
        this.messageConverter = Arrays.asList(messageConverters).stream()
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
        testCards.add(cardRepository.save(new Card("TEST_SUMMARY_1", "TEST_DESCRIPTION_1")));
        testCards.add(cardRepository.save(new Card("TEST_SUMMARY_2")));
    }

    @Test
    public void attemptToReadAllCards() throws Exception {
        mockMvc.perform(get(CardController.CARD_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(testCards.size())));
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
}
