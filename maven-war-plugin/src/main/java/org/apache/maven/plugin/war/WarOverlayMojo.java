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

	protected void attachWarArtifact( File warArchive ) {
        projectHelper.attachArtifact( getProject(), "war-overlay", "webcontent", warArchive );
	}

	protected void attachClassesArtifact( File classesArchive ) {
        Artifact artifact = getProject().getArtifact();
        artifact.setFile( classesArchive );
	}

	protected File getTargetClassesFile() {
        return getTargetFile( new File( getOutputDirectory() ), 
        		getWarName(), null, "jar" );
	}

	protected File getTargetWarFile() {
        return getTargetFile( new File( getOutputDirectory() ), 
        		getWarName(), "webcontent", "zip" );
	}

	protected boolean hasClassesArchive() {
		return true;
	}

	protected boolean isWebXmlRequired() {
		return false;
	}
	
	protected List getImplicitPackagingExcludes() {
		return Collections.singletonList( "WEB-INF/classes/**" );
	}
	
	protected boolean getPackageArtifacts() {
		return false;
	}
}
