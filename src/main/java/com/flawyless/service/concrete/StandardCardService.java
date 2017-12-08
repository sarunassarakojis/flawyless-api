package com.flawyless.service.concrete;

import com.flawyless.model.Card;
import com.flawyless.repository.CardRepository;
import com.flawyless.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StandardCardService implements CardService {

    private CardRepository cardRepository;

    @Autowired
    public StandardCardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Optional<Card> getCardById(long id) {
        return Optional.ofNullable(cardRepository.findOne(id));
    }

    public Card saveCard(Card cardToSave) {
        return cardRepository.save(cardToSave);
    }

    public void deleteCard(long id) {
        cardRepository.delete(id);
    }
}
