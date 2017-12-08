package com.flawyless.controller;

import com.flawyless.model.Card;
import com.flawyless.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = ControllerConstants.CARD_API_URL)
public class CardController {

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
        return cardService.getCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Card> addCard(@RequestBody Card card) {
        Card newCard = cardService.saveCard(card);
        URI newCardLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newCard.getId()).toUri();

        return ResponseEntity.created(newCardLocation).body(newCard);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Card> updateCard(@RequestBody Card newCard, @PathVariable Long id) {
        Optional<Card> card = cardService.getCardById(id);

        newCard.setId(id);

        return card.isPresent() ? ResponseEntity.ok(cardService.saveCard(newCard))
                : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Card> deleteCard(@PathVariable Long id) {
        try {
            cardService.deleteCard(id);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
