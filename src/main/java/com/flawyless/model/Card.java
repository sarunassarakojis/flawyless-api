package com.flawyless.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String summary;
    private String description;

    protected Card() {
    }

    public Card(String summary, String description) {
        this.summary = summary;
        this.description = description;
    }

    public Card(String summary) {
        this(summary, "");
    }

    public long getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }
}
