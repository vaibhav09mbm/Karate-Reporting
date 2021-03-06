package io.jenkins.plugins.sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import javax.annotation.Nonnull;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.reducers.ReducingMethod;
import net.masterthought.cucumber.sorting.SortingMethod;

public class KarateReportPublisher extends Publisher implements SimpleBuildStep {

	private final static String KARATE_REPORT = "karate-report";

	private String reportDirectory = "";
	private String reportTitle = "";

	private String buildStatus;
	private FilePath[] htmlFilesList;
	private boolean stopBuildOnFailedReport;

	private int trendsLimit;
	private String sortingMethod;

	private static void log(TaskListener listener, String message) {
		listener.getLogger().println("[Karate Report] " + message);
	}

	public int getTrendsLimit() {
		return trendsLimit;
	}

	@DataBoundSetter
	public void setTrendsLimit(int trendsLimit) {
		this.trendsLimit = trendsLimit;
	}

	public String getReportDirectory() {
		return reportDirectory;
	}

	@DataBoundSetter
	public void setReportDirectory(String reportDirectory) {
		this.reportDirectory = reportDirectory;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	@DataBoundSetter
	public void setReportTitle(String reportTitle) {
		this.reportTitle = StringUtils.isEmpty(reportTitle) ? "" : reportTitle.trim();
	}

	public String getDirectorySuffix() {
		return StringUtils.isEmpty(this.reportTitle) ? ""
				: UUID.nameUUIDFromBytes(reportTitle.getBytes(StandardCharsets.UTF_8)).toString();
	}

	public String getDirectorySuffixWithSeparator() {
		return StringUtils.isEmpty(this.reportTitle) ? "" : ReportBuilder.SUFFIX_SEPARATOR + getDirectorySuffix();
	}

	public String getBuildStatus() {
		return buildStatus;
	}

	@DataBoundSetter
	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}

	@DataBoundSetter
	public void setStopBuildOnFailedReport(boolean stopBuildOnFailedReport) {
		this.stopBuildOnFailedReport = stopBuildOnFailedReport;
	}

	public boolean getStopBuildOnFailedReport() {
		return stopBuildOnFailedReport;
	}

	@DataBoundSetter
	public void setSortingMethod(String sortingMethod) {
		this.sortingMethod = sortingMethod;
	}

	public String getSortingMethod() {
		return sortingMethod;
	}

	@DataBoundConstructor
	public KarateReportPublisher(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	
	@Override
	public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
			@Nonnull TaskListener listener) throws InterruptedException, IOException {

		// generateReport(run, workspace, listener);
		copyReport(run, workspace, listener);
		SafeArchiveServingRunAction caa = new SafeArchiveServingRunAction(run,
				new File(run.getRootDir(), getReportDirectory()),
				// ReportBuilder.BASE_DIRECTORY
				Messages.SidePanel_DisplayNameNoTitle(), "features.APITest.html", KarateReportBaseAction.ICON_NAME,
				getActionName(), htmlFilesList, getDirectorySuffixWithSeparator());
		run.addAction(caa);
	}

	private String getActionName() {
		return StringUtils.isEmpty(reportTitle) ? Messages.SidePanel_DisplayNameNoTitle()
				: String.format(Messages.SidePanel_DisplayNameNoTitle(), reportTitle);
	}

	private void copyReport(Run<?, ?> build, FilePath workspace, TaskListener listener)
			throws InterruptedException, IOException {
		FilePath directoryForReport = new FilePath(new FilePath(build.getRootDir()), getReportDirectory());
		FilePath inputDirectory = new FilePath(workspace, getReportDirectory());
		inputDirectory.copyRecursiveTo("**/*.html", directoryForReport);
		htmlFilesList = directoryForReport.list("features.*.html");

	}

	private boolean hasReportFailed(Reportable result, TaskListener listener) {
		// happens when the report could not be generated
		if (result == null) {
			log(listener, "Missing report result - report was not successfully completed");
			return true;
		}

		return false;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	public static class Classification extends AbstractDescribableImpl<Classification> implements Serializable {

		public String key;
		public String value;

		@DataBoundConstructor
		public Classification(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Extension
		public static class DescriptorImpl extends Descriptor<Classification> {

			@Override
			public String getDisplayName() {
				return "";
			}
		}
	}

	@Extension
	@Symbol("karate")
	public static class BuildStatusesDescriptorImpl extends KarateReportDescriptor {
	}
}