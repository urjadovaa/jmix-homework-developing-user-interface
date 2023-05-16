package com.sample.airtickets.screen.airport;

import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Airport;

@UiController("Airport.browse")
@UiDescriptor("airport-browse.xml")
@LookupComponent("airportsTable")
public class AirportBrowse extends StandardLookup<Airport> {
}