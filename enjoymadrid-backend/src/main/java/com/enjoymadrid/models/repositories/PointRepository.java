package com.enjoymadrid.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.enjoymadrid.models.Point;

@NoRepositoryBean
public interface PointRepository<T extends Point> extends JpaRepository<T, Long>{
	
}
