/*
 * Copyright 2012 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kevinsawicki.wishlist;

import android.app.Activity;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for lists where only multiple view types are needed
 *
 */
public abstract class MultiTypeAdapter extends TypeAdapter {

  private final LayoutInflater inflater;

  private final int[] layout;

  private final int[][] children;

  private final List<Object> items = new ArrayList<Object>();

  private final SparseIntArray types = new SparseIntArray();

  /**
   * Create adapter
   *
   * @param activity
   */
  public MultiTypeAdapter(final Activity activity) {
    this(activity.getLayoutInflater());
  }

  /**
   * Create adapter
   *
   * @param context
   */
  public MultiTypeAdapter(final Context context) {
    this(LayoutInflater.from(context));
  }

  /**
   * Create adapter
   *
   * @param inflater
   */
  public MultiTypeAdapter(final LayoutInflater inflater) {
    this.inflater = inflater;

    int[] empty = new int[0];
    int count = getViewTypeCount();
    children = new int[count][];
    layout = new int[count];
    for (int i = 0; i < count; i++) {
      int[] ids = getChildViewIds(i);
      if (ids == null)
        ids = empty;
      children[i] = ids;
      layout[i] = getChildLayoutId(i);
    }
  }

  /**
   * Clear all items
   *
   * @return this adapter
   */
  public MultiTypeAdapter clear() {
    items.clear();
    types.clear();

    notifyDataSetChanged();
    return this;
  }

  /**
   * Add items to adapter registered as the given type
   *
   * @param type
   * @param items
   * @return this adapter
   */
  public MultiTypeAdapter addItems(int type, Object... items) {
    if (items == null || items.length == 0)
      return this;

    for (Object item : items) {
      types.put(this.items.size(), type);
      this.items.add(item);
    }

    notifyDataSetChanged();
    return this;
  }

  /**
   * Get layout id for type
   *
   * @param type
   * @return layout id
   */
  protected abstract int getChildLayoutId(int type);

  /**
   * Get child view ids for type
   *
   * @param type
   * @return array of view ids
   */
  protected abstract int[] getChildViewIds(int type);

  public int getCount() {
    return items.size();
  }

  public Object getItem(final int position) {
    return items.get(position);
  }

  public long getItemId(int position) {
    return items.get(position).hashCode();
  }

  public int getItemViewType(int position) {
    return types.get(position);
  }

  /**
   * Set text on text view with given id
   *
   * @param parentView
   * @param childViewId
   * @param text
   * @return text view
   */
  protected TextView setText(View parentView, int childViewId, CharSequence text) {
    TextView textView = textView(parentView, childViewId);
    textView.setText(text);
    return textView;
  }

  /**
   * Update view for item
   *
   * @param position
   * @param view
   * @param item
   * @param type
   */
  protected abstract void update(int position, View view, Object item, int type);

  public View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);
    if (convertView == null)
      convertView = initialize(inflater.inflate(layout[type], null),
          children[type]);
    update(position, convertView, getItem(position), type);
    return convertView;
  }
}
