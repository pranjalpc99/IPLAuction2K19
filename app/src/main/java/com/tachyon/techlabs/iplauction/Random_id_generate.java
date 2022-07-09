package com.tachyon.techlabs.iplauction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Random_id_generate {

    public long id() {
        //long millis = System.currentTimeMillis();
        return System.currentTimeMillis();
    }
}
