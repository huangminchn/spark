/*
 * Copyright 2014 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.resource;

import java.net.MalformedURLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.URIUtil;

import spark.utils.Assert;

/**
 * Locates resources in classpath
 * Created by Per Wendel on 2014-05-18.
 */
public class ClassPathResourceHandler extends AbstractResourceHandler {

    private final String baseResource;
    private String welcomeFile;

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     */
    public ClassPathResourceHandler(String baseResource) {
        this(baseResource, null);
    }

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     * @param welcomeFile  the welcomeFile
     */
    public ClassPathResourceHandler(String baseResource, String welcomeFile) {
        Assert.notNull(baseResource);
        this.baseResource = baseResource;
        this.welcomeFile = welcomeFile;
    }

    @Override
    protected AbstractFileResolvingResource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        try {
            path = URIUtil.canonicalPath(path);

            final String addedPath = addPaths(baseResource, path);

            ClassPathResource resource = new ClassPathResource(addedPath);

            if (resource.exists() && resource.getFile().isDirectory()) {
                if (welcomeFile != null) {
                    resource = new ClassPathResource(addPaths(resource.getPath(), welcomeFile));
                } else {
                    //  No welcome file configured, serve nothing since it's a directory
                    resource = null;
                }
            }

            return (resource != null && resource.exists()) ? resource : null;
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }

}
