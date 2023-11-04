package com.airawarehub.backend.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CityAirData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String city;
    private String state;
    private String country;
    private String lat;
    private String lng;
    private String ts;
    private int aqius;
    private String mainus;
    private int aqicn;
    private String maincn;
    private String tp;
    private String pr;
    private int hu;
    private double ws;
    private int wd;
    private String ic;
}
