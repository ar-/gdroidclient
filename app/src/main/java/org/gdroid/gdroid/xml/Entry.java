package org.gdroid.gdroid.xml;

public class Entry {
    public final String title;
    public final String link;
    public final String summary;

    public Entry(String title, String summary, String link) {
        this.title = title;
        this.summary = summary;
        this.link = link;
    }
}
