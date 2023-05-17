package com.sample.airtickets.screen.flight;

import com.sample.airtickets.entity.User;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.ui.component.DateField;
import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Flight;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.TimeZone;

@UiController("Flight.browse")
@UiDescriptor("flight-browse.xml")
@LookupComponent("flightsTable")
public class FlightBrowse extends StandardLookup<Flight> {

    @Autowired
    private DateField<OffsetDateTime> takeOffFrom;
    @Autowired
    private DateField<OffsetDateTime> takeOffTo;
    @Autowired
    private CurrentUserSubstitution currentUserSubstitution;

    @Subscribe
    public void onInit(InitEvent event) {
        User user = (User) currentUserSubstitution.getEffectiveUser();
        String userTimeZoneId = user.getTimeZoneId();

//    ???
//        takeOffFrom.setTimeZone(TimeZone.getTimeZone(userTimeZoneId));
//        takeOffTo.setTimeZone(TimeZone.getTimeZone(userTimeZoneId));

    }


}