package org.apache.maven.plugin.war;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.war.util.ClassesPackager;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.war.WarArchiver;
import org.codehaus.plexus.util.StringUtils;

public abstract class PackagedWarMojo extends AbstractWarMojo {

	/**
	 * The directory for the generated WAR.
	 *
	 * @parameter default-value="${project.build.directory}"
	 * @required
	 */
	private String outputDirectory;
	
	/**
	 * The name of the generated WAR.
	 *
	 * @parameter default-value="${project.build.finalName}"
	 * @required
	 */
	private String warName;
	
	/**
	 * The comma separated list of tokens to exclude from the WAR before
	 * packaging. This option may be used to implement the skinny WAR use
	 * case.
	 *
	 * @parameter
	 * @since 2.1-alpha-2
	 */
	private String packagingExcludes;
	
	/**
	 * The comma separated list of tokens to include in the WAR before
	 * packaging. By default everything is included. This option may be used
	 * to implement the skinny WAR use case.
	 *
	 * @parameter
	 * @since 2.1-beta-1
	 */
	private String packagingIncludes;
	
	/**
	 * The WAR archiver.
	 *
	 * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="war"
	 */
	protected WarArchiver warArchiver;
	
	/**
	 * @component
	 */
	protected MavenProjectHelper projectHelper;
	
	private ClassesPackager packager = new ClassesPackager();
	
    /**
     * Executes the PackagedWarMojo on the current project.
     *
     * @throws MojoExecutionException if an error occurred while building the webapp
     */
	public final void execute() throws MojoExecutionException, MojoFailureException {

        try
        {
            packageAndAttachArtifacts();
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( "Error assembling WAR: " + e.getMessage(), e );
        }
        catch ( ManifestException e )
        {
            throw new MojoExecutionException( "Error assembling WAR", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error assembling WAR", e );
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "Error assembling WAR: " + e.getMessage(), e );
        }
		
	}

	private void packageAndAttachArtifacts() throws DependencyResolutionRequiredException, ManifestException, IOException, ArchiverException, MojoExecutionException, MojoFailureException {
        buildExplodedWebapp( getWebappDirectory() );
        
		File warArchive = createWarArchive();
		attachWarArtifact( warArchive );

		if ( hasClassesArchive() ) {
			File classesArchive = createClassesArchive();
			if ( classesArchive != null ) {
				attachClassesArtifact( classesArchive );
			}
		}
	}

	private File createWarArchive() throws MojoExecutionException, MojoFailureException, ArchiverException, ManifestException, IOException, DependencyResolutionRequiredException {
        File file = getTargetWarFile();    
		configureWarArchiver();
		MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( warArchiver );
		archiver.setOutputFile( file );
        archiver.createArchive( getProject(), getArchive() );
		return file;
	}

	private File createClassesArchive() throws MojoExecutionException {
		File directory = packager.getClassesDirectory( getWebappDirectory() );
		if ( !directory.exists() ) {
			return null;
		}
		File file = getTargetClassesFile();
		getLog().info("Packaging classes");
		packager.packageClasses( directory, file, getJarArchiver(),
				getProject(), getArchive() );
		return file;
	}
	
	protected abstract void attachWarArtifact( File warArchive );
	
	protected abstract void attachClassesArtifact( File classesArchive );

	private void configureWarArchiver() throws ArchiverException {
        warArchiver.addDirectory( getWebappDirectory(), getPackagingIncludes(), getPackagingExcludes() );
        final File webXmlFile = new File( getWebappDirectory(), "WEB-INF/web.xml" );
        if ( webXmlFile.exists() )
        {
            warArchiver.setWebxml( webXmlFile );
        }
        // setIgnoreWebxml is misleading it actually indicates whether a web.xml is required
        warArchiver.setIgnoreWebxml( isWebXmlRequired() );
	}

	public String[] getPackagingExcludes() {
		List excludes = new ArrayList();
		excludes.addAll( getDefinedPackagingExcludes() );
		excludes.addAll( getImplicitPackagingExcludes() );
		return (String[])excludes.toArray( new String[excludes.size()] );
	}

	private List getDefinedPackagingExcludes() {
		if (StringUtils.isEmpty( packagingExcludes )) {
			return Collections.EMPTY_LIST;
		}
		return Arrays.asList( StringUtils.split( packagingExcludes, "," ) );
	}

	protected List getImplicitPackagingExcludes() {
		return Collections.EMPTY_LIST;
	}


	public String[] getPackagingIncludes() {
		if (StringUtils.isEmpty( packagingIncludes) ) {
			return new String[] { "**" };
		}
		return StringUtils.split( packagingIncludes, "," );
	}
	
	protected final File getTargetFile( File basedir, String finalName,
			String classifier, String extension ) {
		if ( classifier == null ) {
			classifier = "";
		} else if ( classifier.trim().length() > 0
				&& !classifier.startsWith( "-" ) ) {
			classifier = "-" + classifier;
		}

		return new File( basedir, finalName + classifier + "." + extension );
	}
	
	/**
	 * Get the Target {@link File} for the classes archive.
	 * 
	 * @return the {@link File}
	 */
	protected abstract File getTargetClassesFile();
	
	/**
	 * Get the Target {@link File} for the War archive.
	 * @return
	 */
	protected abstract File getTargetWarFile();

	protected abstract boolean hasClassesArchive();
	
	protected abstract boolean isWebXmlRequired();

	public void setPackagingIncludes( String packagingIncludes ) {
	    this.packagingIncludes = packagingIncludes;
	}

	public void setPackagingExcludes( String packagingExcludes ) {
	    this.packagingExcludes = packagingExcludes;
	}

	public String getOutputDirectory() {
	    return outputDirectory;
	}

	public void setOutputDirectory( String outputDirectory ) {
	    this.outputDirectory = outputDirectory;
	}

	public String getWarName() {
	    return warName;
	}

	public void setWarName( String warName ) {
	    this.warName = warName;
	}

	public WarArchiver getWarArchiver() {
	    return warArchiver;
	}

	public void setWarArchiver( WarArchiver warArchiver ) {
	    this.warArchiver = warArchiver;
	}

	public MavenProjectHelper getProjectHelper() {
	    return projectHelper;
	}

	public void setProjectHelper( MavenProjectHelper projectHelper ) {
	    this.projectHelper = projectHelper;
	}

}
