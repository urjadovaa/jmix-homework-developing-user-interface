package com.sample.airtickets.screen.ticketreservation;

import com.sample.airtickets.app.TicketService;
import com.sample.airtickets.entity.Airport;
import com.sample.airtickets.entity.Flight;
import com.sample.airtickets.entity.Ticket;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputDialog;
import io.jmix.ui.component.*;
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
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private InputDialogFacet inputDialog;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Table<Flight> flightsTable;


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


    // Действие должно быть доступно как генерируемая колонка (custom / generated column) в таблице рейсов
    @Install(to = "flightsTable.actions", subject = "columnGenerator")
    private Component flightsTableActionsColumnGenerator(Flight flight) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(messageBundle.getMessage("reserveAction"));
        linkButton.addClickListener(clickEvent -> {
            // открытие окна ввода
            inputDialog.create().show();
        });
        return linkButton;
    }


    @Subscribe("inputDialog")
    public void onInputDialogInputDialogClose(InputDialog.InputDialogCloseEvent event) {
        if (event.closedWith(DialogOutcome.OK)) {
            if (flightsTable.getSingleSelected() == null) return;
            // Введенные пользователем значения проставить в создаваемый Ticket.
            Ticket ticket = dataManager.create(Ticket.class);
            ticket.setFlight(flightsTable.getSingleSelected());
            ticket.setPassengerName(event.getValue("passengerName"));
            ticket.setPassportNumber(event.getValue("passportNumber"));
            ticket.setTelephone(event.getValue("telephone"));
            ticket = ticketService.saveTicket(ticket);
            //  После этого открыть экран просмотра с созданным билетом.
            //  Просмотр билета должен открыться не в текущей, а в отдельной вкладке (NEW_TAB)
            screenBuilders.editor(Ticket.class, this)
                    .editEntity(ticket)
                    .withOpenMode(OpenMode.NEW_TAB)
                    .show();
        }
        if (event.closedWith(DialogOutcome.CANCEL)) {
            // Если диалог закрыт по Cancel - прервать процесс покупки билета
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(messageBundle.getMessage("appMessage"))
                    .withDescription(messageBundle.getMessage("ticketReservationCancel")).show();
        }
    }


}