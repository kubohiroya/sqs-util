package net.sqs2.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Implementation class for class: URI resolution.
 * NOTE: this code is duplicate in {@link ClassURLConnection},
 * should probably unify the two.
 * 
 *
 */
class ClassURLHelper
{

    private ClassLoader classLoader;

    public ClassURLHelper() {
        //
    }

    public ClassURLHelper(ClassLoader classLoader) {
        setClassLoader(classLoader);
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Retrieves an input stream for a <code>class:</code> pseudo URI, returns
     * <code>null</code> if no resource available or the passed URI is  
     * not a <code>class:</code> URI.
     * 
     * @param urlString the <code>class:</code> URI
     * @return A {@link InputStream}, or <code>null</code>.
     * @throws IOException
     */
    public InputStream getInputStream(String urlString) throws IOException {
        final URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IOException("Error parsing url [" + urlString
                + "]: "
                + e.getMessage(), e);
        }
        final String scheme = uri.getScheme();
        if ("class".equals(scheme)) {

            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            final ClassLoader localCl = this.classLoader == null ? this.getClass()
                .getClassLoader()
                : this.classLoader;
            // I doubt this may happen because or SecurityException
            // is thrown or for the classloader to be null this class should 
            // have been loaded by the bootstrap class loader
            if (localCl == null) {
                String message = "Cannot load : " + scheme + " / " + path;
                Logger.getLogger(getClass().getName()).severe(message);
                throw new IOException(message);
            }
            return localCl.getResourceAsStream(path);
        } else {
            // not a class: URI
            return null;
        }
    }

}