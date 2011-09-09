package org.apache.maven.plugin.war;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;

/**
 * Build a war overlay.
 * 
 * @author Alex Clarke
 * @author Phillip Webb
 * @version $Id$
 * @goal overlay
 * @phase package
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class WarOverlayMojo extends PackagedWarMojo {

	protected void attachWarArtifact(File warArchive) {
        projectHelper.attachArtifact(getProject(), "war-overlay", "webcontent", warArchive);
	}

	protected void attachClassesArtifact(File classesArchive) {
        Artifact artifact = getProject().getArtifact();
        artifact.setFile( classesArchive );
	}

	protected File getTargetClassesFile() {
        return getTargetFile(new File(getOutputDirectory()), getWarName(), null, "jar");
	}

	protected File getTargetWarFile() {
        return getTargetFile(new File(getOutputDirectory()), getWarName(), "webcontent", "zip");
	}

	protected boolean hasClassesArchive() {
		return true;
	}

	protected boolean isWebXmlRequired() {
		return false;
	}
	
	@Override
	protected List<String> getImplicitPackagingExcludes() {
		return Collections.singletonList("WEB-INF/classes/**");
	}

}
