package com.airawarehub.backend.repository;

import com.airawarehub.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameStartingWith(String name);
}
