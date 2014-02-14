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

package de.nikwen.dynamicshareactionprovider.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        DynamicShareActionProvider provider = (DynamicShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();
        provider.setShareDataType("text/plain");
        provider.setOnShareIntentUpdateListener(new DynamicShareActionProvider.OnShareIntentUpdateListener() {

            @Override
            public Bundle onShareIntentExtrasUpdate() {
                Bundle extras = new Bundle();
                EditText shareEdit = (EditText) findViewById(R.id.share_edit);
                extras.putString(android.content.Intent.EXTRA_TEXT, shareEdit.getText().toString());
                return extras;
            }

        });

        DynamicShareActionProvider shareLaterProvider = (DynamicShareActionProvider) menu.findItem(R.id.menu_item_share_later).getActionProvider();
        shareLaterProvider.setShareDataType("text/plain");
        shareLaterProvider.setOnShareLaterListener(new DynamicShareActionProvider.OnShareLaterListener() {

            @Override
            public void onShareClick(Intent shareIntent) {
                MyShareAsyncTask task = new MyShareAsyncTask();
                task.execute(shareIntent);
            }

        });

        return true;
    }

    private class MyShareAsyncTask extends AsyncTask<Intent, Void, Intent> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, R.string.asynctask, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Intent doInBackground(Intent... intents) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            EditText shareEdit = (EditText) findViewById(R.id.share_edit);
            intents[0].putExtra(android.content.Intent.EXTRA_TEXT, "Shared from an AsyncTask: " + shareEdit.getText().toString());

            return intents[0];
        }

        @Override
        protected void onPostExecute(Intent intent) {
            startActivity(intent);
        }

    }

}
