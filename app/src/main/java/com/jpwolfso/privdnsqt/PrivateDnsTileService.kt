package com.jpwolfso.privdnsqt

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.jpwolfso.privdnsqt.util.dnsMode
import com.jpwolfso.privdnsqt.util.dnsProvider
import com.jpwolfso.privdnsqt.util.hasPermission
import com.jpwolfso.privdnsqt.util.showToast

class PrivateDnsTileService : TileService() {

    private lateinit var preferences: PrivateDnsPreferences
    private val inactiveTileData
        get() = TileData(
            state = Tile.STATE_INACTIVE,
            label = getString(R.string.qt_off),
            icon = R.drawable.ic_dnsoff,
            dnsMode = DNS_MODE_OFF
        )

    private val autoTileData
        get() = TileData(
            state = Tile.STATE_ACTIVE,
            label = getString(R.string.qt_auto),
            icon = R.drawable.ic_dnsauto,
            dnsMode = DNS_MODE_AUTO
        )

    private val activeTileData
        get() = TileData(
            state = Tile.STATE_ACTIVE,
            label = dnsProvider,
            icon = R.drawable.ic_dnsauto,
            dnsMode = DNS_MODE_ON
        )

    override fun onCreate() {
        super.onCreate()
        preferences = PrivateDnsPreferences(this)
    }

    override fun onStartListening() {
        super.onStartListening()
        when (dnsMode?.lowercase()) {
            DNS_MODE_OFF -> refreshTile(inactiveTileData)
            DNS_MODE_AUTO -> refreshTile(autoTileData)
            DNS_MODE_ON -> {
                if (dnsProvider != null) {
                    refreshTile(activeTileData)
                } else {
                    showToast(R.string.toast_no_permission)
                }
            }
        }
    }

    override fun onClick() {
        if (hasPermission()) {
            switchToNextDnsMode()
        } else {
            showToast(R.string.toast_no_permission)
        }
    }

    private fun switchToNextDnsMode() {
        val enabledModes = listOfNotNull(
            DNS_MODE_OFF.takeIf { preferences.toggleOff },
            DNS_MODE_AUTO.takeIf { preferences.toggleAuto },
            DNS_MODE_ON.takeIf { preferences.toggleOn }
        )
        val currentMode = dnsMode?.lowercase()
        val currentIndex = enabledModes.indexOf(currentMode)

        val nextMode = if (currentIndex == -1 || currentIndex == enabledModes.lastIndex) {
            enabledModes.firstOrNull()
        } else {
            enabledModes[currentIndex + 1]
        }
        when (nextMode) {
            DNS_MODE_OFF -> changeTileState(inactiveTileData)
            DNS_MODE_AUTO -> changeTileState(autoTileData)
            DNS_MODE_ON -> changeTileState(activeTileData)
        }
    }

    private fun changeTileState(tileData: TileData) {
        dnsMode = tileData.dnsMode
        refreshTile(tileData)
    }

    private fun refreshTile(tileData: TileData) {
        qsTile.apply {
            state = tileData.state
            label = tileData.label
            icon = Icon.createWithResource(this@PrivateDnsTileService, tileData.icon)
            updateTile()
        }
    }

    companion object {

        private const val DNS_MODE_OFF = "off"
        private const val DNS_MODE_AUTO = "opportunistic"
        private const val DNS_MODE_ON = "hostname"
    }
}

private data class TileData(
    val state: Int,
    val label: String?,
    val icon: Int,
    val dnsMode: String,
)

//            val preferences = PrivateDnsPreferences(this)
//            val toggleOff = preferences.toggleOff
//            val toggleAuto = preferences.toggleAuto
//            val toggleOn = preferences.toggleOn
//            when (dnsMode?.lowercase()) {
//                DNS_MODE_OFF -> {
//                    if (toggleAuto) {
//                        changeTileState(autoTileData)
//                    } else if (toggleOn) {
//                        changeTileState(activeTileData)
//                    }
//                }
//
//                DNS_MODE_AUTO -> {
//                    if (dnsProvider != null) {
//                        if (toggleOn) {
//                            changeTileState(activeTileData)
//                        } else if (toggleOff) {
//                            changeTileState(inactiveTileData)
//                        }
//                    } else if (toggleOff) {
//                        changeTileState(inactiveTileData)
//                    }
//                }
//
//                DNS_MODE_ON -> {
//                    if (toggleOff) {
//                        changeTileState(inactiveTileData)
//                    } else if (toggleAuto) {
//                        changeTileState(autoTileData)
//                    }
//                }
//            }
