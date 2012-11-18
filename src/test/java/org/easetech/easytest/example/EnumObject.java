package org.easetech.easytest.example;

import java.util.EnumSet;

public class EnumObject{
    
    public enum Workingday {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
        public static final EnumSet<Workingday> Workdays = EnumSet.range(
            Monday, Friday);
        public final boolean isWorkday() {
          return Workdays.contains(this);
        }
        public static final EnumSet<Workingday> WHOLE_WEEK = EnumSet.allOf(Workingday.class);
    }

}
