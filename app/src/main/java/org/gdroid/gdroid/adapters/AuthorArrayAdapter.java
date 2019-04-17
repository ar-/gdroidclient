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
import java.util.List;

public class AuthorArrayAdapter extends ArrayAdapter<AuthorBean> {
    private final Context context;
    private final List<AuthorBean> values;
    private final String prefix;

    public AuthorArrayAdapter(Context context, List<AuthorBean> values, String prefix) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.prefix = prefix;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.author_line, parent, false);
        TextView line1 = rowView.findViewById(R.id.lbl_firstLine);
        TextView line2 = rowView.findViewById(R.id.lbl_secondLine);
//        ImageView imageView = rowView.findViewById(R.id.icon);
        line1.setText(values.get(position).author);
        DecimalFormat df = new DecimalFormat("0.0");
        line2.setText(values.get(position).apps + prefix +" apps ("+df.format(values.get(position).stars) + " ★)");
//        imageView.setImageResource(R.drawable.ic_update_green_24dp);
        return rowView;
    }
}