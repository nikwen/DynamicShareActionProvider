/**
 * Copyright 2013 Niklas Wenzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.nikwen.dynamicshareactionprovider.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class DynamicShareActionProvider extends ActionProvider {

    private PackageManager pm;
    private List<ResolveInfo> list;
    private Context context;
    private Intent shareIntent;

    private Object listener = null;

    public DynamicShareActionProvider(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Set the type of the data which should be shared. Needed to show a list of apps.
     * @param type The same as you would pass to {@link android.content.Intent#setType(String)}.
     */
    public void setShareDataType(String type) {
        if (pm == null) {
            pm = context.getPackageManager();
        }
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(type);
        list = pm.queryIntentActivities(shareIntent, 0);
    }

    /**
     * Set a listener of type {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareIntentUpdateListener}.
     * This will override any previous listeners (also those which were set using {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider#setOnShareLaterListener(de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareLaterListener)}.
     * @param listener The listener
     * @see de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider#setOnShareLaterListener(de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareLaterListener)
     */
    public void setOnShareIntentUpdateListener(OnShareIntentUpdateListener listener) {
        if (listener != null) {
            this.listener = listener;
        } else {
            throw new NullPointerException("listener must not be null!");
        }
    }

    /**
     * Set a listener of type {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareLaterListener}.
     * This will override any previous listeners (also those which were set using {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider#setOnShareIntentUpdateListener(de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareIntentUpdateListener)}.
     * @param listener The listener
     * @see de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider#setOnShareIntentUpdateListener(de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareIntentUpdateListener)
     */
    public void setOnShareLaterListener(OnShareLaterListener listener) {
        if (listener != null) {
            this.listener = listener;
        } else {
            throw new NullPointerException("listener must not be null!");
        }
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        if (pm != null && list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo resolveInfo = list.get(i);
                OnMenuItemClickUpdateIntentListener itemListener = new OnMenuItemClickUpdateIntentListener();
                itemListener.setPosition(i);
                MenuItem item = subMenu.add(resolveInfo.loadLabel(pm))
                        .setIcon(resolveInfo.loadIcon(pm))
                        .setOnMenuItemClickListener(itemListener);
            }
        } else {
            String msg;
            if (shareIntent == null || shareIntent.getType() == null || shareIntent.getType().equals("")) {
                msg = context.getString(R.string.no_share_type);
            } else if (list == null || list.size() <= 0) {
                String formatMsg = context.getString(R.string.no_app_to_share);
                msg = String.format(formatMsg, shareIntent.getType());
            } else {
                msg = context.getString(R.string.error_occurred);
            }

            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public class OnMenuItemClickUpdateIntentListener implements MenuItem.OnMenuItemClickListener {

        private int position;

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (listener == null) {
                return false;
            } else {
                ActivityInfo activity = list.get(position).activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                        activity.name);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);

                if (listener instanceof OnShareIntentUpdateListener) {
                    OnShareIntentUpdateListener onShareIntentUpdateListener = (OnShareIntentUpdateListener) listener;
                    shareIntent.putExtras(onShareIntentUpdateListener.onShareIntentExtrasUpdate());

                    context.startActivity(shareIntent);

                    return true;
                } else if (listener instanceof OnShareLaterListener) {
                    OnShareLaterListener onShareLaterListener = (OnShareLaterListener) listener;
                    onShareLaterListener.onShareClick(shareIntent);

                    return true;
                }

                return false;
            }
        }

        protected void setPosition(int position) {
            this.position = position;
        }

    }

    /**
     * Listener which is set using {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider#setOnShareIntentUpdateListener(de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareIntentUpdateListener)}.
     * The data which should be shared has to be passed back as the return value of the {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareIntentUpdateListener#onShareIntentExtrasUpdate()} method as you would pass it to the Intent using {@link android.content.Intent#putExtras(android.os.Bundle)}.
     * @see de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareLaterListener
     */
    public interface OnShareIntentUpdateListener {

        /**
         * This will be called when an app for sharing has been selected. Generate the data here.
         * @return The data which should be shared as you would pass it to the Intent using {@link android.content.Intent#putExtras(android.os.Bundle)}.
         */
        public Bundle onShareIntentExtrasUpdate();

    }

    /**
     * Listener which is set using {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider#setOnShareLaterListener(de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareLaterListener)}.
     * The {@link android.content.Intent} containing the necessary information about the receiver and the data type is passed to the {@link de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareLaterListener#onShareClick(android.content.Intent)} method which has to implement a custom sharing implementation.
     * @see de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider.OnShareIntentUpdateListener
     */
    public interface OnShareLaterListener {

        /**
         * This will be called when an app for sharing has been selected. Implement your custom sharing implementation here.
         * @param shareIntent The {@link android.content.Intent} containing the necessary information about the receiver and the data type
         */
        public void onShareClick(Intent shareIntent);

    }

}
