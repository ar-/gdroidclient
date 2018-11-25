//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.gdroid.gdroid.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.MenuItem;
import android.view.SubMenu;

@RestrictTo({Scope.LIBRARY_GROUP})
@SuppressLint("RestrictedApi")
public final class BottomNavigationMenu extends MenuBuilder {
    public static final int MAX_ITEM_COUNT = 6;

    public BottomNavigationMenu(Context context) {
        super(context);
    }

    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        throw new UnsupportedOperationException("BottomNavigationView does not support submenus");
    }

    protected MenuItem addInternal(int group, int id, int categoryOrder, CharSequence title) {
        if (this.size() + 1 > 6) {
            throw new IllegalArgumentException("Maximum number of items supported by BottomNavigationView is 6. Limit can be checked with BottomNavigationView#getMaxItemCount()");
        } else {
            this.stopDispatchingItemsChanged();
            MenuItem item = super.addInternal(group, id, categoryOrder, title);
            if (item instanceof MenuItemImpl) {
                ((MenuItemImpl)item).setExclusiveCheckable(true);
            }

            this.startDispatchingItemsChanged();
            return item;
        }
    }
}
