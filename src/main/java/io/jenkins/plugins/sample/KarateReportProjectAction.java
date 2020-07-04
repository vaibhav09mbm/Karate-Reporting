package io.jenkins.plugins.sample;

import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;

import net.masterthought.cucumber.ReportBuilder;

public class KarateReportProjectAction extends KarateReportBaseAction implements ProminentProjectAction {

	private final String KARATE_REPORT="karate-report";
    private final Job<?, ?> project;
    private String reportTitle;
    private String directorySuffix;

    public KarateReportProjectAction(Job<?, ?> project, String reportTitle, String directorySuffix) {
        this.reportTitle = reportTitle;
        this.project = project;
        this.directorySuffix = directorySuffix;
    }

    @Override
    public String getUrlName() {
        Run<?, ?> run = this.project.getLastCompletedBuild();
        if (run != null) {
            return run.getNumber() + "/" + Messages.SidePanel_DisplayNameNoTitle();
        }

        // none build was completed, report is yet not available
        return "hello";
    }

    @Override
    public String getDisplayName() {
        return reportTitle;
    }
}