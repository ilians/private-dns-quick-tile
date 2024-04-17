package com.jpwolfso.privdnsqt

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.VideoView
import com.jpwolfso.privdnsqt.util.dnsProvider
import com.jpwolfso.privdnsqt.util.hasPermission
import com.jpwolfso.privdnsqt.util.showToast

class PrivateDnsConfigActivity : Activity() {

    private lateinit var preferences: PrivateDnsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_dns_config)
        preferences = PrivateDnsPreferences(this)
        if (!hasPermission() || preferences.isFirstRun) {
            showHelpMenu()
            preferences.isFirstRun = false
        }

        val checkOff = findViewById<CheckBox>(R.id.check_off)
        val checkAuto = findViewById<CheckBox>(R.id.check_auto)
        val checkOn = findViewById<CheckBox>(R.id.check_on)
        val textHostname = findViewById<EditText>(R.id.text_hostname)
        val okButton = findViewById<Button>(R.id.button_ok)

        checkOff.isChecked = preferences.toggleOff
        checkAuto.isChecked = preferences.toggleAuto
        checkOn.isChecked = preferences.toggleOn

        textHostname.isEnabled = preferences.toggleOn
        textHostname.setText(dnsProvider.orEmpty())

        checkOff.setOnCheckedChangeListener { _, isChecked ->
            preferences.toggleOff = isChecked
        }
        checkAuto.setOnCheckedChangeListener { _, isChecked ->
            preferences.toggleAuto = isChecked
        }
        checkOn.setOnCheckedChangeListener { _, isChecked ->
            preferences.toggleOn = isChecked
            textHostname.isEnabled = isChecked
        }

        okButton.setOnClickListener {
            if (hasPermission()) {
                if (checkOn.isChecked) {
                    if (textHostname.text.isEmpty()) {
                        showToast(R.string.toast_no_dns)
                        return@setOnClickListener
                    } else {
                        dnsProvider = textHostname.text?.toString().orEmpty()
                    }
                }
                showToast(R.string.toast_changes_saved)
                finish()
            } else {
                showToast(R.string.toast_no_permission)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_overflow, menu)
        return true
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_appinfo -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }

            R.id.action_fdroid -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(getString(R.string.url_fdroid)))
                startActivity(intent)
            }

            R.id.action_help -> showHelpMenu()
        }
        return super.onMenuItemSelected(featureId, item)
    }

    private fun showHelpMenu() {
        val layoutInflater = LayoutInflater.from(this)
        val helpView = layoutInflater.inflate(R.layout.dialog_help, null)
        val videoView = helpView.findViewById<VideoView>(R.id.video_view)
        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.terminal))
        videoView.start()
        val helpDialog = AlertDialog.Builder(this@PrivateDnsConfigActivity)
            .setMessage(R.string.message_help)
            .setPositiveButton(android.R.string.ok, null)
            .setView(helpView)
            .create()
        helpDialog.show()
    }
}
