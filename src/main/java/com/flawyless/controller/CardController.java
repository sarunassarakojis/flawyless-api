package com.flawyless.controller;

import com.flawyless.model.Card;
import com.flawyless.repository.CardRepository;
import com.flawyless.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(value = CardController.CARD_API)
public class CardController {

    public static final String CARD_API = "/cards";
    private final CardRepository cardRepository;
    private final CardService cardService;

    @Autowired
    public CardController(CardRepository cardRepository, CardService cardService) {
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Card> readAllCards() {
        return cardRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Card> readCard(@PathVariable Long id) {
        Card card = cardRepository.findOne(id);

        return card != null ? ResponseEntity.ok(card) : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Card> addCard(@RequestBody Card card) {
        Card newCard = cardRepository.save(card);
        URI newCardLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newCard.getId()).toUri();

        return ResponseEntity.created(newCardLocation).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Card> updateCard(@RequestBody Card newCard, @PathVariable Long id) {
        Card card = cardRepository.findOne(id);

        if (card != null) {
            updateCardValues(card, newCard);

            return ResponseEntity.ok(cardRepository.save(card));
        }

        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Card> deleteCard(@PathVariable Long id) {
        if (cardRepository.findOne(id) != null) {
            cardRepository.delete(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    private void updateCardValues(Card toUpdate, Card updater) {
        toUpdate.setSummary(updater.getSummary());
        toUpdate.setDescription(updater.getDescription());
    }
}
