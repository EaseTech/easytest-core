package org.easetech.easytest.example;

import org.junit.Ignore;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Format;

@Ignore
@DataLoader(filePaths={"paramTestConditions.csv"} , writeData=false)
@Format(date={"dd-MM-yyyy", "dd/MM/yy"})
public class TestDatesPolicy {

}
