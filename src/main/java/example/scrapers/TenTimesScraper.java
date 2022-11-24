package example.scrapers;

import example.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TenTimesScraper implements Scraper{

    private static final String URL = "https://10times.com/london-uk/climate-change";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public List<Event> getEvents() throws IOException {

        List<Event> scrapedEvents = new ArrayList<>();
        var page = Jsoup.connect(URL).get();

        var events = page.getElementById("content");
        if (events == null) {
            return Collections.emptyList();
        }

        for (var row : events.children()) {
            if (row.hasClass("event-card")) {
                var event = toEvent(row);
                scrapedEvents.add(event);
            }
        }
        return scrapedEvents;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    private Event toEvent(Element eventRow) {
        var dateInfo = eventRow.childNode(0);
        var eventNameInfo = eventRow.childNode(1);
        var locationInfo = eventRow.childNode(2);

        var startDate = extractStartDate(dateInfo);
        var endDate = extractEndDate(dateInfo);
        var name = extractName(eventNameInfo);
        var location = extractLocation(locationInfo);

        var event = new Event();
        event.setName(name);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setLocation(location);

        return event;
    }

    private LocalDate extractStartDate(Node dateInfo) {
        Node eventTimeDiv = null;
        for (var child : dateInfo.childNodes()) {
            if (child.attr("class").contains("eventTime")) {
                eventTimeDiv = child;
            }
        }
        if (eventTimeDiv != null) {
            var date = eventTimeDiv.attr("data-start-date");
            return LocalDate.parse(date, DATE_TIME_FORMATTER);
        } else {
            return null;
        }
    }

    private LocalDate extractEndDate(Node dateInfo) {
        Node eventTimeDiv = null;
        for (var child : dateInfo.childNodes()) {
            if (child.attr("class").contains("eventTime")) {
                eventTimeDiv = child;
            }
        }
        if (eventTimeDiv != null) {
            var date = eventTimeDiv.attr("data-end-date");
            return LocalDate.parse(date, DATE_TIME_FORMATTER);
        } else {
            return null;
        }
    }

    private String extractName(Node eventNameInfo) {
        var attributes = eventNameInfo.childNode(0).childNode(0).attributes();
        return attributes.get("data-ga-label");
    }

    private String extractLocation(Node locationInfo) {
        var node = locationInfo.childNode(0);
        TextNode textNode = null;
        for (var n : node.childNodes()) {
            if (n instanceof TextNode) {
                textNode = (TextNode) n;
            }
        }
        return textNode != null ? textNode.text() : null;
    }
}
