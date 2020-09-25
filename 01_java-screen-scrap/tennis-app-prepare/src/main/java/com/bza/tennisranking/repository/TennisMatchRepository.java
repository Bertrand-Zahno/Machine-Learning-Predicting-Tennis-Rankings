package com.bza.tennisranking.repository;

import java.util.Date;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bza.tennisranking.data.TennisMatch;

@Repository
public interface TennisMatchRepository extends CrudRepository<TennisMatch, Long> {
	Set<TennisMatch> findAll();
	Set<TennisMatch> findByDateBetween(Date startDate, Date endDate);
}
