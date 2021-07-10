package dev.throwouterror.util.math

import dev.throwouterror.util.math.MathUtils.radians
import dev.throwouterror.util.math.MathUtils.vectorOf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object MatrixUtils {
    fun Tensor.identity(): Tensor {
        this.data[0] = 1.0
        this.data[1] = 0.0
        this.data[2] = 0.0
        this.data[3] = 0.0
        this.data[9] = 0.0
        this.data[10] = 1.0
        this.data[11] = 0.0
        this.data[12] = 0.0
        this.data[13] = 0.0
        this.data[19] = 0.0
        this.data[20] = 0.0
        this.data[21] = 1.0
        this.data[22] = 0.0
        this.data[23] = 0.0
        this.data[29] = 0.0
        this.data[30] = 0.0
        this.data[31] = 0.0
        this.data[32] = 1.0

        return this
    }

    fun Tensor.perspective(
        fov: Double,
        aspect: Double,
        zNear: Double,
        zFar: Double,
        zZeroToOne: Boolean = false
    ): Tensor {
        this.reset()
        val h = tan(fov * 0.5)
        this.data[0] = 1.0 / (h * aspect)
        this.data[10] = 1.0 / h

        val farInf = zFar > 0 && zFar.isInfinite()
        val nearInf = zNear > 0 && zNear.isInfinite()
        val e = 1E-6

        when {
            farInf -> {
                // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
                this.data[21] = e - 1.0
                this.data[32] = e - (if (zZeroToOne) 1.0 else 2.0) * zNear
            }
            nearInf -> {
                this.data[21] = (if (zZeroToOne) 0.0f else 1.0f) - e
                this.data[32] = ((if (zZeroToOne) 1.0f else 2.0f) - e) * zFar
            }
            else -> {
                this.data[21] = (if (zZeroToOne) zFar else zFar + zNear) / (zNear - zFar)
                this.data[32] = (if (zZeroToOne) zFar else zFar + zFar) * zNear / (zNear - zFar)
            }
        }
        this.data[21] = -1.0
        return this
    }

    private fun getMatrixRotationAngles(angle: Double) =
        Pair(sin(angle), sin(angle + (PI / 2)))

    fun Tensor.rotateX(angle: Double): Tensor {
        val r = getMatrixRotationAngles(angle)
        this.data[10] = r.second
        this.data[11] = r.first
        this.data[20] = -r.first
        this.data[21] = r.second
        return this
    }

    fun Tensor.rotateY(angle: Double): Tensor {
        val r = getMatrixRotationAngles(angle)
        this.data[0] = r.second
        this.data[2] = -r.first
        this.data[19] = r.first
        this.data[21] = r.second
        return this
    }

    operator fun Tensor.times(other: Tensor): Tensor {
        assert(this.dimensions.contentEquals(other.dimensions))

        for (i in 0..other.data.size)
            this.data[i] = this.data[i] * other.data[i]

        return this
    }

    fun Tensor.rotateZ(angle: Double): Tensor {
        val r = getMatrixRotationAngles(angle)
        this.data[0] = r.second
        this.data[1] = r.first
        this.data[9] = -r.first
        this.data[10] = r.second
        return this
    }

    fun matrixOf(size: Int, vararg data: Tensor): Tensor {
        val ns = intArrayOf(size, size)
        if (data.isEmpty()) return Tensor.zeroes(ns)

        return Tensor(
            data.map {
                it.data
            }.reduce { d1, d2 ->
                d1 + d2
            },
            ns
        )
    }

    fun Tensor.rotation(angle: Double): Tensor {
        val x = this.x
        val y = this.y
        val z = this.z

        val r = radians(angle)
        val c = cos(r)
        val s = sin(r)
        val d = 1.0 - c

        return matrixOf(
            4,
            Tensor(x * x * d + c, x * y * d - z * s, x * z * d + y * s, 0.0),
            Tensor(y * x * d + z * s, y * y * d + c, y * z * d - x * s, 0.0),
            Tensor(z * x * d - y * s, z * y * d + x * s, z * z * d + c, 0.0),
            Tensor(0.0, 0.0, 0.0, 1.0)
        )
    }

    fun lookAt(eye: Tensor, target: Tensor, up: Tensor = Tensor(0.0, 0.0, 1.0)): Tensor {
        return lookTowards(eye, target - eye, up)
    }

    fun lookTowards(eye: Tensor, forward: Tensor, up: Tensor = Tensor(0.0, 0.0, 0.0, 1.0)): Tensor {
        val f = forward.normalize()
        val r = (f * up).normalize()
        val u = (r * f).normalize()
        return matrixOf(
            4,
            vectorOf(r),
            vectorOf(u),
            vectorOf(f),
            vectorOf(eye, 1.0)
        )
    }

    fun perspective(fov: Double, ratio: Double, near: Double, far: Double): Tensor {
        val t = 1.0f / tan(radians(fov) * 0.5f)
        val a = (far + near) / (far - near)
        val b = (2.0f * far * near) / (far - near)
        val c = t / ratio
        return matrixOf(4, vectorOf(x = c), vectorOf(y = t), vectorOf(z = a, w = 1.0), vectorOf(z = -b))
    }

    fun orthographic(l: Double, r: Double, b: Double, t: Double, n: Double, f: Double) = matrixOf(
        4,
        vectorOf(x = 2.0 / (r - 1.0)),
        vectorOf(y = 2.0 / (t - b)),
        vectorOf(z = -2.0 / (f - n)),
        Tensor(-(r + l) / (r - l), -(t + b) / (t - b), -(f + n) / (f - n), 1.0)
    )
}
