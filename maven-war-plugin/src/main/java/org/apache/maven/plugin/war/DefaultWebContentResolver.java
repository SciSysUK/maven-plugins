package org.apache.maven.plugin.war;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.war.overlay.WebContentResolver;

/**
 * Default implementation of {@link WebContentResolver}.
 * 
 * @author Alex Clarke
 * @author Phillip Webb
 *
 */
public class DefaultWebContentResolver implements WebContentResolver {
	
	private ArtifactFactory artifactFactory;
	
	private ArtifactResolver artifactResolver;
	
	private ArtifactRepository localArtifactRepository;
	
	private List remoteArtifactRepositories;
	
	DefaultWebContentResolver(ArtifactFactory artifactFactory, ArtifactResolver artifactResolver, ArtifactRepository localArtifactRepository, List remoteArtifactRepositories) {
		this.artifactFactory = artifactFactory;
		this.artifactResolver = artifactResolver;
		this.localArtifactRepository = localArtifactRepository;
		this.remoteArtifactRepositories = remoteArtifactRepositories;
	}

	public Artifact getResolvedWebContent(Artifact webClassesArtifact) throws MojoExecutionException {
		Artifact newArtifact = artifactFactory.createArtifactWithClassifier(
				webClassesArtifact.getGroupId(), webClassesArtifact.getArtifactId(),
				webClassesArtifact.getVersion(), "war-overlay", "webcontent");

		newArtifact.setScope(webClassesArtifact.getScope());

		try {
			artifactResolver.resolve(newArtifact, remoteArtifactRepositories, localArtifactRepository);
		} catch (AbstractArtifactResolutionException e) {
			throw new MojoExecutionException("not found in any repository: "
					+ webClassesArtifact.getId(), e);
		}

		return newArtifact;
	}
}