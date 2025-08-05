package org.example;

import org.example.cli.CommandLineInterface;
import org.slf4j.event.Level;
import org.util.LogConfig;

public class Main {
    public static void main(String[] args) {
        LogConfig.setLogLevel(Level.INFO);
        new CommandLineInterface().start();
    }
}