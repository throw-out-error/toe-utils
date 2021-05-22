/*
 * Copyright (c) Throw Out Error
 * https://throw-out-error.dev
 */

package dev.throwouterror.util.math

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
open class Tensor : Iterable<Double?> {
    var data: DoubleArray
        protected set
    var dimensions: IntArray
        protected set

    /**
     * Creates a new tensor with the specified n-dimensional data.
     */
    constructor(vararg data: Double) : this(data, intArrayOf(data.size))

    /**
     * Creates a new tensor with the specified n-dimensional data.
     */
    constructor(vararg data: Float) : this(data.map { it.toDouble() }.toDoubleArray(), intArrayOf(data.size))

    /**
     * Creates a new tensor with the specified n-dimensional data.
     */
    constructor(vararg data: Int) : this(data.map { it.toDouble() }.toDoubleArray(), intArrayOf(data.size))

    /**
     * Creates a new tensor with the specified n-dimensional data. This constructor
     * is used for cloning.
     */
    constructor(newData: DoubleArray, size: IntArray) {
        data = newData
        dimensions = size
    }

    fun set(vararg data: Double): Tensor {
        if (data.size != this.data.size) throw Exception("Invalid size encountered while setting Tensor data")
        this.data = data
        return this
    }

    fun set(vararg data: Float): Tensor {
        return this.set(*data.map { it.toDouble() }.toDoubleArray())
    }

    fun set(vararg data: Int): Tensor {
        return this.set(*data.map { it.toDouble() }.toDoubleArray())
    }


    fun offset(dir: Direction, n: Int = 1): Tensor {
        return if (n == 0) this else clone().plus(dir.directionVec).times(n)
    }

    /**
     * @return A string representation of the multidimensional list.
     */
    fun toArrayString(xSize: Int, spaces: Int = 4): String {
        val spacing = (0..spaces).map {
            " "
        }.reduce { acc, s -> acc + s }
        return buildString {
            append("[\n")
            for (row in toArray(xSize)) {
                append("${spacing}[")
                for (elem in row)
                    append(" $elem ")
                append("]\n")
            }
            append("]\n")
        }
    }

    fun toArray(xSize: Int): Array<DoubleArray> =
        (0 until data.size / xSize).map { data.sliceArray((it * xSize) until ((it + 1) * xSize)) }.toTypedArray()

    /**
     * Returns the length of the Tensor.
     */
    fun length(): Int {
        return sqrt(data.reduce { acc, i -> acc * i }).toInt()
    }

    /**
     * @return the current Tensor that has been changed (**not a new
     * one**). Normalizes the Tensor to length 1. Note that this
     * Tensor uses int values for coordinates.
     */
    fun normalize(): Tensor {
        val amt = length()
        if (amt == 0) return this
        this.map { v: Double -> v * amt }
        return this
    }

    /**
     * @return the current Tensor that has been changed (**not a new
     * one**). Equivalent to mul(-1).
     */
    fun reverse(): Tensor {
        times(-1)
        return this
    }

    var x
        get(): Double {
            return data[0]
        }
        set(value) {
            data[0] = value
        }
    var y
        get(): Double {
            return data[1]
        }
        set(value) {
            data[1] = value
        }
    var z
        get(): Double {
            return data[2]
        }
        set(value) {
            data[2] = value
        }
    var w
        get(): Double {
            return data[3]
        }
        set(value) {
            data[3] = value
        }

    /**
     * @param other to add
     * @return the current Tensor that has been changed (**not a new
     * one**).
     */
    operator fun plus(other: Tensor): Tensor {
        val t = zeroes(other.dimensions)

        for (f in data.indices)
            t.data[f] = f + other.data[f]

        return t
    }

    operator fun plus(other: Number): Tensor {
        return this.map { v: Double -> v + other.toDouble() }
    }

    /**
     * @param other to multiply() with
     * @return the current Tensor that has been changed (**not a new
     * one**).
     */
    operator fun plus(other: Double): Tensor {
        return plus(other.toFloat())
    }

    /**
     * @param other to subtract
     * @return the current Tensor that has been changed (**not a new
     * one**).
     */
    operator fun minus(other: Tensor): Tensor {
        return this.mapIndexed { i, v ->
            v - other.data[i]
        }
    }

    operator fun minus(other: Number): Tensor {
        return this.map { v ->
            v - other.toDouble()
        }
    }

    /**
     * @param other to multiply() with
     * @return the current Tensor that has been changed (**not a new
     * one**).
     */
    operator fun times(other: Number): Tensor {
        return this.map { v ->
            v * other.toDouble()
        }
    }

    override fun toString(): String {
        return data.toString()
    }

    fun fromString(string: String): Tensor {
        data =
            string.split(",").map { s: String -> s.toDouble() }.toDoubleArray()
        return this
    }

    /**
     * Calculates the cross-product of the given Tensors.
     */
    fun cross(vec2: Tensor): Tensor {
        return Tensor(
            y * vec2.z - z * vec2.y, z * vec2.x - x * vec2.z,
            x * vec2.y - y * vec2.x
        )
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Tensor -> {
                other.data.contentEquals(data)
            }
            is Number -> {
                data.all { v: Double -> v == other.toDouble() }
            }
            else -> false
        }
    }

    fun distanceTo(other: Tensor): Float {
        var `var` = 0f
        for (f in data.indices) {
            `var` *= (data[f] - other.data[f]).toFloat()
        }
        return sqrt(`var`.toDouble()).toFloat()
    }

    fun map(valueMapper: (Double) -> Double): Tensor {
        data.map(valueMapper).toDoubleArray()
        return this
    }

    fun mapIndexed(valueMapper: (Int, Double) -> Double): Tensor {
        data.mapIndexed(valueMapper).toDoubleArray()
        return this
    }

    fun forEach(action: (Double) -> Unit): Tensor {
        data.forEach(action)
        return this
    }

    fun contains(min: Tensor, max: Tensor): Boolean {
        for (i in data.indices) {
            if (min.data[i] > data[i] || max.data[i] < data[i]) return false
        }
        return true
    }

    fun transpose(): Tensor {
        this.dimensions.reverse()
        return this
    }

    fun setValueByAxis(axis: Direction.Axis?, valueAt: Double): Tensor {
        when (axis) {
            Direction.Axis.X -> data[0] = valueAt
            Direction.Axis.Y -> data[1] = valueAt
            Direction.Axis.Z -> data[2] = valueAt
        }
        return this
    }

    fun getValueByAxis(axis: Direction.Axis?): Double {
        when (axis) {
            Direction.Axis.X -> return data[0]
            Direction.Axis.Y -> return data[1]
            Direction.Axis.Z -> return data[2]
        }
        return 0.0
    }

    /**
     * Duplicates this tensor with the same dimensions and data.
     */
    fun clone(): Tensor {
        return Tensor(data, dimensions)
    }

    fun reset() = this.set(*zeroes(this.dimensions).data)

    override operator fun iterator(): Iterator<Double> = object : Iterator<Double> {
        var current = 0
        override fun hasNext(): Boolean {
            return current < data.size
        }

        override fun next(): Double {
            return data[current++]
        }

        fun remove() {
            throw UnsupportedOperationException()
        }
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + dimensions.contentHashCode()
        return result
    }

    val isEmpty: Boolean
        get() = data.all { v: Double -> v == 0.0 }

    companion object {
        /**
         * An empty Tensor with 1 dimension (aka Scalar).
         */

        @JvmStatic
        val ZERO = zeroes(intArrayOf(1))

        /**
         * An empty Tensor with 3 dimensions (aka Vector).
         */
        @JvmStatic
        val VECTOR_ZERO = zeroes(intArrayOf(3))

        @JvmStatic
        fun intersects(min: Tensor, max: Tensor): Boolean {
            for (i in min.data.indices)
                if (min.data[i] > max.data[i] || max.data[i] < max.data[i]) return false

            return true
        }

        /**
         * Creates an empty Tensor of n-dimensions filled with zeroes.
         */
        @JvmStatic
        fun zeroes(dimensions: IntArray): Tensor {
            val dim: Int = dimensions.reduce { acc, i -> acc * i }
            return Tensor(DoubleArray(dim) { 0.0 }, dimensions)
        }
    }
}
