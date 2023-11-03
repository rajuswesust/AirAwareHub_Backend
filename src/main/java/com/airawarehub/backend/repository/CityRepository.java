package com.airawarehub.backend.repository;

import com.airawarehub.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameStartingWith(String name);
    @Query("SELECT c FROM City c WHERE c.name LIKE %:name%")
    List<City> findCitiesByNameContaining(@Param("name") String name);
}
