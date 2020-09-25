package com.bza.tennisranking.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bza.tennisranking.data.LoadedPlayer;

@Repository
public interface LoadedPlayerRepository extends CrudRepository<LoadedPlayer, Long> {
	LoadedPlayer	findById(long id);
	Set<LoadedPlayer> findAll();
}

