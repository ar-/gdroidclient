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

package org.gdroid.gdroid.Adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.gdroid.gdroid.GlideApp;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CommentBean;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentBean> {
    private final Context context;
    private final List<CommentBean> values;
    private final ApplicationBean mApp;

    public CommentAdapter(Context context, List<CommentBean> values, ApplicationBean app) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.mApp = app;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.comment_line, parent, false);
        TextView line1 = rowView.findViewById(R.id.lbl_firstLine);
        TextView line2 = rowView.findViewById(R.id.lbl_secondLine);
        ImageView imageView = rowView.findViewById(R.id.icon);
        line1.setText(values.get(position).author);
//        line2.setText(values.get(position).content);
        String content = values.get(position).content;

        // remove marking tags from review text
        content = content.replace("@<span>gdroid</span>","");
        content = content.replace("@<span>gdroid@mastodon.technology</span>","");
        content = content.replace("#<span>"+ Util.convertPackageNameToHashtag(mApp.id)+"</span>","");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            line2.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
        } else {
            line2.setText(Html.fromHtml(content));
        }

//        GlideApp.with(context).load(values.get(position).avatar).override(192, 192).into(imageView);
        GlideApp.with(context).load(values.get(position).avatar).into(imageView);


//        imageView.setImageResource(R.drawable.ic_update_green_24dp);
        return rowView;
    }
}