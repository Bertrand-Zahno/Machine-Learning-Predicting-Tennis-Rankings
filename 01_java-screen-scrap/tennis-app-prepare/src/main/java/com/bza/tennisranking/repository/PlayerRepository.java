package com.bza.tennisranking.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bza.tennisranking.data.Player;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
	List<Player> findByFirstName(String firstName);
	Player	findById(long id);
	Player findBySwisstennisId(int swisstennisId);
	Set<Player> findAll();
}

