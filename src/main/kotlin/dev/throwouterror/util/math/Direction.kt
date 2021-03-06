package dev.throwouterror.util.math

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * A simple enum representing the 6 possible directions with their offset tensors.
 * @author Mojang https://minecraft.net
 */
@Serializable
enum class Direction(
    val index: Int,
    private val opposite: Int,
    val horizontalIndex: Int,
    val id: String,
    val axisDirection: AxisDirection,
    val axis: Axis,
    val facingVec: Tensor
) {
    DOWN(
        0,
        1,
        -1,
        "down",
        AxisDirection.NEGATIVE,
        Axis.Y,
        Tensor(0, -1, 0)
    ),
    UP(
        1,
        0,
        -1,
        "up",
        AxisDirection.POSITIVE,
        Axis.Y,
        Tensor(0, 1, 0)
    ),
    NORTH(
        2,
        3,
        2,
        "north",
        AxisDirection.NEGATIVE,
        Axis.Z,
        Tensor(0, 0, -1)
    ),
    SOUTH(
        3,
        2,
        0,
        "south",
        AxisDirection.POSITIVE,
        Axis.Z,
        Tensor(0, 0, 1)
    ),
    WEST(
        4,
        5,
        1,
        "west",
        AxisDirection.NEGATIVE,
        Axis.X,
        Tensor(-1, 0, 0)
    ),
    EAST(
        5,
        4,
        3,
        "east",
        AxisDirection.POSITIVE,
        Axis.X,
        Tensor(1, 0, 0)
    );

    fun getOpposite(): Direction {
        return byIndex(opposite)
    }

    fun rotateY(): Direction {
        return when (this) {
            NORTH -> EAST
            SOUTH -> WEST
            WEST -> NORTH
            EAST -> SOUTH
            else -> throw IllegalStateException("Unable to get Y-rotated facing of $this")
        }
    }

    fun rotateYCCW(): Direction {
        return when (this) {
            NORTH -> WEST
            SOUTH -> EAST
            WEST -> SOUTH
            EAST -> NORTH
            else -> throw IllegalStateException("Unable to get CCW facing of $this")
        }
    }

    val xOffset: Int
        get() = facingVec.x.roundToInt()

    val yOffset: Int
        get() = facingVec.y.roundToInt()

    val zOffset: Int
        get() = facingVec.z.roundToInt()

    val directionVec: Tensor
        get() = Tensor(
            xOffset.toFloat(),
            yOffset.toFloat(),
            zOffset.toFloat()
        )

    val horizontalAngle: Float
        get() = ((horizontalIndex and 3) * 90).toFloat()

    override fun toString(): String {
        return id
    }

    @Serializable
    enum class Axis(private val id: String) {
        X("x") {
            override fun getCoordinate(x: Int, y: Int, z: Int): Int {
                return x
            }

            override fun getCoordinate(x: Double, y: Double, z: Double): Double {
                return x
            }
        },
        Y("y") {
            override fun getCoordinate(x: Int, y: Int, z: Int): Int {
                return y
            }

            override fun getCoordinate(x: Double, y: Double, z: Double): Double {
                return y
            }
        },
        Z("z") {
            override fun getCoordinate(x: Int, y: Int, z: Int): Int {
                return z
            }

            override fun getCoordinate(x: Double, y: Double, z: Double): Double {
                return z
            }
        };

        val isVertical: Boolean
            get() = this === Y

        val isHorizontal: Boolean
            get() = this === X || this === Z

        override fun toString(): String {
            return id
        }

        fun test(p_test_1_: Direction): Boolean {
            return p_test_1_.axis === this
        }

        val plane: Plane
            get() = when (this) {
                X, Z -> Plane.HORIZONTAL
                Y -> Plane.VERTICAL
            }

        abstract fun getCoordinate(x: Int, y: Int, z: Int): Int
        abstract fun getCoordinate(x: Double, y: Double, z: Double): Double

        companion object {
            private val NAME_LOOKUP: Map<String, Axis> =
                values().associateBy { obj: Axis -> obj.toString() }

            // TODO: Facing axis from Tensor

            /*public static Facing.Axis fromTensor(Tensor v) {

            } */
            fun byName(name: String): Axis? {
                return NAME_LOOKUP[name.toLowerCase()]
            }

            fun random(p_218393_0_: Random): Axis {
                return values()[p_218393_0_.nextInt(values().size)]
            }
        }

    }

    enum class AxisDirection(val offset: Int, private val description: String) {
        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        override fun toString(): String {
            return description
        }

    }

    enum class Plane(
        private val facingValues: Array<Direction>,
        private val axisValues: Array<Axis>
    ) : Iterable<Direction?> {
        HORIZONTAL(
            arrayOf(
                NORTH,
                EAST,
                SOUTH,
                WEST
            ),
            arrayOf(
                Axis.X,
                Axis.Z
            )
        ),
        VERTICAL(
            arrayOf(
                UP,
                DOWN
            ), arrayOf(Axis.Y)
        );

        fun random(rand: Random): Direction {
            return facingValues[rand.nextInt(facingValues.size)]
        }

        fun test(p_test_1_: Direction): Boolean {
            return p_test_1_.axis.plane == this
        }

        override fun iterator(): Iterator<Direction> {
            return facingValues.iterator()
        }

    }

    companion object {
        private val VALUES =
            values()
        private val NAME_LOOKUP: Map<String, Direction> =
            values().associateBy { obj -> obj.id }
        private val BY_INDEX: Array<Direction> =
            values().sortedBy { obj -> obj.index }.toTypedArray()
        private val BY_HORIZONTAL_INDEX: Array<Direction> =
            values().filter { obj -> obj.axis.isHorizontal }
                .sortedBy { obj -> obj.horizontalIndex }
                .toTypedArray()

        private fun compose(
            first: Direction,
            second: Direction,
            third: Direction
        ): Array<Direction> {
            return arrayOf(
                first,
                second,
                third,
                third.getOpposite(),
                second.getOpposite(),
                first.getOpposite()
            )
        }

        fun byName(name: String?): Direction? {
            return if (name == null) null else NAME_LOOKUP[name.toLowerCase()]
        }

        fun byIndex(index: Int): Direction {
            return BY_INDEX[abs(index % BY_INDEX.size)]
        }

        fun byHorizontalIndex(horizontalIndexIn: Int): Direction {
            return BY_HORIZONTAL_INDEX[abs(horizontalIndexIn % BY_HORIZONTAL_INDEX.size)]
        }

        fun fromAngle(angle: Double): Direction {
            return byHorizontalIndex(floor(angle / 90.0 + 0.5).toInt() and 3)
        }

        fun getFacingFromAxisDirection(
            axisIn: Axis?,
            AxisDirectionIn: AxisDirection
        ): Direction {
            return when (axisIn) {
                Axis.X -> if (AxisDirectionIn == AxisDirection.POSITIVE) EAST else WEST
                Axis.Y -> if (AxisDirectionIn == AxisDirection.POSITIVE) UP else DOWN
                Axis.Z -> if (AxisDirectionIn == AxisDirection.POSITIVE) SOUTH else NORTH
                else -> if (AxisDirectionIn == AxisDirection.POSITIVE) SOUTH else NORTH
            }
        }

        fun random(rand: Random): Direction {
            return values()[rand.nextInt(values().size)]
        }

        fun getFacingFromTensor(
            x: Double,
            y: Double,
            z: Double
        ): Direction {
            return getFacingFromTensor(
                x.toFloat(),
                y.toFloat(),
                z.toFloat()
            )
        }

        fun getFacingFromTensor(x: Float, y: Float, z: Float): Direction {
            var dir = NORTH
            var f = Float.MIN_VALUE
            for (dir1 in VALUES) {
                val f1 =
                    x * dir1.facingVec.x.toFloat() + y * dir1.facingVec.y
                        .toFloat() + z * dir1.facingVec.z.toFloat()
                if (f1 > f) {
                    f = f1
                    dir = dir1
                }
            }
            return dir
        }

        fun getFacingFromAxis(
            AxisDirectionIn: AxisDirection,
            axisIn: Axis
        ): Direction {
            for (Facing in values()) {
                if (Facing.axisDirection == AxisDirectionIn && Facing.axis === axisIn) {
                    return Facing
                }
            }
            throw IllegalArgumentException("No such Facing: $AxisDirectionIn $axisIn")
        }
    }

}
