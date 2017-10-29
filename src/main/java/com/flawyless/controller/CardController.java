package com.flawyless.controller;

import com.flawyless.model.Card;
import com.flawyless.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = CardController.CARD_API)
public class CardController {

    public static final String CARD_API = "/cards";
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Card> readAllCards() {
        return cardService.getAllCards();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Card> readCard(@PathVariable Long id) {
        Optional<Card> card = cardService.getCardById(id);

        return card.isPresent() ? ResponseEntity.ok(card.get()) : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Card> addCard(@RequestBody Card card) {
        Card newCard = cardService.saveCard(card);
        URI newCardLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newCard.getId()).toUri();

        return ResponseEntity.created(newCardLocation).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Card> updateCard(@RequestBody Card newCard, @PathVariable Long id) {
        Optional<Card> card = cardService.getCardById(id);
        Card replacement;

        if (card.isPresent()) {
            updateCardValues(replacement = card.get(), newCard);

            return ResponseEntity.ok(cardService.saveCard(replacement));
        }

        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Card> deleteCard(@PathVariable Long id) {
        if (cardService.getCardById(id).isPresent()) {
            cardService.deleteCard(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    private void updateCardValues(Card toUpdate, Card updater) {
        toUpdate.setSummary(updater.getSummary());
        toUpdate.setDescription(updater.getDescription());
    }
}
