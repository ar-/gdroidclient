package org.gdroid.gdroid.xml;

import android.util.Xml;

import org.gdroid.gdroid.beans.ApplicationBean;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FDroidRepoXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "fdroid");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("application")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private ApplicationBean readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "application");
        String id = null;
        String title = null;
        String summary = null;
        String link = null;
        ApplicationBean ret = new ApplicationBean();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                ret.name = readTag(parser,"name");
            } else if (name.equals("id")) {
                ret.id = readTag(parser,"id");
            } else if (name.equals("lastupdated")) {
                ret.lastupdated = readTag(parser,"lastupdated");
            } else if (name.equals("summary")) {
                ret.summary = readTag(parser,"summary");
            } else if (name.equals("added")) {
                ret.added = readTag(parser,"added");
            } else if (name.equals("icon")) {
                ret.icon = readTag(parser,"icon");
            } else if (name.equals("desc")) {
                ret.desc = readTag(parser,"desc");
            } else if (name.equals("license")) {
                ret.license = readTag(parser,"license");
            } else if (name.equals("categories")) {
                ret.categories = readTag(parser,"categories");
            } else if (name.equals("web")) {
                ret.web = readTag(parser,"web");
            } else if (name.equals("source")) {
                ret.source = readTag(parser,"source");
            } else if (name.equals("tracker")) {
                ret.tracker = readTag(parser,"tracker");
            } else if (name.equals("changelog")) {
                ret.changelog = readTag(parser,"changelog");
            } else if (name.equals("author")) {
                ret.author = readTag(parser,"author");
            } else if (name.equals("bitcoin")) {
                ret.bitcoin = readTag(parser,"bitcoin");
            } else if (name.equals("liberapay")) {
                ret.liberapay = readTag(parser,"liberapay");
            } else if (name.equals("marketversion")) {
                ret.marketversion = readTag(parser,"marketversion");
            } else if (name.equals("marketvercode")) {
                ret.marketvercode = readTag(parser,"marketvercode");
            } else if (name.equals("antifeatures")) {
                ret.antifeatures = readTag(parser,"antifeatures");
            } else {
                skip(parser);
            }
        }

        return ret;
    }

    // Processes any plain string element
    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return title;
    }

    // Processes title tags in the feed.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return title;
    }

    // Processes title tags in the feed.
    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}