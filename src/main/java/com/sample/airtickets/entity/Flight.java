package com.sample.airtickets.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "FLIGHT", indexes = {
        @Index(name = "IDX_FLIGHT_FROM_AIRPORT", columnList = "FROM_AIRPORT_ID"),
        @Index(name = "IDX_FLIGHT_TO_AIRPORT", columnList = "TO_AIRPORT_ID"),
        @Index(name = "IDX_FLIGHT_AIRLINE", columnList = "AIRLINE_ID")
})
@Entity
public class Flight {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @Column(name = "NUMBER_", nullable = false)
    private String number;

    @JoinColumn(name = "FROM_AIRPORT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Airport fromAirport;

    @JoinColumn(name = "TO_AIRPORT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Airport toAirport;

    @JoinColumn(name = "AIRLINE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Airline airline;

    @NotNull
    @Column(name = "TAKE_OFF_DATE", nullable = false)
    private OffsetDateTime takeOffDate;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public OffsetDateTime getTakeOffDate() {
        return takeOffDate;
    }

    public void setTakeOffDate(OffsetDateTime takeOffDate) {
        this.takeOffDate = takeOffDate;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Airport getToAirport() {
        return toAirport;
    }

    public void setToAirport(Airport toAirport) {
        this.toAirport = toAirport;
    }

    public Airport getFromAirport() {
        return fromAirport;
    }

    public void setFromAirport(Airport fromAirport) {
        this.fromAirport = fromAirport;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}