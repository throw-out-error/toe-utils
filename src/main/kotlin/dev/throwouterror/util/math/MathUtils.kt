package dev.throwouterror.util.math

import kotlin.math.PI

object MathUtils {
    const val invPi = 1.0 / PI
    const val halfPi = PI * 0.5
    const val twoPi = PI * 2.0

    fun degrees(v: Double) = v * (180.0 * invPi)

    fun radians(v: Double) = v * (PI / 180.0)

    /**
     * Helper function to create a Vector with optional values.
     */
    fun vectorOf(
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        w: Double = 0.0
    ) = Tensor(x, y, z, w)

    fun vectorOf(v: Double) = vectorOf(v, v, v, v)
    fun vectorOf(v: Tensor, z: Double = 0.0, w: Double = 0.0) = vectorOf(v.x, v.y, z, w)
    fun vectorOf(v: Tensor, w: Double = 0.0) = vectorOf(v.x, v.y, v.z, w)
    fun vectorOf(v: Tensor) = vectorOf(v.x, v.y, v.z, v.w)
}
