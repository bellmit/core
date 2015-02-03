package com.logginghub.logging.frontend.views.historicalstack.historicalevents;

import com.logginghub.logging.frontend.binary.ImportController;
import com.logginghub.logging.frontend.model.EnvironmentModel;
import com.logginghub.logging.frontend.modules.EnvironmentMessagingService;
import com.logginghub.logging.frontend.modules.configuration.HistoryViewConfiguration;
import com.logginghub.logging.frontend.services.LayoutService;
import com.logginghub.logging.messages.StackSnapshot;
import com.logginghub.utils.Destination;
import com.logginghub.utils.SystemTimeProvider;
import com.logginghub.utils.TimeProvider;
import com.logginghub.utils.TimeUtils;
import com.logginghub.utils.logging.Logger;
import com.logginghub.utils.module.Inject;
import com.logginghub.utils.module.Module;
import com.logginghub.utils.module.ServiceDiscovery;
import com.logginghub.utils.observable.ObservablePropertyListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Component;


public class HistoricalStackViewModule implements Module<HistoryViewConfiguration> {

    private static final Logger logger = Logger.getLoggerFor(HistoricalStackViewModule.class);

    private JTable resultsTable;

    private FixedColumnTable fixedColumnTable;

    //    private HistoricalStackUnderlyingModel underlyingModel = new HistoricalStackUnderlyingModel();

    private HistoricalStackTableDataModel tableModel = new HistoricalStackTableDataModel();
    //    private HistoricalStackTableHeadersModel headersModel = new HistoricalStackTableHeadersModel(underlyingModel);

    //    private JTable headersTable;

    //    private JTextArea area;

    // private DetailedLogEventTablePanel detailedLogEventTablePanel;
    //    private EnvironmentMessagingService messagingService;
    private ImportController importController;
    private EnvironmentModel environmentModel;
    //    private EnvironmentNotificationService environmentNotificationService;
    private TimeProvider timeProvider = new SystemTimeProvider();

    private String name;
    private String layout;
    private LayoutService layoutService;

    //    private TimeSelectionView timeSelectionView = new TimeSelectionView();
    private final JPanel contentPanel;

    //    private TimeSelectionModel timeSelectionModel =new TimeSelectionModel();

    private HistoricalStackSearchModel historicalStackSearchModel;
    private HistoricalStackSearchController historicalSearchController;
    private HistoricalStackSearchControlsView historicalSearchControlsView = new HistoricalStackSearchControlsView();

    public HistoricalStackViewModule(EnvironmentMessagingService messagingService) {


        historicalStackSearchModel = new HistoricalStackSearchModel();
        long now = timeProvider.getTime();
        historicalStackSearchModel.getTimeSelectionModel().getEndTime().getTime().set(now);
        historicalStackSearchModel.getTimeSelectionModel().getStartTime().getTime().set(TimeUtils.before(now, "1 minute"));

        historicalSearchController = new HistoricalStackSearchController(messagingService, historicalStackSearchModel);
        historicalSearchControlsView.bind(historicalSearchController);

        contentPanel = new JPanel(new MigLayout("", "[fill, grow]", "[fill][fill][fill, grow]"));

        contentPanel.add(historicalSearchControlsView, "cell 0 0");

        historicalSearchControlsView.setBorder(BorderFactory.createTitledBorder("Commands"));


        resultsTable = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                JLabel c = (JLabel) super.prepareRenderer(renderer, row, column);

                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);

                final Object valueAt = tableModel.getValueAt(row, column);
                if (valueAt != null) {
                    final String stringValue = valueAt.toString();
                    c.setText(stringValue);

                    if (stringValue.equals("RUNNABLE")) {
                        c.setBackground(Color.GREEN);
                        c.setText("");
                    } else if (stringValue.equals("WAITING")) {
                        c.setBackground(Color.YELLOW);
                        c.setText("");
                    } else if (stringValue.equals("TIMED_WAITING")) {
                        c.setBackground(Color.YELLOW.darker());
                        c.setText("");
                    } else if (stringValue.equals("BLOCKED")) {
                        c.setBackground(Color.RED);
                        c.setText("");
                    } else if (stringValue.equals("NEW")) {
                        c.setBackground(Color.CYAN);
                        c.setText("");
                    } else if (stringValue.equals("TERMINATED")) {
                        c.setBackground(Color.BLACK);
                        c.setForeground(Color.WHITE);
                        c.setText("");
                    }
                } else {
                    c.setText("");
                }

                if (column >= HistoricalStackTableDataModel.fixedColumnCount) {
                    String trace = tableModel.getTrace(row, column);
                    c.setToolTipText(trace);
                } else {
                    c.setToolTipText(null);
                }
                return c;
            }
        };

        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        final TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();

        final TableColumnModel columnModel = resultsTable.getColumnModel();
        historicalStackSearchModel.getResultsStream().addDestination(new Destination<StackSnapshot>() {
            @Override public void send(final StackSnapshot stackSnapshot) {
                tableModel.add(stackSnapshot);
                tableModel.fireTableStructureChanged();

                for (int i = 5; i < resultsTable.getColumnModel().getColumnCount(); i++) {
                    TableColumn column = columnModel.getColumn(i);
                    column.setPreferredWidth(20);
                    column.setHeaderRenderer(headerRenderer);
                }

            }
        });

        historicalStackSearchModel.getSearchInProgress().addListenerAndNotifyCurrent(new ObservablePropertyListener<Boolean>() {
            @Override public void onPropertyChanged(Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    tableModel.clear();
                    tableModel.fireTableStructureChanged();
                }
            }
        });

        JScrollPane scroller = new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        //        fixedColumnTable = new FixedColumnTable(4, scroller);

        //        JViewport jv2 = new JViewport();
        //        jv2.add(headersTable);
        //        scroller.setRowHeader(jv2);
        //        scroller.setRowHeaderView(headersTable);

        scroller.setBorder(BorderFactory.createTitledBorder("Results"));
        contentPanel.add(scroller, "cell 0 2");

        environmentModel = new EnvironmentModel();

    }

    public void setName(String name) {
        this.name = name;
    }

    //    @Inject public void setMessagingService(EnvironmentMessagingService messagingService) {
    //        this.messagingService = messagingService;
    //    }

    //    @Inject public void setEnvironmentNotificationService(EnvironmentNotificationService environmentNotificationService) {
    //        this.environmentNotificationService = environmentNotificationService;
    //    }

    @Inject public void setLayoutService(LayoutService layoutService) {
        this.layoutService = layoutService;
    }

    @Override public void configure(HistoryViewConfiguration configuration, ServiceDiscovery discovery) {
        layoutService = discovery.findService(LayoutService.class);
        //        messagingService = discovery.findService(EnvironmentMessagingService.class, configuration.getEnvironmentRef());
        //        environmentNotificationService = discovery.findService(EnvironmentNotificationService.class, configuration.getEnvironmentRef());
    }

    public void initialise() {
        layoutService.add(contentPanel, layout);
    }

    @Override public void start() {

    }

    @Override public void stop() {}


}
