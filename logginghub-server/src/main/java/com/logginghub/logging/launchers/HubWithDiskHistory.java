package com.logginghub.logging.launchers;

/**
 * Created by james on 28/01/15.
 */
public class HubWithDiskHistory {
    public static void main(String[] args) {
        RunHub.fromConfiguration("src/main/resources/configs/hub/hub.with.disk.history.xml");
    }
}
