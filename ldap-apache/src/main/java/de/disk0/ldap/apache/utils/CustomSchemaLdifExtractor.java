/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package de.disk0.ldap.apache.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.schema.extractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.extractor.UniqueResourceException;
import org.apache.directory.api.ldap.schema.extractor.impl.ResourceMap;
import org.apache.directory.api.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;


/**
 * Extracts LDIF files for the schema repository onto a destination directory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CustomSchemaLdifExtractor implements SchemaLdifExtractor
{

    /** The base path. */
    private static final String BASE_PATH = "";

    /** The schema sub-directory. */
    private static final String SCHEMA_SUBDIR = "schema";

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( CustomSchemaLdifExtractor.class );

    /**
     * The pattern to extract the schema from LDIF files.
     * java.util.regex.Pattern is immutable so only one instance is needed for all uses.
     */
    private static final Pattern EXTRACT_PATTERN = Pattern.compile( ".*custom_schema" + "[/\\Q\\\\E]" + "ou=schema.*\\.ldif" );

    /** The extracted flag. */
    private boolean extracted;

    /** The output directory. */
    private File outputDirectory;

    /** The schema directory. */
    private File schemaDirectory;


    /**
     * Creates an extractor which deposits files into the specified output
     * directory.
     *
     * @param outputDirectory the directory where the schema root is extracted
     */
    public CustomSchemaLdifExtractor( File outputDirectory )
    {
        LOG.debug( "BASE_PATH set to {}, outputDirectory set to {}", BASE_PATH, outputDirectory );
        this.outputDirectory = outputDirectory;
        this.schemaDirectory = new File( outputDirectory, SCHEMA_SUBDIR );

        if ( !outputDirectory.exists() )
        {
            LOG.debug( "Creating output directory: {}", outputDirectory );
            if ( !outputDirectory.mkdir() )
            {
                LOG.error( "Failed to create outputDirectory: {}", outputDirectory );
            }
        }
        else
        {
            LOG.debug( "Output directory exists: no need to create." );
        }

        if ( !schemaDirectory.exists() )
        {
            LOG.info( "Schema directory '{}' does NOT exist: extracted state set to false.", schemaDirectory );
            extracted = false;
        }
        else
        {
            LOG.info( "Schema directory '{}' does exist: extracted state set to true.", schemaDirectory );
            extracted = true;
        }
    }


    /**
     * Gets whether or not schema folder has been created or not.
     *
     * @return true if schema folder has already been extracted.
     */
    public boolean isExtracted()
    {
        return extracted;
    }


    /**
     * Extracts the LDIF files from a Jar file or copies exploded LDIF resources.
     *
     * @param overwrite over write extracted structure if true, false otherwise
     * @throws IOException if schema already extracted and on IO errors
     */
    public void extractOrCopy( boolean overwrite ) throws IOException
    {
    	
    	
        if ( !outputDirectory.exists() && !outputDirectory.mkdirs() )
        {
            throw new IOException( I18n.err( I18n.ERR_09001_DIRECTORY_CREATION_FAILED, outputDirectory
                .getAbsolutePath() ) );
        }

        File schemaDirectory = new File( outputDirectory, SCHEMA_SUBDIR );

        if ( !schemaDirectory.exists() )
        {
            if ( !schemaDirectory.mkdirs() )
            {
                throw new IOException( I18n.err( I18n.ERR_09001_DIRECTORY_CREATION_FAILED, schemaDirectory.getAbsolutePath() ) );
            }
        }
        else if ( !overwrite )
        {
            return;
        }

    	ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();   
    	Resource[] resources = patternResolver.getResources("classpath*:custom_schema/**/*.ldif");
    	for(Resource r : resources) {
    		File f = new File(schemaDirectory.getAbsolutePath()+"/"+r.getURL().toString().replaceAll(".*/custom_schema/", ""));
    		f.getParentFile().mkdirs();
    		FileOutputStream fos = null;
    		try {
				fos = new FileOutputStream(f);
				IOUtils.copy(r.getInputStream(), fos);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(fos);
			}
    	}
    	
    }

 
    /**
     * Extracts the LDIF files from a Jar file or copies exploded LDIF
     * resources without overwriting the resources if the schema has
     * already been extracted.
     *
     * @throws IOException if schema already extracted and on IO errors
     */
    public void extractOrCopy() throws IOException
    {
        extractOrCopy( false );
    }


    /**
     * Gets the unique schema file resource from the class loader off the base path.  If 
     * the same resource exists multiple times then an error will result since the resource
     * is not unique.
     *
     * @param resourceName the file name of the resource to load
     * @param resourceDescription human description of the resource
     * @return the InputStream to read the contents of the resource
     * @throws IOException if there are problems reading or finding a unique copy of the resource
     */
    public static InputStream getUniqueResourceAsStream( String resourceName, String resourceDescription )
        throws IOException
    {
        resourceName = BASE_PATH + resourceName;
        URL result = getUniqueResource( resourceName, resourceDescription );
        return result.openStream();
    }


    /**
     * Gets a unique resource from the class loader.
     * 
     * @param resourceName the name of the resource
     * @param resourceDescription the description of the resource
     * @return the URL to the resource in the class loader
     * @throws IOException if there is an IO error
     */
    public static URL getUniqueResource( String resourceName, String resourceDescription ) throws IOException
    {
        Enumeration<URL> resources = CustomSchemaLdifExtractor.class.getClassLoader().getResources( resourceName );
        if ( !resources.hasMoreElements() )
        {
            throw new UniqueResourceException( resourceName, resourceDescription );
        }
        URL result = resources.nextElement();
        if ( resources.hasMoreElements() )
        {
            throw new UniqueResourceException( resourceName, result, resources, resourceDescription );
        }
        return result;
    }


    /**
     * Extracts the LDIF schema resource from class loader.
     *
     * @param resource the LDIF schema resource
     * @throws IOException if there are IO errors
     */
    private void extractFromClassLoader( String resource ) throws IOException
    {
        byte[] buf = new byte[512];
        InputStream in = CustomSchemaLdifExtractor.getUniqueResourceAsStream( resource,
            "LDIF file in schema repository" );

        try
        {
            File destination = new File( outputDirectory, resource );

            /*
             * Do not overwrite an LDIF file if it has already been extracted.
             */
            if ( destination.exists() )
            {
                return;
            }

            if ( !destination.getParentFile().exists() && !destination.getParentFile().mkdirs() )
            {
                throw new IOException( I18n.err( I18n.ERR_09001_DIRECTORY_CREATION_FAILED, destination
                    .getParentFile().getAbsolutePath() ) );
            }

            FileOutputStream out = new FileOutputStream( destination );
            try
            {
                while ( in.available() > 0 )
                {
                    int readCount = in.read( buf );
                    out.write( buf, 0, readCount );
                }
                out.flush();
            }
            finally
            {
                out.close();
            }
        }
        finally
        {
            in.close();
        }
    }
}