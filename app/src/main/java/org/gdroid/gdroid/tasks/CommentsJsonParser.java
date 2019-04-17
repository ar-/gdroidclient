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

import android.text.TextUtils;

import org.gdroid.gdroid.beans.CommentBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentsJsonParser {
    public List<CommentBean> getCommentBeansFromJson(String jsonString) {
        List<CommentBean> entries = new ArrayList<>();
        try {
            JSONArray ja =new JSONArray(jsonString);
            entries = new ArrayList<>(ja.length());
            for (int i = 0; i < ja.length(); i++) {
                JSONObject toot = ja.getJSONObject(i);
                try {
                    if (i%60 ==0)
                        System.gc(); // avoid out of memory exception on older devices
                    CommentBean cb = jsonObjToCommentBean(toot);
                    if (!TextUtils.isEmpty(cb.author))
                        entries.add(cb);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //skip
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private CommentBean jsonObjToCommentBean(JSONObject toot) throws JSONException {
        CommentBean cb = new CommentBean();
        cb.author = toot.getJSONObject("account").optString("display_name","");
        if (TextUtils.isEmpty(cb.author))
            cb.author = toot.getJSONObject("account").optString("username","");
        cb.avatar = toot.getJSONObject("account").optString("avatar_static","");
        cb.content = toot.optString("content","");
        cb.url = toot.optString("url","");
        return cb;
    }

}
