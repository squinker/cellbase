package org.opencb.cellbase.app.cli;

import org.opencb.cellbase.core.CellBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by imedina on 03/02/15.
 */
public abstract class CommandParser {

    protected String logLevel;
    protected boolean verbose;
    protected String configFile;

    protected String appHome;
    protected CellBaseConfiguration configuration;

    protected Logger logger;

    public CommandParser() {

    }

    public CommandParser(String logLevel, boolean verbose, String configFile) {
        this.logLevel = logLevel;
        this.verbose = verbose;
        this.configFile = configFile;

        this.appHome = System.getProperty("app.home", "/opt/cellbase");

        if(logLevel != null && !logLevel.isEmpty()) {
            // We must call to this method
            setLogLevel(logLevel);
        }
    }

    public abstract void parse();

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        // This small hack allow to configure the appropriate Logger level from the command line, this is done
        // by setting the DEFAULT_LOG_LEVEL_KEY before the logger object is created.
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel);
        logger = LoggerFactory.getLogger(this.getClass().toString());
        this.logLevel = logLevel;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public Logger getLogger() {
        return logger;
    }

    public void readCellBaseConfiguration() throws URISyntaxException, IOException {
//        this.configuration = CellBaseConfiguration.load(CellBaseConfiguration.class.getClassLoader().getResourceAsStream("configuration.json"));
        if(this.configFile != null) {
            this.configuration = CellBaseConfiguration.load(new FileInputStream(new File(this.configFile)));
        }else {
            this.configuration = CellBaseConfiguration.load(new FileInputStream(new File(this.appHome+"/configuration.json")));
        }
    }
}
