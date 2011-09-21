package org.apache.maven.plugin.war.overlay;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Stratergy interface used to resolve a web content {@link Artifact} from a provided Web Classess {@link Artifact}.
 * 
 * @author Alex Clarke
 * @author Phillip Webb
 * 
 */
public interface WebContentResolver {

	/**
	 * Resolve the web content {@link Artifact} for the provided web classes {@link Artifact}
	 * @param webClassesArtifact
	 * @return
	 * @throws MojoExecutionException
	 */
	public Artifact getResolvedWebContent(Artifact webClassesArtifact) throws MojoExecutionException;

}
