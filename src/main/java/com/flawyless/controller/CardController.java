package com.flawyless.controller;

import com.flawyless.model.Card;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CardController {

    @RequestMapping(name = "/")
    public String index() {
        return "mvc test";
    }

    @RequestMapping("/cards")
    public Iterable<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();

        cards.add(new Card("summary_0", "desc"));
        cards.add(new Card("summary_1", "desc"));
        cards.add(new Card("summary_2", "desc"));

        return cards;
    }
}
