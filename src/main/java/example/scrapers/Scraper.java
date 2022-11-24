package example.scrapers;

import example.Event;

import java.io.IOException;
import java.util.List;

/**
 * All Scrapers should implement this interface.
 */
public interface Scraper {

    /**
     * Gets all events the Scraper can find.
     * @return list of Events
     * @throws IOException
     */
    List<Event> getEvents() throws IOException;

    String getUrl();
}
