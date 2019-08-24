package net.sqs2.xslt;

import javax.xml.transform.URIResolver;

/**
 * A base class for {@link URIResolver} that
 * may defer resolution to a parent {@link URIResolver}
 * that must be configured in constructor. 
 */
public abstract class FilterURIResolver implements URIResolver
{

    protected final URIResolver parent;
    /**
     * Subclasses only constructor.
     * @param parent A {@link URIResolver}, never <code>null</code>.
     */
    protected FilterURIResolver(URIResolver parent)
    {
        super();
        this.parent = parent;
        if (parent == null)
            throw new IllegalArgumentException("null parent resolver");
    }

}