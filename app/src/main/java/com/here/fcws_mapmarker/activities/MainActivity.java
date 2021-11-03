/*
 * Copyright (C) 2019-2020 HERE Europe B.V.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.fcws_mapmarker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.here.fcws_mapmarker.App;
import com.here.fcws_mapmarker.service.DataReceiver;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.service.UDPServerService;
import com.here.fcws_mapmarker.model.Vehicle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context context;
    final Handler handler = new Handler();

    private int portNumber = 15000;
    private static List<Vehicle> vehicleList = new ArrayList<>();

    private Button buttonStartReceiving;
    private Button buttonStopReceiving;
    private Button buttonClear;
    private static TextView textViewDataFromClient;

    public static int textView_msgCounter = 0;
    public static final int textView_msgLimit = 30;
    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = MainActivity.this;
        setContentView(R.layout.activity_main);

        buttonStartReceiving = (Button) findViewById(R.id.btn_start_receiving);
        buttonStopReceiving = (Button) findViewById(R.id.btn_stop_receiving);
        textViewDataFromClient = (TextView) findViewById(R.id.tv_data_from_server);
        buttonClear = (Button) findViewById(R.id.btn_clear);

        buttonClear.setEnabled(false);

        Toast.makeText(context,"Start the server to receive vehicles data.", Toast.LENGTH_LONG).show();

        initVehicleList();

        Intent intent = new Intent(this, UDPServerService.class);
        intent.putExtra("portNumber", portNumber);

        ResultReceiver dataReceiver = new DataReceiver(handler);
        intent.putExtra("receiver", dataReceiver);

        buttonStartReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartReceiving.setEnabled(false);
                buttonStopReceiving.setEnabled(true);
                buttonClear.setEnabled(true);
                Toast.makeText(context, "Listening on port " + portNumber + "...", Toast.LENGTH_LONG).show();

                if(DEBUG) Log.d("UDP", "Starting UDP Server Service...");
                startService(intent);
            }
        });

        buttonStopReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEBUG) Log.d("UDP", "Stopping UDP Server Service...");
                buttonStopReceiving.setEnabled(false);

                stopService(intent);

                buttonStartReceiving.setEnabled(true);
            }
        });
    }

    public static TextView getTextViewDataFromClient() {
        return textViewDataFromClient;
    }

    private void initVehicleList() {
        vehicleList.add(new Vehicle());
    }

    public void clearTextViewButtonClicked(View v) {
        textViewDataFromClient.setText("");
    }

    public void navigateToMapView(View view) {
        Intent intent = new Intent(this, MapViewActivity.class);
        intent.putExtra("vehicleList", (Serializable) vehicleList);

        startActivity(intent);
    }
}