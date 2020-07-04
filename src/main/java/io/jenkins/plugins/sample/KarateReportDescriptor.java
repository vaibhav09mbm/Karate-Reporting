package io.jenkins.plugins.sample;

import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.QueryParameter;

import net.masterthought.cucumber.sorting.SortingMethod;

public class KarateReportDescriptor extends BuildStepDescriptor<Publisher> {

    @Override
    public String getDisplayName() {
        return Messages.Plugin_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }
    
   
}