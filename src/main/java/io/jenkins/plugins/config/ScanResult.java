package io.jenkins.plugins.config;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Run;

public class ScanResult implements Action {
    private final Run<?, ?> owner;
    // private String name;
    private FilePath workspace;
    //workspace.getRemote()  
    //owner.getRootDir()
    public ScanResult(Run<?, ?> owner, FilePath workspace) {
        this.owner = owner;
        this.workspace = workspace;
    }
    public String getScanResult() {
        return workspace.getRemote() + "/format/format_result.json";
    }

    public String getHistoryImage() {
        return workspace.getRemote() + "/data/graph/histogram.png";
    }

    @Override
    public String getDisplayName() {

        return "ScanResult";
    }

    @Override
    public String getIconFileName() {
        return "scan";
    }

    @Override
    public String getUrlName() {
        return "scan";
    }

    // public void getHistoryReport() throws IOException {
    //     File buildDirectory = new File(owner.getRootDir(), "scan");
        
    // }

    // private void generateReport() throws IOException {
    //     File buildDirectory = new File(owner.getRootDir(), "scan");
    // }
}