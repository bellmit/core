package com.logginghub.logging.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.logginghub.logging.DefaultLogEvent;
import com.logginghub.logging.LogEvent;
import com.logginghub.logging.api.patterns.Pattern;
import com.logginghub.logging.listeners.LogEventListener;
import com.logginghub.logging.messaging.PatternModel;
import com.logginghub.logging.messaging.PatternisedLogEvent;
import com.logginghub.logging.modules.configuration.PatternConfiguration;
import com.logginghub.logging.utils.ValueStripper2;
import com.logginghub.utils.Destination;
import com.logginghub.utils.Multiplexer;
import com.logginghub.utils.NotImplementedException;
import com.logginghub.utils.observable.ObservableList;

public class PatternCollection implements Destination<LogEvent>, LogEventListener {

    private ObservableList<PatternModel> observablePatternModels = new ObservableList<PatternModel>(new ArrayList<PatternModel>());
    private ObservableList<Pattern> patternModels = new ObservableList<Pattern>(new ArrayList<Pattern>());

    private Multiplexer<PatternisedLogEvent> patternisedEventMultiplexer = new Multiplexer<PatternisedLogEvent>();
    private List<ValueStripper2> strippers = new CopyOnWriteArrayList<ValueStripper2>();
    private boolean matchAgainstAllPatterns = true;

    public void setMatchAgainstAllPatterns(boolean matchAgainstAllPatterns) {
        this.matchAgainstAllPatterns = matchAgainstAllPatterns;
    }

    public boolean isMatchAgainstAllPatterns() {
        return matchAgainstAllPatterns;
    }

    public void configureFromModels(List<PatternModel> patterns) {
        for (PatternModel patternConfiguration : patterns) {
            add(patternConfiguration);
        }
    }

    public void add(PatternModel observablePatternModel) {
        ValueStripper2 valueStripper2 = new ValueStripper2();

        valueStripper2.setPatternName(observablePatternModel.getName().asString());
        valueStripper2.setPattern(observablePatternModel.getPattern().asString());
        valueStripper2.setDebug(observablePatternModel.getDebug().asBoolean());

        strippers.add(valueStripper2);
        
        this.observablePatternModels.add(observablePatternModel);
    }
    
    public void add(Pattern patternModel) {
        ValueStripper2 valueStripper2 = new ValueStripper2();

        valueStripper2.setPatternID(patternModel.getPatternID());
        valueStripper2.setPatternName(patternModel.getName());
        valueStripper2.setPattern(patternModel.getPattern());
        valueStripper2.setDebug(patternModel.isDebug());

        strippers.add(valueStripper2);
        
        this.patternModels.add(patternModel);
    }
    
    public void remove(Pattern patternModel) {
        // TODO : implement me
        throw new NotImplementedException();
    }
    
    public void configureFromConfigurations(List<PatternConfiguration> patterns) {
        for (PatternConfiguration patternConfiguration : patterns) {
            ValueStripper2 valueStripper2 = new ValueStripper2();

            valueStripper2.setPatternID(patternConfiguration.getPatternID());
            valueStripper2.setPatternName(patternConfiguration.getName());
            valueStripper2.setPattern(patternConfiguration.getPattern());
            valueStripper2.setDebug(patternConfiguration.isDebug());

            strippers.add(valueStripper2);
            
            PatternModel model = new PatternModel();
            model.getDebug().set(patternConfiguration.isDebug());
            model.getCleanUp().set(patternConfiguration.isCleanup());
            model.getName().set(patternConfiguration.getName());
            model.getPattern().set(patternConfiguration.getPattern());

            this.observablePatternModels.add(model);
        }

    }

    public void addDestination(Destination<PatternisedLogEvent> listener) {
        patternisedEventMultiplexer.addDestination(listener);
    }

    public void removeDestination(Destination<PatternisedLogEvent> listener) {
        patternisedEventMultiplexer.removeDestination(listener);
    }

    public void send(LogEvent t) {
        processEvent(t);
    }

    /**
     * Patternise this event using the first pattern that matches.
     * 
     * @param t
     * @return
     */
    public PatternisedLogEvent patternise(LogEvent t) {
        for (final ValueStripper2 valueStripper2 : strippers) {
            final PatternisedLogEvent patternised = valueStripper2.patternise2(t);
            if (patternised != null) {
                return patternised;
            }
        }

        return null;
    }

    private void processEvent(LogEvent t) {
        for (final ValueStripper2 valueStripper2 : strippers) {
            final PatternisedLogEvent patternised = valueStripper2.patternise2(t);
            if (patternised != null) {
                patternisedEventMultiplexer.send(patternised);
                if (!matchAgainstAllPatterns) {
                    break;
                }
            }
        }
    }

    public DefaultLogEvent depatternise(PatternisedLogEvent patternisedLogEvent) {

        DefaultLogEvent depatternised = new DefaultLogEvent();
        depatternised.setLocalCreationTimeMillis(patternisedLogEvent.getTime());

        int patternID = patternisedLogEvent.getPatternID();

        // TODO: needs a lookup map?
        for (ValueStripper2 valueStripper2 : strippers) {
            if (valueStripper2.getPatternID() == patternID) {
                depatternised.setMessage(valueStripper2.depatternise(patternisedLogEvent));
                break;
            }
        }

        return depatternised;

    }

    public ObservableList<Pattern> getPatternList() {
        return patternModels;
    }
    
    public ObservableList<PatternModel> getObservablePatternList() {
        return observablePatternModels;
    }

    public List<String> getPatternNames() {
        List<String> names = new ArrayList<String>();

        for (PatternModel patternModel : observablePatternModels) {
            names.add(patternModel.getName().get());
        }
        
        for (Pattern patternModel : patternModels) {
            names.add(patternModel.getName());
        }

        Collections.sort(names);
        
        return names;

    }

    @Override public void onNewLogEvent(LogEvent event) {
        send(event);
    }

    

}
