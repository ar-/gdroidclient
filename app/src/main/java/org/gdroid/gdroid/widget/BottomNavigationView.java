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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.gdroid.gdroid.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.R.attr;
import android.support.design.R.color;
import android.support.design.R.dimen;
import android.support.design.R.style;
import android.support.design.R.styleable;
import android.support.design.internal.ThemeEnforcement;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

@SuppressLint("RestrictedApi")
public class BottomNavigationView extends FrameLayout {
    private static final int MENU_PRESENTER_ID = 1;
    private final MenuBuilder menu;
    private final BottomNavigationMenuView menuView;
    private final BottomNavigationPresenter presenter;
    private MenuInflater menuInflater;
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener;
    private BottomNavigationView.OnNavigationItemReselectedListener reselectedListener;

    public BottomNavigationView(Context context) {
        this(context, (AttributeSet)null);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, attr.bottomNavigationStyle);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.presenter = new BottomNavigationPresenter();
        this.menu = new BottomNavigationMenu(context);
        this.menuView = new BottomNavigationMenuView(context);
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 17;
        this.menuView.setLayoutParams(params);
        this.presenter.setBottomNavigationMenuView(this.menuView);
        this.presenter.setId(1);
        this.menuView.setPresenter(this.presenter);
        this.menu.addMenuPresenter(this.presenter);
        this.presenter.initForMenu(this.getContext(), this.menu);
        TintTypedArray a = ThemeEnforcement.obtainTintedStyledAttributes(context, attrs, styleable.BottomNavigationView, defStyleAttr, style.Widget_Design_BottomNavigationView, new int[]{styleable.BottomNavigationView_itemTextAppearanceInactive, styleable.BottomNavigationView_itemTextAppearanceActive});
        if (a.hasValue(styleable.BottomNavigationView_itemIconTint)) {
            this.menuView.setIconTintList(a.getColorStateList(styleable.BottomNavigationView_itemIconTint));
        } else {
            this.menuView.setIconTintList(this.menuView.createDefaultColorStateList(16842808));
        }

        this.setItemIconSize(a.getDimensionPixelSize(styleable.BottomNavigationView_itemIconSize, this.getResources().getDimensionPixelSize(dimen.design_bottom_navigation_icon_size)));
        if (a.hasValue(styleable.BottomNavigationView_itemTextAppearanceInactive)) {
            this.setItemTextAppearanceInactive(a.getResourceId(styleable.BottomNavigationView_itemTextAppearanceInactive, 0));
        }

        if (a.hasValue(styleable.BottomNavigationView_itemTextAppearanceActive)) {
            this.setItemTextAppearanceActive(a.getResourceId(styleable.BottomNavigationView_itemTextAppearanceActive, 0));
        }

        if (a.hasValue(styleable.BottomNavigationView_itemTextColor)) {
            this.setItemTextColor(a.getColorStateList(styleable.BottomNavigationView_itemTextColor));
        }

        if (a.hasValue(styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, (float)a.getDimensionPixelSize(styleable.BottomNavigationView_elevation, 0));
        }

        this.setLabelVisibilityMode(a.getInteger(styleable.BottomNavigationView_labelVisibilityMode, -1));
        this.setItemHorizontalTranslationEnabled(a.getBoolean(styleable.BottomNavigationView_itemHorizontalTranslationEnabled, true));
        int itemBackground = a.getResourceId(styleable.BottomNavigationView_itemBackground, 0);
        this.menuView.setItemBackgroundRes(itemBackground);
        if (a.hasValue(styleable.BottomNavigationView_menu)) {
            this.inflateMenu(a.getResourceId(styleable.BottomNavigationView_menu, 0));
        }

        a.recycle();
        this.addView(this.menuView, params);
        if (VERSION.SDK_INT < 21) {
            this.addCompatibilityTopDivider(context);
        }

        this.menu.setCallback(new Callback() {
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                if (BottomNavigationView.this.reselectedListener != null && item.getItemId() == BottomNavigationView.this.getSelectedItemId()) {
                    BottomNavigationView.this.reselectedListener.onNavigationItemReselected(item);
                    return true;
                } else {
                    return BottomNavigationView.this.selectedListener != null && !BottomNavigationView.this.selectedListener.onNavigationItemSelected(item);
                }
            }

            public void onMenuModeChange(MenuBuilder menu) {
            }
        });
    }

    public void setOnNavigationItemSelectedListener(@Nullable BottomNavigationView.OnNavigationItemSelectedListener listener) {
        this.selectedListener = listener;
    }

    public void setOnNavigationItemReselectedListener(@Nullable BottomNavigationView.OnNavigationItemReselectedListener listener) {
        this.reselectedListener = listener;
    }

    @NonNull
    public Menu getMenu() {
        return this.menu;
    }

    public void inflateMenu(int resId) {
        this.presenter.setUpdateSuspended(true);
        this.getMenuInflater().inflate(resId, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(true);
    }

    public int getMaxItemCount() {
        return 6;
    }

    @Nullable
    public ColorStateList getItemIconTintList() {
        return this.menuView.getIconTintList();
    }

    public void setItemIconTintList(@Nullable ColorStateList tint) {
        this.menuView.setIconTintList(tint);
    }

    public void setItemIconSize(@Dimension int iconSize) {
        this.menuView.setItemIconSize(iconSize);
    }

    public void setItemIconSizeRes(@DimenRes int iconSizeRes) {
        this.setItemIconSize(this.getResources().getDimensionPixelSize(iconSizeRes));
    }

    @Dimension
    public int getItemIconSize() {
        return this.menuView.getItemIconSize();
    }

    @Nullable
    public ColorStateList getItemTextColor() {
        return this.menuView.getItemTextColor();
    }

    public void setItemTextColor(@Nullable ColorStateList textColor) {
        this.menuView.setItemTextColor(textColor);
    }

    /** @deprecated */
    @Deprecated
    @DrawableRes
    public int getItemBackgroundResource() {
        return this.menuView.getItemBackgroundRes();
    }

    public void setItemBackgroundResource(@DrawableRes int resId) {
        this.menuView.setItemBackgroundRes(resId);
    }

    @Nullable
    public Drawable getItemBackground() {
        return this.menuView.getItemBackground();
    }

    public void setItemBackground(@Nullable Drawable background) {
        this.menuView.setItemBackground(background);
    }

    @IdRes
    public int getSelectedItemId() {
        return this.menuView.getSelectedItemId();
    }

    public void setSelectedItemId(@IdRes int itemId) {
        MenuItem item = this.menu.findItem(itemId);
        if (item != null && !this.menu.performItemAction(item, this.presenter, 0)) {
            item.setChecked(true);
        }

    }

    public void setLabelVisibilityMode(int labelVisibilityMode) {
        if (this.menuView.getLabelVisibilityMode() != labelVisibilityMode) {
            this.menuView.setLabelVisibilityMode(labelVisibilityMode);
            this.presenter.updateMenuView(false);
        }

    }

    public int getLabelVisibilityMode() {
        return this.menuView.getLabelVisibilityMode();
    }

    public void setItemTextAppearanceInactive(@StyleRes int textAppearanceRes) {
        this.menuView.setItemTextAppearanceInactive(textAppearanceRes);
    }

    @StyleRes
    public int getItemTextAppearanceInactive() {
        return this.menuView.getItemTextAppearanceInactive();
    }

    public void setItemTextAppearanceActive(@StyleRes int textAppearanceRes) {
        this.menuView.setItemTextAppearanceActive(textAppearanceRes);
    }

    @StyleRes
    public int getItemTextAppearanceActive() {
        return this.menuView.getItemTextAppearanceActive();
    }

    public void setItemHorizontalTranslationEnabled(boolean itemHorizontalTranslationEnabled) {
        if (this.menuView.isItemHorizontalTranslationEnabled() != itemHorizontalTranslationEnabled) {
            this.menuView.setItemHorizontalTranslationEnabled(itemHorizontalTranslationEnabled);
            this.presenter.updateMenuView(false);
        }

    }

    public boolean isItemHorizontalTranslationEnabled() {
        return this.menuView.isItemHorizontalTranslationEnabled();
    }

    private void addCompatibilityTopDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(ContextCompat.getColor(context, color.design_bottom_navigation_shadow_color));
        LayoutParams dividerParams = new LayoutParams(-1, this.getResources().getDimensionPixelSize(dimen.design_bottom_navigation_shadow_height));
        divider.setLayoutParams(dividerParams);
        this.addView(divider);
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(this.getContext());
        }

        return this.menuInflater;
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BottomNavigationView.SavedState savedState = new BottomNavigationView.SavedState(superState);
        savedState.menuPresenterState = new Bundle();
        this.menu.savePresenterStates(savedState.menuPresenterState);
        return savedState;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof BottomNavigationView.SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            BottomNavigationView.SavedState savedState = (BottomNavigationView.SavedState)state;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.menu.restorePresenterStates(savedState.menuPresenterState);
        }
    }

    static class SavedState extends AbsSavedState {
        Bundle menuPresenterState;
        public static final Creator<BottomNavigationView.SavedState> CREATOR = new ClassLoaderCreator<BottomNavigationView.SavedState>() {
            public BottomNavigationView.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new BottomNavigationView.SavedState(in, loader);
            }

            public BottomNavigationView.SavedState createFromParcel(Parcel in) {
                return new BottomNavigationView.SavedState(in, (ClassLoader)null);
            }

            public BottomNavigationView.SavedState[] newArray(int size) {
                return new BottomNavigationView.SavedState[size];
            }
        };

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.readFromParcel(source, loader);
        }

        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(this.menuPresenterState);
        }

        private void readFromParcel(Parcel in, ClassLoader loader) {
            this.menuPresenterState = in.readBundle(loader);
        }
    }

    public interface OnNavigationItemReselectedListener {
        void onNavigationItemReselected(@NonNull MenuItem var1);
    }

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(@NonNull MenuItem var1);
    }
}
