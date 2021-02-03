package dev.throwouterror.util.math

import dev.throwouterror.util.math.Tensor.Companion.intersects
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

@Serializable
class Cuboid {
    fun minX(): Int {
        return minPoint.intX()
    }

    fun minY(): Int {
        return minPoint.intY()
    }

    fun minZ(): Int {
        return minPoint.intZ()
    }

    fun maxX(): Int {
        return minPoint.intX()
    }

    fun maxY(): Int {
        return minPoint.intY()
    }

    fun maxZ(): Int {
        return minPoint.intZ()
    }

    fun clone(): Cuboid {
        return Cuboid(this)
    }

    val type: SizeType
        get() {
            var type =
                if (isEmpty) SizeType.AIR else SizeType.OTHER
            if (maxPoint.equals(1)) type = SizeType.FULL
            return type
        }

    /**
     * @return Returns whether or not the maximum point's values are 0.
     */
    val isEmpty: Boolean
        get() = maxPoint.isEmpty

    /**
     * '
     *
     * @return the Tensor containing the minimum point of this cuboid
     */
    val minPoint: Tensor

    /**
     * '
     *
     * @return the Tensor containing the maximum point of this cuboid
     */
    val maxPoint: Tensor

    /**
     * Creates a cuboid with a minimum and maximum point.
     */
    constructor(p1: Tensor, p2: Tensor) {
        minPoint = p1.clone()
        maxPoint = p2.clone()
    }

    constructor(loc: Tensor) : this(loc, loc)
    constructor(other: Cuboid) : this(other.minPoint, other.maxPoint)
    constructor(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float
    ) {
        minPoint = Tensor(x1, y1, z1)
        maxPoint = Tensor(x2, y2, z2)
    }

    constructor(
        x1: Double,
        y1: Double,
        z1: Double,
        x2: Double,
        y2: Double,
        z2: Double
    ) {
        minPoint = Tensor(x1.toFloat(), y1.toFloat(), z1.toFloat())
        maxPoint = Tensor(x2.toFloat(), y2.toFloat(), z2.toFloat())
    }

    constructor(
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) {
        minPoint = Tensor(x1.toFloat(), y1.toFloat(), z1.toFloat())
        maxPoint = Tensor(x2.toFloat(), y2.toFloat(), z2.toFloat())
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other !is Cuboid) {
            false
        } else {
            val c = other
            c.minPoint.equals(minPoint) && c.maxPoint.equals(maxPoint)
        }
    }

    /**
     * Creates a new [Cuboid] that has been contracted by the given amount,
     * with positive changes decreasing max values and negative changes increasing
     * min values. <br></br>
     * If the amount to contract by is larger than the length of a side, then the
     * side will wrap (still creating a valid AABB - see last sample).
     *
     * <h3>Samples:</h3>
     * <table>
     * <tr>
     * <th>Input</th>
     * <th>Result</th>
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(2, 2, 2)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[0.0,
     * 0.0, 0.0 -> 2.0, 2.0, 2.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(-2, -2, -
     * 2)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[2.0, 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(5, 5, 5, 7, 7, 7).contract(0, 1, -
     * 1)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[5.0, 5.0, 6.0 -> 7.0, 6.0, 7.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(-2, -2, -2, 2, 2, 2).contract(4, -4,
     * 0)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[-8.0, 2.0, -2.0 -> -2.0, 8.0, 2.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
    </table> *
     *
     * <h3>See Also:</h3>
     *
     *  * [.expand] - like this, except for
     * expanding.
     *  * [.grow] and [.grow] - expands in
     * all directions.
     *  * [.shrink] - contracts in all directions (like
     * [.grow])
     *
     *
     * @return A new modified bounding box.
     */
    fun contract(x: Float, y: Float, z: Float): Cuboid {
        var d0 = minX().toFloat()
        var d1 = minY().toFloat()
        var d2 = minZ().toFloat()
        var d3 = maxX().toFloat()
        var d4 = maxY().toFloat()
        var d5 = maxZ().toFloat()
        if (x < 0.0) {
            d0 -= x
        } else if (x > 0.0) {
            d3 -= x
        }
        if (y < 0.0) {
            d1 -= y
        } else if (y > 0.0) {
            d4 -= y
        }
        if (z < 0.0) {
            d2 -= z
        } else if (z > 0.0) {
            d5 -= z
        }
        return Cuboid(d0, d1, d2, d3, d4, d5)
    }

    /**
     * Creates a new [Cuboid] that has been expanded by the given amount, with
     * positive changes increasing max values and negative changes decreasing min
     * values.
     *
     * <h3>Samples:</h3>
     * <table>
     * <tr>
     * <th>Input</th>
     * <th>Result</th>
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(2, 2, 2)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[0, 0,
     * 0 -> 3, 3, 3]</samp>
    </pre> *
     *
    </td> *
     * <td>
    </td></tr> * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(-2, -2, -2)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[-2,
     * -2, -2 -> 1, 1, 1]</samp>
    </pre> *
     *
    </td> *
     * <td>
    </td></tr> * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(5, 5, 5, 7, 7, 7).expand(0, 1, -1)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[5, 5,
     * 4, 7, 8, 7]</samp>
    </pre> *
     *
    </td> *
     * <td>
    </td></tr></table> *
     *
     * <h3>See Also:</h3>
     *
     *  * [.contract] - like this, except for
     * shrinking.
     *  * [.grow] and [.grow] - expands in
     * all directions.
     *  * [.shrink] - contracts in all directions (like
     * [.grow])
     *
     *
     * @return A modified bounding box that will always be equal or greater in
     * volume to this bounding box.
     */
    fun expand(x: Float, y: Float, z: Float): Cuboid {
        var d0 = minX().toFloat()
        var d1 = minY().toFloat()
        var d2 = minZ().toFloat()
        var d3 = maxX().toFloat()
        var d4 = maxY().toFloat()
        var d5 = maxZ().toFloat()
        if (x < 0.0) {
            d0 += x
        } else if (x > 0.0) {
            d3 += x
        }
        if (y < 0.0) {
            d1 += y
        } else if (y > 0.0) {
            d4 += y
        }
        if (z < 0.0) {
            d2 += z
        } else if (z > 0.0) {
            d5 += z
        }
        return Cuboid(d0, d1, d2, d3, d4, d5)
    }

    /**
     * Creates a new [Cuboid] that has been contracted by the given amount in
     * both directions. Negative values will shrink the AABB instead of expanding
     * it. <br></br>
     * Side lengths will be increased by 2 times the value of the parameters, since
     * both min and max are changed. <br></br>
     * If contracting and the amount to contract by is larger than the length of a
     * side, then the side will wrap (still creating a valid AABB - see last ample).
     *
     * <h3>Samples:</h3>
     * <table>
     * <tr>
     * <th>Input</th>
     * <th>Result</th>
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(2, 2, 2)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[-2.0, -
     * 2.0, -2.0 -> 3.0, 3.0, 3.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(0, 0, 0, 6, 6, 6).grow(-2, -2, -2)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[2.0,
     * 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(5, 5, 5, 7, 7, 7).grow(0, 1, -1)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[5.0,
     * 4.0, 6.0 -> 7.0, 8.0, 6.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
     * <tr>
     * <td>
     *
     * <pre>
     * `new AxisAlignedBB(1, 1, 1, 3, 3, 3).grow(-4, -2, -3)`
    </pre> *
     *
    </td> *
     * <td>
     *
     * <pre>
     * <samp>box[-1.0,
     * 1.0, 0.0 -> 5.0, 3.0, 4.0]</samp>
    </pre> *
     *
    </td> *
    </tr> *
    </table> *
     *
     * <h3>See Also:</h3>
     *
     *  * [.expand] - expands in only one
     * direction.
     *  * [.contract] - contracts in only one
     * direction. <lu>[.grow] - version of this that expands in
     * all directions from one parameter.
     *  * [.shrink] - contracts in all directions
    </lu> *
     *
     * @return A modified bounding box.
     */
    fun grow(x: Float, y: Float, z: Float): Cuboid {
        val d0 = minX() - x
        val d1 = minY() - y
        val d2 = minZ() - z
        val d3 = maxX() + x
        val d4 = maxY() + y
        val d5 = maxZ() + z
        return Cuboid(d0, d1, d2, d3, d4, d5)
    }

    /**
     * Creates a new [Cuboid] that is expanded by the given value in all
     * directions. Equivalent to [.grow] with the given
     * value for all 3 params. Negative values will shrink the AABB. <br></br>
     * Side lengths will be increased by 2 times the value of the parameter, since
     * both min and max are changed. <br></br>
     * If contracting and the amount to contract by is larger than the length of a
     * side, then the side will wrap (still creating a valid AABB - see samples on
     * [.grow]).
     *
     * @return A modified AABB.
     */
    fun grow(value: Float): Cuboid {
        return this.grow(value, value, value)
    }

    fun intersect(other: Cuboid): Cuboid {
        val d0: Float = max(minX(), other.minX()).toFloat()
        val d1: Float = max(minY(), other.minY()).toFloat()
        val d2: Float = max(minZ(), other.minZ()).toFloat()
        val d3: Float = min(maxX(), other.maxX()).toFloat()
        val d4: Float = min(maxY(), other.maxY()).toFloat()
        val d5: Float = min(maxZ(), other.maxZ()).toFloat()
        return Cuboid(d0, d1, d2, d3, d4, d5)
    }

    fun union(other: Cuboid): Cuboid {
        val d0: Float = min(minX(), other.minX()).toFloat()
        val d1: Float = min(minY(), other.minY()).toFloat()
        val d2: Float = min(minZ(), other.minZ()).toFloat()
        val d3: Float = max(maxX(), other.maxX()).toFloat()
        val d4: Float = max(maxY(), other.maxY()).toFloat()
        val d5: Float = max(maxZ(), other.maxZ()).toFloat()
        return Cuboid(d0, d1, d2, d3, d4, d5)
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    fun offset(x: Float, y: Float, z: Float): Cuboid {
        return Cuboid(
            minX() + x, minY() + y, minZ() + z, maxX() + x, maxY() + y,
            maxZ() + z
        )
    }

    fun offset(vec: Tensor?): Cuboid {
        return Cuboid(minPoint.clone().add(vec!!), maxPoint.clone().add(vec))
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z
     * dimensions, calculate the offset between them in the X dimension. return var2
     * if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset. Otherwise return the calculated offset.
     */
    fun calculateXOffset(other: Cuboid, offsetX: Float): Float {
        var offX = offsetX
        return if (other.maxY() > minY() && other.minY() < maxY() && other.maxZ() > minZ() && other.minZ() < maxZ()
        ) {
            if (offX > 0.0 && other.maxX() <= minX()) {
                val d1 = minX() - other.maxX().toFloat()
                if (d1 < offX)
                    offX = d1
            } else if (offX < 0.0 && other.minX() >= maxX()) {
                val d0 = maxX() - other.minX().toFloat()
                if (d0 > offX)
                    offX = d0
            }
            offX
        } else
            offX
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z
     * dimensions, calculate the offset between them in the Y dimension. return var2
     * if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset. Otherwise return the calculated offset.
     */
    fun calculateYOffset(other: Cuboid, offsetY: Float): Float {
        var offY = offsetY
        return if (other.maxX() > minX() && other.minX() < maxX() && other.maxZ() > minZ() && other.minZ() < maxZ()
        ) {
            if (offY > 0.0 && other.maxY() <= minY()) {
                val d1 = minY() - other.maxY().toFloat()
                if (d1 < offY)
                    offY = d1
            } else if (offY < 0.0 && other.minY() >= maxY()) {
                val d0 = maxY() - other.minY().toFloat()
                if (d0 > offY)
                    offY = d0
            }
            offY
        } else
            offY
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X
     * dimensions, calculate the offset between them in the Z dimension. return var2
     * if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset. Otherwise return the calculated offset.
     */
    fun calculateZOffset(other: Cuboid, offsetZ: Float): Float {
        var offZ = offsetZ
        return if (other.maxX() > minX() && other.minX() < maxX() && other.maxY() > minY() && other.minY() < maxY()
        ) {
            if (offZ > 0.0 && other.maxZ() <= minZ()) {
                val d1 = minZ() - other.maxZ().toFloat()
                if (d1 < offZ) {
                    offZ = d1
                }
            } else if (offZ < 0.0 && other.minZ() >= maxZ()) {
                val d0 = maxZ() - other.minZ().toFloat()
                if (d0 > offZ) {
                    offZ = d0
                }
            }
            offZ
        } else {
            offZ
        }
    }

    /**
     * Checks if the bounding box intersects with another.
     */
    fun intersects(other: Cuboid): Boolean {
        return intersects(minPoint, other.maxPoint)
    }

    /**
     * Returns if the supplied Tensor is completely inside the bounding box
     */
    operator fun contains(t: Tensor): Boolean {
        return t.contains(minPoint, maxPoint)
    }

    fun contains(vararg data: Double): Boolean {
        return contains(Tensor(*data))
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    val averageEdgeLength: Float
        get() {
            val d0 = maxX() - minX().toFloat()
            val d1 = maxY() - minY().toFloat()
            val d2 = maxZ() - minZ().toFloat()
            return (d0 + d1 + d2) / 3.0f
        }

    val xSize: Float
        get() = (maxX() - minX()).toFloat()

    val ySize: Float
        get() = (maxY() - minY()).toFloat()

    val zSize: Float
        get() = (maxZ() - minZ()).toFloat()

    /**
     * Creates a new [Cuboid] that is expanded by the given value in all
     * directions. Equivalent to [.grow] with value set to the negative
     * of the value provided here. Passing a negative value to this method values
     * will grow the AABB. <br></br>
     * Side lengths will be decreased by 2 times the value of the parameter, since
     * both min and max are changed. <br></br>
     * If contracting and the amount to contract by is larger than the length of a
     * side, then the side will wrap (still creating a valid AABB - see samples on
     * [.grow]).
     *
     * @return A modified AABB.
     */
    fun shrink(value: Float): Cuboid {
        return this.grow(-value)
    }

    override fun toString(): String =
        ("" + minX() + "," + minY() + "," + minZ() + ":" + maxX() + "," + maxY() + ","
            + maxZ() + "")

    fun toArray(size: Int): Array<DoubleArray> =
        Tensor(minPoint.x(), minPoint.y(), minPoint.z(), maxPoint.x(), maxPoint.y(), maxPoint.z()).toArray(size)

    fun toArrayString(size: Int): String =
        Tensor(minPoint.x(), minPoint.y(), minPoint.z(), maxPoint.x(), maxPoint.y(), maxPoint.z()).toArrayString(size)

    fun toArrayString(): String = this.toArrayString(3)

    fun hasNaN(): Boolean = minX().toDouble().isNaN() ||
        minY().toDouble().isNaN()
        || minZ().toDouble().isNaN()
        || maxX().toDouble().isNaN() ||
        maxY().toDouble().isNaN()
        || maxZ().toDouble().isNaN()

    val center: Tensor
        get() = Tensor(
            minX() + (maxX() - minX()) * 0.5f,
            minY() + (maxY() - minY()) * 0.5f, minZ() + (maxZ() - minZ()) * 0.5f
        )

    enum class SizeType {
        FULL, AIR, OTHER;

        override fun toString(): String {
            return name.toLowerCase()
        }
    }

    companion object {
        val AIR = Cuboid(0, 0, 0, 0, 0, 0)
        val FULL_CUBE = Cuboid(0, 0, 0, 1, 1, 1)

        fun fromString(s: String): Cuboid {
            val minMax = s.split(":")
            val min = minMax[0].split(",").map { v -> v.toDouble() }
            val max = minMax[1].split(",").map { v -> v.toDouble() }
            return Cuboid(min[0], min[1], min[2], max[1], max[2], max[3])
        }
    }
}
