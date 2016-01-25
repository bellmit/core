package com.logginghub.logging.frontend.model;

import java.util.HashMap;
import java.util.Map;

public class ColumnSettingsModel {

    private boolean disableColumnFile = false;
    private Map<String, ColumnSettingModel> columnSettings = new HashMap<String, ColumnSettingModel>();

    public void setDisableColumnFile(boolean disableColumnFile) {
        this.disableColumnFile = disableColumnFile;
    }

    public boolean isDisableColumnFile() {
        return disableColumnFile;
    }

    public Map<String, ColumnSettingModel> getColumnSettings() {
        return columnSettings;
    }

    public ColumnSettingModel getSettingsForColumn(String column) {
        return columnSettings.get(column);
    }

    public final static class ColumnSettingModel {

        private final int width;
        private final int order;
        private final String alignment;
        private final String metadataMapping;
        private final String name;

        public ColumnSettingModel(String name, int width, int order, String alignment, String metadataMapping) {
            this.name = name;
            this.width = width;
            this.order = order;
            this.alignment = alignment;
            this.metadataMapping = metadataMapping;
        }

        public int getOrder() {
            return order;
        }

        public int getWidth() {
            return width;
        }

        public String getAlignment() {
            return alignment;
        }

        public String getMetadataMapping() {
            return metadataMapping;
        }

        public String getName() {
            return name;
        }
    }
}