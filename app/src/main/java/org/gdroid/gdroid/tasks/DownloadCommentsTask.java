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

import android.os.AsyncTask;
import android.util.Log;

import org.gdroid.gdroid.beans.CommentBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DownloadCommentsTask extends AsyncTask<String, Void, List<CommentBean>> {
    private final Runnable runnableOnPostExecute;
    private final List<CommentBean> mCommentBeansInActivity;

    public DownloadCommentsTask(List<CommentBean> commentBeansInActivity, Runnable runnableOnPostExecute) {
        this.mCommentBeansInActivity = commentBeansInActivity;
        this.runnableOnPostExecute = runnableOnPostExecute;
    }

    protected List<CommentBean> doInBackground(String... urls) {
        String urldisplay = urls[0];
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            String s = getStreamContent(in,"UTF-8");
            CommentsJsonParser p = new CommentsJsonParser();
            final List<CommentBean> comments = p.getCommentBeansFromJson(s);
            return comments;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    protected void onPostExecute(List<CommentBean> result) {
        mCommentBeansInActivity.clear();
        mCommentBeansInActivity.addAll(result);
        runnableOnPostExecute.run();
    }

    private static String getStreamContent(
            InputStream fis,
            String          encoding )
    {
        try
        {
            BufferedReader br = new BufferedReader( new InputStreamReader(fis, encoding ));
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
            }
            return sb.toString();
        }
        catch (Exception e)
        {

        }
        return "[]";
    }


}