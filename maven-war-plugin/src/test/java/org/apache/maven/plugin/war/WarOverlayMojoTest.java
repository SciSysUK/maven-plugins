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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.war.stub.MavenProjectBasicStub;
import org.apache.maven.plugin.war.stub.ProjectHelperStub;
import org.apache.maven.project.MavenProject;

/**
 * Unit test of the extra functionality provided in {@link WarOverlayMojo}
 */
public class WarOverlayMojoTest extends TestCase
{
    private static final String TEST_FILE = "test-file";
	private static final String TEST_DIRECTORY = "test-dir";
	private static final String SEPERATOR = System.getProperty("file.separator");

	private WarOverlayMojo mojo = new WarOverlayMojo();
    
	private MavenProject project;
	
	private ProjectHelperStub projectHelper = new ProjectHelperStub();
    
    public void setUp() throws Exception
    {
		project = new MavenProjectBasicStub();
    	project.setArtifact(new DefaultArtifact("test", "test", VersionRange.createFromVersion("1.0"), "", "", "", null));
		mojo.setProject(project);
		mojo.setProjectHelper(projectHelper);
    }
    
    public void testAttachClassesArtifact()
    {
    	File classesArchive = new File("classesFile");
    	
		mojo.attachClassesArtifact(classesArchive);
		
		assertEquals(project.getArtifact().getFile(), classesArchive);
    }
    
    public void testAttachWarArtifact()
    {
    	File warArchive = new File("warFile");
    	
    	mojo.attachWarArtifact(warArchive);
    	
    	assertEquals("war-overlay", projectHelper.getArtifactType());
    	assertEquals("webcontent", projectHelper.getArtifactClassifier());
    	assertEquals(warArchive, projectHelper.getArtifactFile());
    	
    }
    
    public void testGetTargetClassesFile()
    {
    	mojo.setOutputDirectory(TEST_DIRECTORY);
    	mojo.setWarName(TEST_FILE);
    	
    	File targetClassesFile = mojo.getTargetClassesFile();
    	
     	assertEquals(TEST_DIRECTORY + SEPERATOR + TEST_FILE +".jar", targetClassesFile.getPath());
    }
    
    public void testGetTargetWarFile()
    {
    	mojo.setOutputDirectory(TEST_DIRECTORY);
    	mojo.setWarName(TEST_FILE);
    	
    	File targetClassesFile = mojo.getTargetWarFile();
    	
     	assertEquals(TEST_DIRECTORY + SEPERATOR + TEST_FILE +"-webcontent.zip", targetClassesFile.getPath());
    }
    

}

