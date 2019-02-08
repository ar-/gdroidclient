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

package org.gdroid.gdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import org.gdroid.gdroid.Adapters.CommentAdapter;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.CommentBean;
import org.gdroid.gdroid.tasks.DownloadCommentsTask;

import java.util.ArrayList;
import java.util.List;

public class CommentListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppDatabase db = AppDatabase.get(getApplicationContext());

        String appId = getIntent().getStringExtra("appId");

        // fetch comments and init adapter
        ListView commentsListView = findViewById(R.id.listview_comments);

        final List<CommentBean> commentBeans = new ArrayList<>();
        final CommentAdapter commentAdapter = new CommentAdapter(this, commentBeans, appId);
        commentsListView.setAdapter(commentAdapter);
        (new DownloadCommentsTask(commentBeans, new Runnable(){
            @Override
            public void run() {
                commentAdapter.notifyDataSetChanged();
                findViewById(R.id.circle_fetching_comments).setVisibility(View.GONE);
            }
        })).execute("https://mastodon.technology/api/v1/timelines/tag/"+Util.convertPackageNameToHashtag(appId)+"?limit=40");
        //})).execute("https://mastodon.technology/api/v1/timelines/tag/gdroid"+"?limit=3");

    }

}
