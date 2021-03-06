/*
 * Copyright (c) Throw Out Error
 * https://throw-out-error.dev
 */

package dev.throwouterror.test

import dev.throwouterror.util.math.Cuboid
import dev.throwouterror.util.math.Tensor

fun main() {

    val t1 = Tensor(1, 2, 3, 4, 5, 6)
    println("Original tensor: \n" + t1.toArrayString(2))

    t1.mul(2)
    println("Tensor * 2: \n" + t1.toArrayString(2))

    val c1 = Cuboid(0, 0, 0, 5, 5, 5)
    println("Cuboid 1: ")
    println(c1.toArrayString())
}
