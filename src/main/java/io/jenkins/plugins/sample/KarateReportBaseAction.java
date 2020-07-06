package io.jenkins.plugins.sample;

import hudson.model.Action;

import net.masterthought.cucumber.ReportBuilder;

public abstract class KarateReportBaseAction implements Action {

    protected static final String ICON_NAME = "/plugin/karate-reporting/Karate.png";

    @Override
    public String getUrlName() {
        return ReportBuilder.HOME_PAGE;
    }

    @Override
    public String getIconFileName() {
        return ICON_NAME;
    }
}