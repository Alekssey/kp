package ru.mpei.brics.extention.knowledgeBase;

import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;

public abstract class KnowledgeBaseCommunicator {
    public abstract double getFitnessValue(NetworkElementConfiguration cfg);
    public abstract boolean getAllowSignal(NetworkElementConfiguration cfg);
}
