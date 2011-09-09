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

import org.apache.maven.artifact.Artifact;

/**
 * Build a WAR file.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 * @goal war
 * @phase package
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class WarMojo
    extends PackagedWarMojo
{
    /**
     * Classifier to add to the generated WAR. 
     * If provided an artifact will be attached with this classifier instead of the WAR being the default artifact.
     *
     * @parameter
     */
    private String classifier;

    /**
     * The classifier to use for the attached classes artifact.
     *
     * @parameter default-value="classes"
     * @since 2.1-alpha-2
     */
    private String classesClassifier = "classes";

    /**
     * Whether this is the main artifact being built. Set to <code>false</code> if you don't want to install or
     * deploy it to the local repository instead of the default one in an execution.
     *
     * @parameter expression="${primaryArtifact}" default-value="true"
     */
    private boolean primaryArtifact = true;

    /**
     * Whether or not to fail the build if the <code>web.xml</code> file is missing. Set to <code>false</code>
     * if you want you WAR built without a <code>web.xml</code> file.
     * This may be useful if you are building an overlay that has no web.xml file.
     *
     * @parameter expression="${failOnMissingWebXml}" default-value="true"
     * @since 2.1-alpha-2
     */
    private boolean failOnMissingWebXml = true;

    /**
     * Whether classes (that is the content of the WEB-INF/classes directory) should be attached to the
     * project.
     *
     * @parameter default-value="false"
     * @since 2.1-alpha-2
     */
    private boolean attachClasses = false;

	protected void attachWarArtifact(File warArchive) {
        if ( classifier != null )
        {
            projectHelper.attachArtifact( getProject(), "war", classifier, warArchive );
        }
        else
        {
            Artifact artifact = getProject().getArtifact();
            if ( primaryArtifact || artifact.getFile() == null || artifact.getFile().isDirectory() )
            {
                artifact.setFile( warArchive );
            }
        }
	}
	
	protected void attachClassesArtifact(File classesArchive) {
		projectHelper.attachArtifact(getProject(), "jar",
				classesClassifier, classesArchive);
	}
	
	protected File getTargetWarFile()
    {
        return getTargetFile( new File( getOutputDirectory() ), getWarName(), getClassifier(), "war" );

    }

    protected File getTargetClassesFile()
    {
        return getTargetFile( new File( getOutputDirectory() ), getWarName(), getClassesClassifier(), "jar" );
    }
    
	protected boolean hasClassesArchive() {
		return attachClasses;
	}

	protected boolean isWebXmlRequired() {
		if (failOnMissingWebXml) {
			return true;
		}
		getLog().debug( "Build won't fail if web.xml file is missing." );
		return false;
	}
    
    public String getClassifier()
    {
        return classifier;
    }

    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    public boolean isPrimaryArtifact()
    {
        return primaryArtifact;
    }

    public void setPrimaryArtifact( boolean primaryArtifact )
    {
        this.primaryArtifact = primaryArtifact;
    }

    public boolean isAttachClasses()
    {
        return attachClasses;
    }

    public void setAttachClasses( boolean attachClasses )
    {
        this.attachClasses = attachClasses;
    }

    public String getClassesClassifier()
    {
        return classesClassifier;
    }

    public void setClassesClassifier( String classesClassifier )
    {
        this.classesClassifier = classesClassifier;
    }

    public boolean isFailOnMissingWebXml()
    {
        return failOnMissingWebXml;
    }

    public void setFailOnMissingWebXml( boolean failOnMissingWebXml )
    {
        this.failOnMissingWebXml = failOnMissingWebXml;
    }

}
