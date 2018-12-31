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

package org.gdroid.gdroid.authors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.gdroid.gdroid.R;
import org.gdroid.gdroid.beans.AuthorBean;

import java.text.DecimalFormat;

public class AuthorArrayAdapter extends ArrayAdapter<AuthorBean> {
    private final Context context;
    private final AuthorBean[] values;

    public AuthorArrayAdapter(Context context, AuthorBean[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.author_line, parent, false);
        TextView line1 = (TextView) rowView.findViewById(R.id.lbl_firstLine);
        TextView line2 = (TextView) rowView.findViewById(R.id.lbl_secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        line1.setText(values[position].author);
        DecimalFormat df = new DecimalFormat("0.0");
        line2.setText(values[position].apps + " apps ("+df.format(values[position].stars) + " ★)");
//        imageView.setImageResource(R.drawable.ic_update_green_24dp);
        return rowView;
    }
}