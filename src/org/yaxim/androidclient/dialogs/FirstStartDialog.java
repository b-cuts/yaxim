package org.yaxim.androidclient.dialogs;

import org.yaxim.androidclient.XMPPRosterServiceAdapter;
import org.yaxim.androidclient.exceptions.YaximXMPPAdressMalformedException;
import org.yaxim.androidclient.util.PreferenceConstants;
import org.yaxim.androidclient.util.XMPPHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.yaxim.androidclient.MainWindow;
import org.yaxim.androidclient.R;

public class FirstStartDialog extends AlertDialog implements DialogInterface.OnClickListener,
		TextWatcher {

	private MainWindow mainWindow;
	private Button mOkButton;
	private EditText mEditJabberID;
	private EditText mEditPassword;
	private EditText mEditPort;

	public FirstStartDialog(MainWindow mainWindow,
			XMPPRosterServiceAdapter serviceAdapter) {
		super(mainWindow);
		this.mainWindow = mainWindow;

		setTitle(R.string.StartupDialog_Title);

		LayoutInflater inflater = (LayoutInflater) mainWindow
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View group = inflater.inflate(R.layout.firststartdialog, null, false);
		setView(group);

		setButton(BUTTON_POSITIVE, mainWindow.getString(android.R.string.ok), this);

		mEditJabberID = (EditText) group.findViewById(R.id.StartupDialog_JID_EditTextField);
		mEditPassword = (EditText) group.findViewById(R.id.StartupDialog_PASSWD_EditTextField);
		mEditPort = (EditText) group.findViewById(R.id.StartupDialog_PORT_EditTextField);
		mEditJabberID.addTextChangedListener(this);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mOkButton = getButton(BUTTON_POSITIVE);
		mOkButton.setEnabled(false);
	}


	public void onClick(DialogInterface dialog, int which) {
		verifyAndSavePreferences();
	}

	private void verifyAndSavePreferences() {
		String password = mEditPassword.getText().toString();
		String jabberID = mEditJabberID.getText().toString();
		String port = mEditPort.getText().toString();

		if (port == null) {
			savePreferences(jabberID, password);
			cancel();

		} else {
			savePreferences(jabberID, password, port);
			cancel();
		}
	}

	public void afterTextChanged(Editable s) {
		try {
			XMPPHelper.verifyJabberID(s);
			mOkButton.setEnabled(true);
			//mOkButton.setOnClickListener(this);
			mEditJabberID.setTextColor(Color.DKGRAY);
		} catch (YaximXMPPAdressMalformedException e) {
			mOkButton.setEnabled(false);
			mEditJabberID.setTextColor(Color.RED);
		}
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	private void savePreferences(String jabberID, String password, String port) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mainWindow);
		Editor editor = sharedPreferences.edit();

		editor.putString(PreferenceConstants.JID, jabberID);
		editor.putString(PreferenceConstants.PASSWORD, password);
		editor.putString(PreferenceConstants.PORT, port);
		editor.commit();
	}

	private void savePreferences(String jabberID, String password) {
		savePreferences(jabberID, password, PreferenceConstants.DEFAULT_PORT);
	}

}
