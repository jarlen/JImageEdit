/*
 * Copyright (C) 2016 jarlen
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.jarlen.imgedit.base;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * For RecyclerView.Adapter
 * Created by jarlen on 2016/11/9.
 */
public abstract class RvCommonAdapter<D> extends RecyclerView.Adapter<RvViewHolder> {

    public Context mContext = null;
    protected List<D> listData = new ArrayList<D>();

    public RvCommonAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder viewHolder = RvViewHolder.getViewHolder(mContext, parent, getLayoutResId(viewType));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder viewHolder, int position) {
        onBindView(viewHolder, listData.get(position));
    }

    @Override
    public int getItemCount() {
        if (listData == null) {
            return 0;
        }
        return listData.size();
    }

    public abstract void onBindView(RvViewHolder viewHolder, D item);

    public abstract int getLayoutResId(int viewType);

    public void addDataList(List<D> mList) {
        if (listData != null) {
            listData.addAll(mList);
        }
        this.notifyDataSetChanged();
    }

    public void addData(D data) {
        if (listData != null) {
            listData.add(data);
        }
        this.notifyDataSetChanged();
    }

    public void clearDataList() {
        if (listData != null) {
            listData.clear();
        }
        this.notifyDataSetChanged();
    }

    public void removeData(int position) {
        if (listData != null) {
            listData.remove(position);
        }
        this.notifyDataSetChanged();
    }

    public void removeData(D data) {
        if (listData != null) {
            listData.remove(data);
        }
        this.notifyDataSetChanged();
    }

    public void removeDataList(List<D> data) {
        if (listData != null) {
            listData.removeAll(data);
        }
        this.notifyDataSetChanged();
    }


}
