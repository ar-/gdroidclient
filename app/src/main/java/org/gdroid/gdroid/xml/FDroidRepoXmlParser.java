/*
 * Copyright (C) 2018 Andreas Redmer <ar-gdroid@abga.be>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.gdroid.gdroid.xml;

import android.text.TextUtils;
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
        ApplicationBean ret = new ApplicationBean();
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String name = parser.getName();
//            if (name.equals("name")) {
//                ret.name = readTag(parser,"name");
//            } else if (name.equals("id")) {
//                ret.id = readTag(parser,"id");
//            } else if (name.equals("lastupdated")) {
//                ret.lastupdated = readTag(parser,"lastupdated");
//            } else if (name.equals("summary")) {
//                ret.summary = readTag(parser,"summary");
//            } else if (name.equals("added")) {
//                ret.added = readTag(parser,"added");
//            } else if (name.equals("icon")) {
//                ret.icon = readTag(parser,"icon");
//            } else if (name.equals("desc")) {
//                ret.desc = readTag(parser,"desc");
//            } else if (name.equals("license")) {
//                ret.license = readTag(parser,"license");
//            } else if (name.equals("categories")) {
//                ret.categories = readTag(parser,"categories");
//            } else if (name.equals("web")) {
//                ret.web = readTag(parser,"web");
//            } else if (name.equals("source")) {
//                ret.source = readTag(parser,"source");
//            } else if (name.equals("tracker")) {
//                ret.tracker = readTag(parser,"tracker");
//            } else if (name.equals("changelog")) {
//                ret.changelog = readTag(parser,"changelog");
//            } else if (name.equals("author")) {
//                ret.author = readTag(parser,"author");
//            } else if (name.equals("email")) {
//                ret.email = readTag(parser,"email");
//            } else if (name.equals("bitcoin")) {
//                ret.bitcoin = readTag(parser,"bitcoin");
//            } else if (name.equals("liberapay")) {
//                ret.liberapay = readTag(parser,"liberapay");
//            } else if (name.equals("marketversion")) {
//                ret.marketversion = readTag(parser,"marketversion");
//            } else if (name.equals("marketvercode")) {
//                ret.marketvercode = readTag(parser,"marketvercode");
//            } else if (name.equals("antifeatures")) {
//                ret.antifeatures = readTag(parser,"antifeatures");
//            } else if (name.equals("package")) {
//                Pack p = readPackage(parser);
//                if (TextUtils.isEmpty(ret.apkname)) {
//                    ret.permissions = p.permissions;
//                    ret.apkname = p.apkname;
//                }
//            } else {
//                skip(parser);
//            }
//        }

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

    private Pack readPackage(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "package");
        Pack ret = new Pack();
        //String perm = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("apkname")) {
                ret.apkname = readTag(parser,"apkname");
            } else if (name.equals("permissions")) {
                ret.permissions = readTag(parser,"permissions");
            } else {
                skip(parser);
            }
        }
        return ret;
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

    public class Pack{
        String apkname = "";
        String permissions = "";
    }

}