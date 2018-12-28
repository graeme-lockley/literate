package za.co.no9.literate.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import za.co.no9.literate.tools.make.MainKt;


@Mojo(name = "literate")
public class SLEMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${project.basedir}/src/main/literate", alias = "sourceDirectory")
    private String sourceDirectory;

    @Parameter(defaultValue = "${project.basedir}/target/generated-sources/literate/java", alias = "outputDirectory")
    private String outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MainKt.build(new Log(getLog()), new java.io.File(sourceDirectory), new java.io.File(outputDirectory));
        project.addCompileSourceRoot(outputDirectory);
    }
}

