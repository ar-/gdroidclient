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

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.gdroid.gdroid.AppDownloader;
import org.gdroid.gdroid.GlideApp;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CommentBean;
import org.gdroid.gdroid.beans.VersionBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class VersionAdapter extends ArrayAdapter<VersionBean> {
    private final Context context;
    private final List<VersionBean> values;
    private final ApplicationBean app;
    private final String installedVersion;

    public VersionAdapter(Context context, List<VersionBean> values, ApplicationBean app) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.app = app;
        installedVersion = Util.getInstalledVersionOfApp(context,app.id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.version_line, parent, false);
        TextView line1 = rowView.findViewById(R.id.lbl_firstLine);
        TextView line2 = rowView.findViewById(R.id.lbl_secondLine);
        TextView line3 = rowView.findViewById(R.id.lbl_thirdline);
        TextView llbWhatsNew = rowView.findViewById(R.id.lbl_whats_new);
        final Button btnInstall = rowView.findViewById(R.id.btn_install);
        TextView lblinstalled = rowView.findViewById(R.id.lbl_installed);
        line1.setText(values.get(position).versionName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        line2.setText(parent.getResources().getString(R.string.added_on)+ ": "+sdf.format(values.get(position).added));
        DecimalFormat dfSize = new DecimalFormat("#.#");
        line3.setText(parent.getResources().getString(R.string.app_size)+ ": "+dfSize.format(Math.max(values.get(position).size/1024f/1024f,0.1f)) + " MB");

        // attach the whatsnew-text to the first item
        if (position == 0 && ! TextUtils.isEmpty(app.whatsNew))
        {
            llbWhatsNew.setVisibility(View.VISIBLE);
            llbWhatsNew.setText(parent.getResources().getString(R.string.perms_new_perm_prefix)+ " "+app.whatsNew);
        }
        else
        {
            llbWhatsNew.setVisibility(View.GONE);
        }

        // mark installed version
        if (values.get(position).versionName.equals(installedVersion))
        {
            lblinstalled.setVisibility(View.VISIBLE);
            btnInstall.setVisibility(View.GONE);
        }

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDownloader.download(context, app, true);
            }
        });


        return rowView;
    }
}