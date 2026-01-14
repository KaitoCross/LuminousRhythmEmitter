package com.github.kaitocross.luminousrhythmcontroller

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.kaitocross.luminousrhythmcontroller.ui.theme.LuminousRhythmControllerTheme
import java.util.UUID

private const val LOG_UI = "UI"
private const val LOG_BT = "BLE"

class MainActivity : ComponentActivity() {
    companion object {
        // BLE device name characteristics are limited to 20 bytes
        private const val LUMINOUS_DEVICE_NAME = "luminousrhythmemitte"
        // See LRE.yaml for custom values
        private val LUMINOUS_SERVICE_ID = UUID.fromString("759297ad-ad74-4eb2-af4b-d94332ec6b7d")
        private val LUMINOUS_RGB_CHARACTERISTIC = UUID.fromString("b4cb7b26-407c-4040-b992-00325d97f517")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuminousRhythmControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RemoteControl(
                        Modifier.padding(innerPadding),
                        { color -> updateColor(color) },
                    )
                }
            }
        }
        initBluetooth()
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        when {
            checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {

            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), BT_PERMISSIONS)
            }
        }
    }*/

    private fun initBluetooth() {
        val req =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (!permissions.all { permission -> permission.value }) {
                    // TODO Need to display an error that BT permissions are required
                    finishAndRemoveTask()
                } else {
                    startBluetoothScan()
                }
            }
        req.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        )
        /*when {
            checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {

            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), BT_PERMISSIONS)
            }
        }*/
    }

    private fun updateColor(color: Color) : Unit {
        val argb = color.toArgb()
        val rgb = byteArrayOf((argb shr 16).toByte(), (argb shr 8).toByte(), argb.toByte())
        Handler(mainLooper).post {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                foundDevices.forEach { device ->
                    val gatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
                        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                            super.onServicesDiscovered(gatt, status)
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                Log.i(LOG_BT, "setting color ${rgb[0]},${rgb[1]},${rgb[2]} on device ${gatt?.device?.address}")
                                val service = gatt?.getService(LUMINOUS_SERVICE_ID)
                                val characteristic = service?.getCharacteristic(LUMINOUS_RGB_CHARACTERISTIC)
                                characteristic?.setValue(rgb)
                                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                    gatt?.writeCharacteristic(characteristic)
                                }
                            } else {
                                Log.i(LOG_BT, "couldn't discover any services on ${gatt?.device?.address} (status: $status)")
                            }
                        }

                        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                            super.onConnectionStateChange(gatt, status, newState)
                            Log.i(LOG_BT, "connection state change to $newState on device ${gatt?.device?.address}")
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                if (newState == BluetoothGatt.STATE_CONNECTED) {
                                    Log.i(LOG_BT, "discovering services on ${gatt?.device?.address}")
                                    if (!(gatt?.discoverServices() ?: false)) {
                                        Log.i(LOG_BT, "can't initiate service scan on ${gatt?.device?.address}")
                                    }
                                }
                            }
                        }
                    })
                    gatt.connect()
                }
            }
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                synchronized(foundDevices) {
                    Log.i(LOG_BT, "adding device ${result.device}")
                    foundDevices.add(result.device)
                }
            }
        }
    }

    private var scanning = false
    private val foundDevices : MutableSet<BluetoothDevice> = HashSet()
    private fun startBluetoothScan() {
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            val manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothLeScanner = manager.adapter.bluetoothLeScanner
            if (!scanning) {
                // stop scanning after 10 seconds.
                Handler(mainLooper).postDelayed({
                    Log.i(LOG_BT, "stopping scan, sleeping")
                    scanning = false
                    bluetoothLeScanner.stopScan(leScanCallback)
                    // wait 10 seconds, then restart the scan
                    Handler(mainLooper).postDelayed({
                        startBluetoothScan()
                    }, 10000)
                }, 10000)
                // TODO filter for our characteristics
                val filter = listOf(ScanFilter.Builder().setDeviceName(LUMINOUS_DEVICE_NAME).build())
                /*synchronized(foundDevices) {
                    foundDevices.clear()
                }*/
                Log.i(LOG_BT, "starting scan")
                scanning = true
                bluetoothLeScanner.startScan(filter, ScanSettings.Builder().build(), leScanCallback)
            }
        }
    }

}

@Composable
fun RemoteControl(modifier: Modifier = Modifier, updateColor: (color: Color) -> Unit = {}) {
    var colorHue by remember { mutableFloatStateOf(0f) }
    var colorSaturation by remember { mutableFloatStateOf(1f) }
    var colorValue by remember { mutableFloatStateOf(1f) }
    Column(modifier = modifier) {
        Text(
            text = "",
            modifier = Modifier
                .padding(8.dp)
                .height(80.dp)
                .fillMaxSize()
                //.border(1.5.dp, MaterialTheme.colorScheme.primary)
                .background(Color.hsv(colorHue, colorSaturation, colorValue)),
        )
        Row(modifier = Modifier.padding(8.dp)) {
            /*Text(
                text = "H",
                fontSize = 32.sp,
                modifier = Modifier.padding(4.dp),
            )*/
            Slider(
                value = colorHue,
                valueRange = 0f..360f,
                onValueChange = { hueValue ->
                    colorHue = hueValue
                },
                onValueChangeFinished = {
                    val color = Color.hsv(colorHue, colorSaturation, colorValue)
                    Log.i(LOG_UI, "value=$color")
                    updateColor(color)
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.hsv(colorHue, 1f, 1f),
                ),
                modifier = Modifier.padding(8.dp),
                /*track = { sliderState ->
                    val gradient = Brush.horizontalGradient(
                        0f to Color.hsv(0f, 1f, 1f),
                        0.3333333f to Color.hsv(120f, 1f, 1f),
                        0.6666667f to Color.hsv(240f, 1f, 1f),
                        1f to Color.hsv(360f, 1f, 1f),
                    )
                    SliderDefaults.Track(sliderState = sliderState, modifier = Modifier.background(gradient))
                }*/
            )
        }
        /*Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "S",
                fontSize = 32.sp,
                modifier = Modifier.padding(4.dp),
            )
            Slider(
                value = colorSaturation,
                valueRange = 0f..1f,
                onValueChange = { satValue ->
                    colorSaturation = satValue
                },
                onValueChangeFinished = {
                    val color = Color.hsv(colorHue, colorSaturation, colorValue)
                    updatePairedDevices(color)
                },
                modifier = Modifier.padding(8.dp),
            )
        }*/
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LuminousRhythmControllerTheme {
        RemoteControl()
    }
}
