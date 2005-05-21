package org.apache.maven.plugin.ear;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * A <tt>Mojo</tt> used to build the <tt>application.xml</tt> file
 * if necessary.
 *
 * @author <a href="stephane.nicoll@gmail.com">Stephane Nicoll</a>
 * @version $Id $
 * @goal generate-application-xml
 * @phase process-resources
 * @requiresDependencyResolution test
 * @description generates the application.xml deployment descriptor
 */
public class GenerateApplicationXmlMojo
    extends AbstractEarMojo
{

    public static final String VERSION_1_3 = "1.3";

    public static final String VERSION_1_4 = "1.4";

    public static final String UTF_8 = "UTF-8";

    /**
     * Inserts the doctype header depending on the specified version.
     *
     * @parameter expression="${maven.ear.appxml.version}"
     */
    private String version = VERSION_1_3;

    /**
     * Display name of the application to be used when <tt>application.xml</tt>
     * file is autogenerated.
     *
     * @parameter expression="${project.artifactId}"
     */
    private String displayName = null;

    /**
     * The description in generated <tt>application.xml</tt>.
     *
     * @parameter
     */
    private String description = null;

    /**
     * Character encoding for the auto-generated <tt>application.xml</tt> file.
     *
     * @parameter
     * @TODO handle this field
     */
    private String encoding = UTF_8;

    /**
     * Directory where the <tt>application.xml</tt> file will be auto-generated.
     *
     * @parameter expression="${project.build.directory}"
     */
    private String generatedDescriptorLocation;

    public void execute()
        throws MojoExecutionException
    {
        getLog().debug( " ======= GenerateApplicationXmlMojo settings =======" );
        getLog().debug( "version[" + version + "]" );
        getLog().debug( "displayName[" + displayName + "]" );
        getLog().debug( "description[" + description + "]" );
        getLog().debug( "encoding[" + encoding + "]" );
        getLog().debug( "generatedDescriptorLocation[" + generatedDescriptorLocation + "]" );

        // Generate deployment descriptor
        try
        {
            getLog().info( "Generating application.xml" );
            generateDeploymentDescriptor();
            FileUtils.copyFileToDirectory( new File( generatedDescriptorLocation, "application.xml" ),
                                           new File( getBuildDir(), "META-INF" ) );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to generate application.xml", e );
        }
    }

    /**
     * Generates the deployment descriptor if necessary.
     *
     * @throws IOException
     */
    protected void generateDeploymentDescriptor()
        throws IOException
    {
        File outputDir = new File( generatedDescriptorLocation );
        if ( !outputDir.exists() )
        {
            outputDir.mkdir();
        }

        File descriptor = new File( outputDir, "application.xml" );
        if ( !descriptor.exists() )
        {
            descriptor.createNewFile();
        }

        ApplicationXmlWriter writer = new ApplicationXmlWriter( version );
        try
        {
            writer.write( descriptor, getModules(), displayName, description );
        }
        catch ( EarPluginException e )
        {
            throw new IOException( "Unable to generate application.xml[" + e.getMessage() + "]" );
        }
    }
}
