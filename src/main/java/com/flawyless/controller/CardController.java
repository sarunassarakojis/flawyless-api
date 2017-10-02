package com.flawyless.controller;

import com.flawyless.model.Card;
import com.flawyless.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@RestController
public class CardController {

    private final CardRepository cardRepository;

    @Autowired
    public CardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @RequestMapping(value = "/cards", method = RequestMethod.GET)
    public Collection<Card> readAllCards() {
        return cardRepository.findAll();
    }

    @RequestMapping(value = "/cards/{id}", method = RequestMethod.GET)
    public Card readCard(@PathVariable Long id) {
        return cardRepository.findOne(id);
    }

    @RequestMapping(value = "/cards", method = RequestMethod.POST)
    public void addCard(@RequestBody Card card, HttpServletResponse response) {
        Card save = cardRepository.save(card);
        response.setStatus(201);
    }

    @RequestMapping(value = "/cards/{id}", method = RequestMethod.PUT)
    public void updateCard(@RequestBody Card card, @PathVariable Long id, HttpServletResponse response) {
        Card fromRepo = cardRepository.findOne(id);

        fromRepo.setSummary(card.getSummary());
        fromRepo.setDescription(card.getDescription());
        cardRepository.save(fromRepo);
        response.setStatus(204);
    }

    @RequestMapping(value = "/cards/{id}", method = RequestMethod.DELETE)
    public void deleteCard(@PathVariable Long id, HttpServletResponse response) {
        cardRepository.delete(id);
        response.setStatus(200);
    }
}
