package com.bza.tennisranking.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bza.tennisranking.data.CalculationRun;

@Repository
public interface CalculationRunRepository extends CrudRepository<CalculationRun, Long> {
	Set<CalculationRun> findAll();
}
