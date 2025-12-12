package com.proofence.shakecardapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit,
    private val thresholdG: Float = 2.3f,
    private val cooldownMs: Long = 900L
) : SensorEventListener {

    private var lastShakeTimeMs: Long = 0

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / 9.81f
        val gY = y / 9.81f
        val gZ = z / 9.81f

        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce >= thresholdG) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTimeMs >= cooldownMs) {
                lastShakeTimeMs = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
