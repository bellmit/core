package com.logginghub.logging.modules.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.logginghub.logging.modules.PatternisedDiskHistoryModule;
import com.logginghub.utils.module.Configures;

@Configures(PatternisedDiskHistoryModule.class) @XmlAccessorType(XmlAccessType.FIELD) public class PatternisedDiskHistoryConfiguration {

    @XmlAttribute private int streamingBatchSize = 200;
    @XmlAttribute private String logEventSourceRef;
    @XmlAttribute private String folder = "binarylogs/";
    @XmlAttribute private String filename = "patternised.";

    @XmlAttribute private boolean readOnly = false;

    @XmlAttribute private String totalFileSizeLimit = "1 GB";
    @XmlAttribute private String fileSizeLimit = "128 MB";
    @XmlAttribute private String blockSize = "10 MB";
    @XmlAttribute private String maximumFlushInterval = "5 seconds";
    @XmlAttribute private boolean useEventTimes;

    public String getTotalFileSizeLimit() {
        return totalFileSizeLimit;
    }

    public void setTotalFileSizeLimit(String totalFileSizeLimit) {
        this.totalFileSizeLimit = totalFileSizeLimit;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getLogEventSourceRef() {
        return logEventSourceRef;
    }

    public void setLogEventSourceRef(String logEventSourceRef) {
        this.logEventSourceRef = logEventSourceRef;
    }

    public int getStreamingBatchSize() {
        return streamingBatchSize;
    }

    public void setStreamingBatchSize(int streamingBatchSize) {
        this.streamingBatchSize = streamingBatchSize;
    }

    public String getFilename() {
        return filename;
    }

    public String getFolder() {
        return folder;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setFileSizeLimit(String fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }

    public void setBlockSize(String blockSize) {
        this.blockSize = blockSize;
    }

    public String getBlockSize() {
        return blockSize;
    }

    public String getFileSizeLimit() {
        return fileSizeLimit;
    }

    public void setMaximumFlushInterval(String maximumFlushInterval) {
        this.maximumFlushInterval = maximumFlushInterval;
    }

    public String getMaximumFlushInterval() {
        return maximumFlushInterval;
    }

    public void setUseEventTimes(boolean useEventTimes) {
        this.useEventTimes = useEventTimes;
    }
    
    public boolean isUseEventTimes() {
        return useEventTimes;
    }
}
