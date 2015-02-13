package de.l3s.boilerpipe.sax;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;

/**
 * Created by Robby on 2/12/2015.
 */
public class OpenGraphExtractorTest {
    
    @Test
    public void shouldGetOpenGraphTags() throws IOException {
        final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        final OpenGraphExtractor ie = OpenGraphExtractor.INSTANCE;

        Map<String, String> tags = ie.process(IOUtils.toString(OpenGraphExtractorTest.class.getResourceAsStream("/opengraph1.html")), extractor);
        
        assertThat(tags, Matchers.notNullValue());
        assertThat(tags, allOf(hasEntry("og:title", "The Rock (1996)"), hasEntry("og:site_name", "IMDb"), 
                hasEntry("og:description", "Directed by Michael Bay.  With Sean Connery, Nicolas Cage, Ed Harris, John Spencer. A mild-mannered chemist and an ex-con must lead the counterstrike when a rogue group of military men, led by a renegade general, threaten a nerve gas attack from Alcatraz against San Francisco."),
                hasEntry("og:url", "http://www.imdb.com/title/tt0117500/"), hasEntry("og:type", "video.movie"), hasEntry("og:image", "http://ia.media-imdb.com/images/M/MV5BMTM3MTczOTM1OF5BMl5BanBnXkFtZTYwMjc1NDA5._V1_.jpg")));
    }
}
