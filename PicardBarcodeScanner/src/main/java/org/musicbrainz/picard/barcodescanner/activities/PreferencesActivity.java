/*
 * Copyright (C) 2012 Philipp Wolfer <ph.wolfer@googlemail.com>
 * 
 * This file is part of MusicBrainz Picard Barcode Scanner.
 * 
 * MusicBrainz Picard Barcode Scanner is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * MusicBrainz Picard Barcode Scanner is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MusicBrainz Picard Barcode Scanner. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.musicbrainz.picard.barcodescanner.activities;

import org.musicbrainz.picard.barcodescanner.R;
import org.musicbrainz.picard.barcodescanner.util.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PreferencesActivity extends BaseActivity {

	private EditText mIpAddressInput;
	private EditText mPortInput;
	private Button mConnectBtn;
	private String mBarcode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubView(R.layout.activity_preferences);
		
		mIpAddressInput = (EditText) findViewById(R.id.picard_ip_address);
		mPortInput = (EditText) findViewById(R.id.picard_port);
		mConnectBtn = (Button) findViewById(R.id.btn_picard_connect);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

		handleIntents();
		registerEventListeners();
		loadFormDataFromPreferences();
		checkConnectButtonEnabled();

		if (mBarcode != null) {
			TextView errorMsg = (TextView) findViewById(R.id.label_connection_error);
			errorMsg.setVisibility(View.VISIBLE);
			mConnectBtn.setText(R.string.btn_picard_connect);
		}
	}

	@Override
	protected void handleIntents() {
		super.handleIntents();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mBarcode = extras.getString(Constants.INTENT_EXTRA_BARCODE);
		} else {
			mBarcode = null;
		}
	}

	private void registerEventListeners() {
		TextWatcher textWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				checkConnectButtonEnabled();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		mIpAddressInput.addTextChangedListener(textWatcher);

		mConnectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getPreferences().setIpAddressAndPort(readIpAddressFromInput(),
						readPortFromInput());

				startNextActivity();
			}
		});
	}

	private void loadFormDataFromPreferences() {
		mIpAddressInput.setText(getPreferences().getIpAddress());
		mPortInput.setText(String.valueOf(getPreferences().getPort()));
	}

	private void checkConnectButtonEnabled() {
		mConnectBtn.setEnabled(!readIpAddressFromInput().equals(""));
	}

	private String readIpAddressFromInput() {
		return mIpAddressInput.getText().toString();
	}

	private int readPortFromInput() {
		String port = mPortInput.getText().toString();

		try {
			return Integer.parseInt(port.trim());
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	private void startNextActivity() {
		Intent intent;

		if (mBarcode == null) {
			intent = new Intent(PreferencesActivity.this, ScannerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else {
			intent = new Intent(PreferencesActivity.this,
					PerformSearchActivity.class);
			intent.putExtra(Constants.INTENT_EXTRA_BARCODE, mBarcode);
		}

		startActivity(intent);
		finish();
	}
}