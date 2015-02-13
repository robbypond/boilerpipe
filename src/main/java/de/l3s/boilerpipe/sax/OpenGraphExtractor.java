package de.l3s.boilerpipe.sax;


import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OpenGraphExtractor {
    public static final OpenGraphExtractor INSTANCE = new OpenGraphExtractor();

    /**
     * Returns the singleton instance of {@link de.l3s.boilerpipe.sax.ImageExtractor}.
     *
     * @return
     */
    public static OpenGraphExtractor getInstance() {
        return INSTANCE;
    }

    private OpenGraphExtractor() {
    }

    /**
     * Processes the given {@link de.l3s.boilerpipe.document.TextDocument} and the original HTML text (as a
     * String).
     *
     * @param doc
     *            The processed {@link de.l3s.boilerpipe.document.TextDocument}.
     * @param origHTML
     *            The original HTML document.
     * @return A Map of Open Graph key-value pairs
     * @throws de.l3s.boilerpipe.BoilerpipeProcessingException if an error during extraction occure
     */
    public Map<String, String> process(final TextDocument doc, final String origHTML)
            throws BoilerpipeProcessingException {
        return process(doc, new InputSource(new StringReader(origHTML)));
    }

    /**
     * Processes the given {@link TextDocument} and the original HTML text (as an
     * {@link org.xml.sax.InputSource}).
     *
     * @param doc
     *            The processed {@link TextDocument}.
     * @return A List of enclosed {@link de.l3s.boilerpipe.document.Image}s
     * @throws BoilerpipeProcessingException
     */
    public Map<String, String> process(final TextDocument doc, final InputSource is)
            throws BoilerpipeProcessingException {
        final Implementation implementation = new Implementation();
        implementation.process(doc, is);

        return implementation.tags;
    }

    /**
     * Fetches the given {@link java.net.URL} using {@link de.l3s.boilerpipe.sax.HTMLFetcher} and processes the
     * retrieved HTML using the specified {@link de.l3s.boilerpipe.BoilerpipeExtractor}.
     * @param url the url of the document to fetch
     * @param extractor extractor to use
     *
     * @return A List of enclosed {@link de.l3s.boilerpipe.document.Image}s
     * @throws java.io.IOException
     * @throws BoilerpipeProcessingException
     * @throws org.xml.sax.SAXException
     */
    @SuppressWarnings("javadoc")
    public Map<String, String> process(final URL url, final BoilerpipeExtractor extractor)
            throws IOException, BoilerpipeProcessingException, SAXException {
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);

        final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource())
                .getTextDocument();
        extractor.process(doc);

        final InputSource is = htmlDoc.toInputSource();

        return process(doc, is);
    }

    /**
     * parses the media (picture, video) out of doc
     * @param doc document to parse the media out
     * @param extractor extractor to use
     * @return list of extracted media, with size = 0 if no media found
     */
    public Map<String, String> process(String doc, final BoilerpipeExtractor extractor) {
        final HTMLDocument htmlDoc = new HTMLDocument(doc);
        Map<String, String> tags;
        try {
            TextDocument tdoc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            extractor.process(tdoc);
            final InputSource is = htmlDoc.toInputSource();
            tags = process(tdoc, is);
        } catch (Exception e) {
            return null;
        }
        return tags;
    }

    private final class Implementation extends AbstractSAXParser implements
            ContentHandler {

        public Map<String,String> tags = new HashMap<String, String>();

        Implementation() {
            super(new HTMLConfiguration());
            setContentHandler(this);
        }

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {

        }

        @Override
        public void startPrefixMapping(String s, String s1) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String s) throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException {
            if(localName.startsWith("META") && attributes.getValue("property") != null && 
                    (attributes.getValue("property").startsWith("og:") || attributes.getValue("property").startsWith("article:"))) {
                tags.put(attributes.getValue("property"), attributes.getValue("content"));
            }
        }

        @Override
        public void endElement(String s, String s1, String s2) throws SAXException {

        }

        @Override
        public void characters(char[] chars, int i, int i1) throws SAXException {
            
        }

        @Override
        public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {

        }

        @Override
        public void processingInstruction(String s, String s1) throws SAXException {

        }

        @Override
        public void skippedEntity(String s) throws SAXException {

        }

        public void process(TextDocument doc, InputSource is) throws BoilerpipeProcessingException {
            try {
                parse(is);
            } catch (SAXException e) {
                throw new BoilerpipeProcessingException(e);
            } catch (IOException e) {
                throw new BoilerpipeProcessingException(e);
            }
        }
    }
}
