package com.sample.airtickets.screen.airport;

import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Airport;

@UiController("Airport.edit")
@UiDescriptor("airport-edit.xml")
@EditedEntityContainer("airportDc")
@DialogMode(forceDialog = true, modal = true, height="AUTO", width = "AUTO")
public class AirportEdit extends StandardEditor<Airport> {

}