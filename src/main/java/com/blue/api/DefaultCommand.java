package com.blue.api;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class DefaultCommand implements Command {
    private String name;
    private String shorthelp;

    public DefaultCommand(String name, String shorthelp) {
        this.name = name;
        this.shorthelp = shorthelp;
    }

    @Override
    public String getShorthelp() {
        return shorthelp;
    }

    @Override
    public String getName() {
        return name;
    }
}
