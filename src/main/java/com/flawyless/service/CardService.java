package com.flawyless.service;

import com.flawyless.model.Card;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CardService {

    List<Card> getAllCards();

    Optional<Card> getCardById(long id);

    Card saveCard(Card cardToSave);

    void deleteCard(long id);
}
