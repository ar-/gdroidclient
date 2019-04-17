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

package org.gdroid.gdroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.gdroid.gdroid.CommentListActivity;
import org.gdroid.gdroid.GlideApp;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.CommentBean;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentBean> {
    private final Context context;
    private final List<CommentBean> values;
    private final String appId;
    private final boolean limitLines;

    public CommentAdapter(Context context, List<CommentBean> values, String appId, boolean limitLines) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.appId = appId;
        this.limitLines = limitLines;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.comment_line, parent, false);
        LinearLayout commentLineContainer = rowView.findViewById(R.id.comment_line_container);
        TextView line1 = rowView.findViewById(R.id.lbl_firstLine);
        final TextView line2 = rowView.findViewById(R.id.lbl_secondLine);
        final TextView lblMore = rowView.findViewById(R.id.lbl_more);
        ImageView imageView = rowView.findViewById(R.id.icon);
        line1.setText(values.get(position).author);
        String content = values.get(position).content;

        // remove marking tags from review text
        content = content.replace("@<span>gdroid</span>","");
        content = content.replace("@<span>gdroid@mastodon.technology</span>","");
        content = content.replace("#<span>"+ Util.convertPackageNameToHashtag(appId)+"</span>","");
        content = content.replace("#<span>fdroid_app_comments</span>","");

        if (limitLines)
        {
            line2.setMaxLines(10);
        }
        else
        {
            line2.setMaxLines(200);
        }

        // make the whole comment line clickable (#205)
        commentLineContainer.setClickable(true);
        commentLineContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(values.get(position).url));
                context.startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            line2.setText(Html.fromHtml(content, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
        } else {
            line2.setText(Html.fromHtml(content));
        }


        // checking this right away does not work, so do it a bit later
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTextTruncated(line2))
                        {
                            lblMore.setVisibility(View.VISIBLE);
                            lblMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent myIntent = new Intent(context, CommentListActivity.class);
                                    myIntent.putExtra("appId", appId);
                                    context.startActivity(myIntent);
                                }
                            });
                        }
                        else
                        {
                            lblMore.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        GlideApp.with(context).load(values.get(position).avatar).into(imageView);
        return rowView;
    }

    private static boolean isTextTruncated( TextView textView )
    {
        if ( textView != null )
        {
            Layout layout = textView.getLayout();
            if ( layout != null )
            {
                int lines = layout.getLineCount();
                if ( lines > 0 )
                {
                    int ellipsisCount = layout.getEllipsisCount( lines - 1 );
                    if ( ellipsisCount > 0 )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}