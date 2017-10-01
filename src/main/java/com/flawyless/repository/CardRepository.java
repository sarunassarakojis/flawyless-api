package com.flawyless.repository;

import com.flawyless.model.Card;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {
}
