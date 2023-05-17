package com.sample.airtickets.screen.flight;

import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Flight;

@UiController("Flight.edit")
@UiDescriptor("flight-edit.xml")
@EditedEntityContainer("flightDc")
@DialogMode(modal = true, forceDialog = true, width = "AUTO", height = "AUTO")
public class FlightEdit extends StandardEditor<Flight> {
}