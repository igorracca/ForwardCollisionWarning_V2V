package com.here.fcws_mapmarker.model;

import com.opencsv.CSVWriter;
import java.io.Serializable;

public class CSVwriterParcelable implements Serializable {

    public CSVWriter writer;

    public CSVwriterParcelable(CSVWriter writer) {
        this.writer = writer;
    }
}
