/*
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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.gdroid.gdroid.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.view.ViewGroup;

@RestrictTo({Scope.LIBRARY_GROUP})
public class BottomNavigationPresenter implements MenuPresenter {
    private MenuBuilder menu;
    private BottomNavigationMenuView menuView;
    private boolean updateSuspended = false;
    private int id;

    public BottomNavigationPresenter() {
    }

    public void setBottomNavigationMenuView(BottomNavigationMenuView menuView) {
        this.menuView = menuView;
    }

    public void initForMenu(Context context, MenuBuilder menu) {
        this.menu = menu;
        this.menuView.initialize(this.menu);
    }

    public MenuView getMenuView(ViewGroup root) {
        return this.menuView;
    }

    public void updateMenuView(boolean cleared) {
        if (!this.updateSuspended) {
            if (cleared) {
                this.menuView.buildMenuView();
            } else {
                this.menuView.updateMenuView();
            }

        }
    }

    public void setCallback(Callback cb) {
    }

    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        return false;
    }

    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
    }

    public boolean flagActionItems() {
        return false;
    }

    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public Parcelable onSaveInstanceState() {
        BottomNavigationPresenter.SavedState savedState = new BottomNavigationPresenter.SavedState();
        savedState.selectedItemId = this.menuView.getSelectedItemId();
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof BottomNavigationPresenter.SavedState) {
            this.menuView.tryRestoreSelectedItemId(((BottomNavigationPresenter.SavedState)state).selectedItemId);
        }

    }

    public void setUpdateSuspended(boolean updateSuspended) {
        this.updateSuspended = updateSuspended;
    }

    static class SavedState implements Parcelable {
        int selectedItemId;
        public static final Creator<BottomNavigationPresenter.SavedState> CREATOR = new Creator<BottomNavigationPresenter.SavedState>() {
            public BottomNavigationPresenter.SavedState createFromParcel(Parcel in) {
                return new BottomNavigationPresenter.SavedState(in);
            }

            public BottomNavigationPresenter.SavedState[] newArray(int size) {
                return new BottomNavigationPresenter.SavedState[size];
            }
        };

        SavedState() {
        }

        SavedState(Parcel in) {
            this.selectedItemId = in.readInt();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(@NonNull Parcel out, int flags) {
            out.writeInt(this.selectedItemId);
        }
    }
}
