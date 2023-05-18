package com.sample.airtickets.screen.ticketreservation;

import com.sample.airtickets.app.TicketService;
import com.sample.airtickets.entity.Airport;
import com.sample.airtickets.entity.Flight;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.DateField;
import io.jmix.ui.component.EntityComboBox;
import io.jmix.ui.component.ProgressBar;
import io.jmix.ui.executor.BackgroundTask;
import io.jmix.ui.executor.BackgroundTaskHandler;
import io.jmix.ui.executor.BackgroundWorker;
import io.jmix.ui.executor.TaskLifeCycle;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;


@UiController("TicketReservation")
@UiDescriptor("ticket-reservation.xml")
public class TicketReservation extends Screen {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private EntityComboBox<Airport> from;
    @Autowired
    private EntityComboBox<Airport> to;
    @Autowired
    private DateField<OffsetDateTime> departureDate;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private CollectionLoader<Airport> airportsDl;
    @Autowired
    private CollectionContainer<Flight> flightsDc;
    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private ProgressBar searchProgress;


    @Subscribe
    public void onInit(InitEvent event) {
        airportsDl.load();
    }


    // запуск поиска рейсов
    @Subscribe("searchFlights")
    public void onSearchFlights(Action.ActionPerformedEvent event) {
        loadFlights();
    }


    // загрузчик рейсов
    public void loadFlights() {

        // Если все пустые - то вывести уведомление-warning "Please fill at least one filter field".
        if (from.getValue() == null && to.getValue() == null && departureDate.getValue() == null) {
            notifications.create(Notifications.NotificationType.WARNING).
                    withCaption(messageBundle.getMessage("appWarning")).
                    withDescription(messageBundle.getMessage("filterIsEmpty")).
                    show();
            flightsDc.setItems(null);
            return;
        }

        final LocalDate date =  departureDate.getValue() != null ? departureDate.getValue().toLocalDate() : null;
        final Airport fromAirport = from.getValue();
        final Airport toAirport = to.getValue();

        // Метод ищет рейсы долго, несколько секунд, поэтому для загрузки данных необходимо запускать фоновую задачу (BackgroundTask)
        BackgroundTask<Void, List<Flight>> task = new BackgroundTask<Void, List<Flight>>(100, this) {
            @Override
            public List<Flight> run(TaskLifeCycle<Void> taskLifeCycle) throws Exception {
                return ticketService.searchFlights(fromAirport, toAirport, date);
            }

            @Override
            public void done(List<Flight> result) {
                flightsDc.setItems(result);
                searchProgress.setVisible(false);
            }
        };
        BackgroundTaskHandler<List<Flight>> taskHandler = backgroundWorker.handle(task);
        searchProgress.setVisible(true);
        taskHandler.execute();
    }


    // очистка полей фильтра
    @Subscribe("clearFilter")
    public void onClearFilter(Action.ActionPerformedEvent event) {
        from.clear();
        to.clear();
        departureDate.clear();
        flightsDc.setItems(null);
    }


}