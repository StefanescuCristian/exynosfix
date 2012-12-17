package com.rc.exynosmemfix;

import com.rc.exynosmemfix.VirtualTerminal.VTCommandResult;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView textVuln;
	TextView textBoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textVuln = (TextView) findViewById(R.id.textVuln);
		textBoot = (TextView) findViewById(R.id.textBoot);
		Button buttonFix = (Button) findViewById(R.id.buttonFix);
		Button buttonUnFix = (Button) findViewById(R.id.buttonUnFix);
		Button buttonSetOnBoot = (Button) findViewById(R.id.buttonSetOnBoot);
		Button buttonUnSetOnBoot = (Button) findViewById(R.id.buttonUnSetOnBoot);

		buttonFix.setOnClickListener(fixClickListener);
		buttonUnFix.setOnClickListener(unFixClickListener);
		buttonSetOnBoot.setOnClickListener(setOnBoot);
		buttonUnSetOnBoot.setOnClickListener(unSetOnBoot);

		checkIfVuln();
		checkIfBoot();}

	private void checkIfVuln() {
		try {
			VTCommandResult r = VirtualTerminal.run("ls -l /dev/exynos-mem", true);
			if (r.stdout.contains("rw-rw-rw-")) {
				textVuln.setText("/dev/exynos-mem is vulnerable!");
				textVuln.setTextColor(Color.RED);
			} 
			else {
				textVuln.setText("/dev/exynos-mem is NOT vulnerable!");
				textVuln.setTextColor(Color.GREEN);
			}
		}
		catch (Exception ex) {
			textVuln.setText("Unable to determine vulnerability status - is your device rooted?");
			textVuln.setTextColor(Color.YELLOW);
		}
	}
	private void checkIfBoot() {
		try {
			VTCommandResult rb = VirtualTerminal.run("ls -l /etc/init.d/", true);
			if (rb.stdout.contains("70-exynosfix.sh")) {
				textBoot.setText("Patch is applied on boot!");
				textBoot.setTextColor(Color.GREEN);
			}
			else {
				textBoot.setText("Patch is not applied on boot!");
				textBoot.setTextColor(Color.RED);
			}
		}
		catch (Exception ex) {
			textBoot.setText("Unable to determine vulnerability status - is your device rooted?");
			textBoot.setTextColor(Color.YELLOW);
		}
	}
		
	OnClickListener fixClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			VTCommandResult r = VirtualTerminal.run("chmod 400 /dev/exynos-mem", true);
			if (!r.success()) {
				Toast.makeText(MainActivity.this, "Error: " + r.stderr, Toast.LENGTH_LONG).show();
			}
			checkIfVuln();
		}
	};

	OnClickListener unFixClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			VTCommandResult r = VirtualTerminal.run("chmod 666 /dev/exynos-mem", true);
			if (!r.success()) {
				Toast.makeText(MainActivity.this, "Error: " + r.stderr, Toast.LENGTH_LONG).show();
			}
			checkIfVuln();
		}
	};
	
	OnClickListener setOnBoot = new OnClickListener() {

		@Override
		public void onClick(View v) {
			VTCommandResult rb = VirtualTerminal.run("mount -o remount,rw /system; touch /etc/init.d/70-exynosfix.sh; echo 'chmod 400 /dev/exynos-mem' > /etc/init.d/70-exynosfix.sh; mount -o remount,ro /system", true);
			if (!rb.success()) {
                                Toast.makeText(MainActivity.this, "Error: " + rb.stderr, Toast.LENGTH_LONG).show();
                        }
                        checkIfBoot();
                 }
	};
	
	OnClickListener unSetOnBoot = new OnClickListener() {
	
		@Override
		public void onClick(View v) {
			VTCommandResult rb = VirtualTerminal.run("mount -o remount,rw /system; rm /etc/init.d/70-exynosfix.sh; mount -o remount,ro /system", true);
			if (!rb.success()) {
			      Toast.makeText(MainActivity.this, "Error: " + rb.stderr, Toast.LENGTH_LONG).show();
			}
			checkIfBoot();
	}

	};
}
