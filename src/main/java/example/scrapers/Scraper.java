package example.scrapers;

import example.Event;

import java.io.IOException;
import java.util.List;

public interface Scraper {

    List<Event> getEvents() throws IOException;

    String getUrl();
}
