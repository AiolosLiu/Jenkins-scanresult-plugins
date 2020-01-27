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
        GenerateGraph gg = new GenerateGraph();
        String path = workspace.getRemote() + "/data";
        String result = gg.transformat(path);
        try {
            gg.generate(path); 
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        return result;
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