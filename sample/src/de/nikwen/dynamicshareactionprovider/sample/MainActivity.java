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

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.EditText;

import de.nikwen.dynamicshareactionprovider.library.DynamicShareActionProvider;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        DynamicShareActionProvider provider = (DynamicShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_item_share));
        provider.setShareDataType("text/plain");
        provider.setOnShareIntentUpdateListener(provider.new OnShareIntentUpdateListener() {

            @Override
            public Bundle onShareIntentExtrasUpdate() {
                Bundle extras = new Bundle();
                EditText shareEdit = (EditText) findViewById(R.id.share_edit);
                extras.putString(android.content.Intent.EXTRA_TEXT, shareEdit.getText().toString());
                return extras;
            }

        });
        return true;
    }
}
