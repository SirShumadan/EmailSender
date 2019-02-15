package com.example.solution;

import com.example.solution.util.PropertiesFactory;

import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException {
        Properties properties = PropertiesFactory.getProperties();
    }
}
