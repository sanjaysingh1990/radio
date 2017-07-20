package com.raj.moh.appversionchecker;

/**
 * Created by NEERAJ on 7/1/2017.
 */

public interface UpdateApplication {
    void newVersionFound(String latestVersion);
    void noUpdate();
}
