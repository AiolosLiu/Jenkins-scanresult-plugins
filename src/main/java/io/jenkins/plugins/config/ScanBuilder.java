package io.jenkins.plugins.config;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;

public class ScanBuilder extends Builder implements SimpleBuildStep {

    private final String name;

    @DataBoundConstructor
    public ScanBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        listener.getLogger().println("Check report folders whether exist");
        String path = workspace.getRemote() + "/data";
        
        File workdir = new File(path);
        if (!workdir.exists()){
            listener.getLogger().println("Creating data folder");
            workdir.mkdir();

            String gPath = workspace.getRemote() + "/data/graph";
            File gdir = new File(gPath);
            if (!gdir.exists()){
                listener.getLogger().println("Creating data/graph folder");
                gdir.mkdir();
            }
            String fPath = workspace.getRemote() + "/data/format";
            File fdir = new File(fPath);
            if (!fdir.exists()){
                listener.getLogger().println("Creating data/graph folder");
                fdir.mkdir();
            }
            String hPath = workspace.getRemote() + "/data/history";
            File hdir = new File(hPath);
            if (!hdir.exists()){
                listener.getLogger().println("Creating data/history folder");
                hdir.mkdir();
            }
            String sPath = workspace.getRemote() + "/data/scan";
            File sdir = new File(sPath);
            if (!sdir.exists()){
                listener.getLogger().println("Creating data/scan folder");
                sdir.mkdir();
            }
        }
        listener.getLogger().println("Doing scanning!");
        //******scan function******
        //
        //
        listener.getLogger().println("Finish scanning and generate report...");

        GenerateGraph gg = new GenerateGraph();
        
        gg.transformat(path, "testdata.json");
        try {
            gg.generate(path); 
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        listener.getLogger().println("Report generated");
        run.addAction(new ScanResult(run, workspace));
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Scanner";
        }

    }

}
