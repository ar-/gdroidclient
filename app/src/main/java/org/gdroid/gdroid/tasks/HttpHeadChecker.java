/*
 * Copyright (C) 2019 Andreas Redmer <ar-gdroid@abga.be>
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

package org.gdroid.gdroid.tasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class HttpHeadChecker {
    public static String getEtag(String surl)
    {
        try {
            URL url = new URL(surl);
            String ret="";
            URLConnection con = url.openConnection();
            if (con instanceof HttpURLConnection) {
                /* Workaround for https://code.google.com/p/android/issues/detail?id=61013 */
                con.addRequestProperty("Accept-Encoding", "identity");
                con.addRequestProperty("http.keepAlive", "false");
                ((HttpURLConnection) con).setRequestMethod("HEAD");
                int response = ((HttpURLConnection) con).getResponseCode();
                ret = con.getHeaderField("ETag");
                if (response != HttpURLConnection.HTTP_OK)
                    return "";
            }
            InputStream in = con.getInputStream();
            in.close();
            return ret;
        } catch (FileNotFoundException x) {
            return "";
        } catch (UnknownHostException x) {
            return "";
        } catch (SocketException x) {
            return "";
        } catch (ProtocolException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
}
