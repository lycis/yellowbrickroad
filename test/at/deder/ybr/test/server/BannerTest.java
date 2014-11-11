package at.deder.ybr.test.server;

import at.deder.ybr.server.Banner;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for the Banner bean.
 * @author lycis
 */
public class BannerTest {
    
    /**
     * Check if setting and getting the banner text works.
     */
    @Test
    public void testText() {
        final String BANNER_TEXT = "test banner text";
        Banner banner = new Banner();
        banner.setText(BANNER_TEXT);
        assertEquals(banner.getText(), BANNER_TEXT);
    }
    
    /**
     * Check if setting and getting the banner text with linebreaks works.
     */
    @Test
    public void testTextLineBreak() {
        final String BANNER_TEXT = "test banner text\nwith line break";
        Banner banner = new Banner();
        banner.setText(BANNER_TEXT);
        assertEquals(banner.getText(), BANNER_TEXT);
    }
}
