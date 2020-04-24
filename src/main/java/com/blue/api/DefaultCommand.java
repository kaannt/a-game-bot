package com.blue.api;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class DefaultCommand implements Command {
    private List<String> names;
    private String shorthelp;

    public DefaultCommand(List<String> names, String shorthelp) {
        this.names = names;
        this.shorthelp = shorthelp;
    }

    @Override
    public String getShorthelp() {
        return shorthelp;
    }

    @Override
    public List<String> getNames() {
        return names;
    }
}
