/*
 * Copyright (C) 2018-2019 Andreas Redmer <ar-gdroid@abga.be>
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.gdroid.gdroid.adapters.AuthorArrayAdapter;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.AuthorBean;

import java.util.List;

public class AuthorListActivity extends AppCompatActivity {
    ListView authorsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        authorsList = findViewById(R.id.listview_authors);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppDatabase db = AppDatabase.get(getApplicationContext());

        String type = getIntent().getStringExtra("type");
        final List<AuthorBean> authors;
        final boolean showTopAuthors = ! TextUtils.isEmpty(type) && type.equals("top");
        if (showTopAuthors) {
            authors = db.appDao().getTopAuthors();
        }
        else
        {
            authors = db.appDao().getAllAuthors();
        }
        db.close();

        final AuthorArrayAdapter adapter = new AuthorArrayAdapter(this, authors, showTopAuthors ? " top" : "");
        authorsList.setAdapter(adapter);

        final AuthorListActivity caller = this;
        authorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final AuthorBean item = (AuthorBean) parent.getItemAtPosition(position);

                Intent myIntent = new Intent(caller, AppCollectionActivity.class);
                myIntent.putExtra("collectionName", "author:"+item.author);
                myIntent.putExtra("headline", item.author);
                caller.startActivity(myIntent);
            }

        });
    }

}
