package com.sample.airtickets.screen.airline;

import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Airline;

@UiController("Airline.browse")
@UiDescriptor("airline-browse.xml")
@LookupComponent("table")
public class AirlineBrowse extends MasterDetailScreen<Airline> {
}