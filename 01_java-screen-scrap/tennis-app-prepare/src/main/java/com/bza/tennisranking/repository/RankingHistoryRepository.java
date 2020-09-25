package com.bza.tennisranking.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bza.tennisranking.data.RankingHistory;

@Repository
public interface RankingHistoryRepository extends CrudRepository<RankingHistory, Long> {
	RankingHistoryRepository	findById(long id);
	Set<RankingHistory>         findAll();
	List<RankingHistory>	    findByPeriode(String periode);
}

