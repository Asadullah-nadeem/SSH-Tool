package org.example;

import org.example.cli.CommandLineInterface;
import org.util.LogConfig;

import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        LogConfig.setLogLevel(Level.INFO);
        new CommandLineInterface().start();
    }
}