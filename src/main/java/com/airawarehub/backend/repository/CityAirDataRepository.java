package com.airawarehub.backend.repository;

import com.airawarehub.backend.entity.CityAirData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityAirDataRepository extends JpaRepository<CityAirData, Long> {
    boolean existsByCity(String name);

    CityAirData findByCity(String city);
}
