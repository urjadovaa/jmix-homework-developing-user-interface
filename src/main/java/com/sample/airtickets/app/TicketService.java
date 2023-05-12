package com.sample.airtickets.app;

import com.sample.airtickets.entity.Airport;
import com.sample.airtickets.entity.Flight;
import com.sample.airtickets.entity.Ticket;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TicketService {

    @Autowired
    private DataManager dataManager;

    /*
     * Search flights using given filter fields.
     */
    public List<Flight> searchFlights(@Nullable Airport airportFrom, @Nullable Airport airportTo, @Nullable LocalDate date) {
        if (airportFrom == null && airportTo == null && date == null) {
            throw new IllegalArgumentException("At least one filter should be specified");
        }

        // heavy search...
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String query = "select f from Flight f where";
        Map<String, Object> params = new HashMap<>();
        if (airportFrom != null) {
            query += " f.fromAirport = :af and";
            params.put("af", airportFrom);
        }
        if (airportTo != null) {
            query += " f.toAirport = :at and";
            params.put("at", airportTo);
        }
        if (date != null) {
            query += " f.takeOffDate >= :dateStart and f.takeOffDate < :dateEnd and";
            ZonedDateTime zonedDate = date.atStartOfDay(ZoneId.systemDefault());
            params.put("dateStart", zonedDate.toOffsetDateTime());
            params.put("dateEnd", zonedDate.plusDays(1).toOffsetDateTime());
        }
        query += " 1 = 1";
        return dataManager.load(Flight.class)
                .query(query)
                .parameters(params)
                .list();
    }

    /*
     * Assign unique reservation ID and save ticket to database.
     */
    public Ticket saveTicket(Ticket ticket) {
        int reservationId = ThreadLocalRandom.current().nextInt(1000, 9999);
        ticket.setReservationId(Integer.toString(reservationId));

        return dataManager.save(ticket);
    }
}