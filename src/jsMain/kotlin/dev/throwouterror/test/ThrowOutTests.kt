/*
 * Copyright (c) Creepinson
 */

package dev.throwouterror.test

import dev.throwouterror.util.math.Tensor

fun main() {
    val t1 = Tensor(1, 2, 3, 4, 5, 6)
    println("Original tensor: " + t1.toArrayString(2))

    t1.mul(2)
    println("Tensor * 2: " + t1.toArrayString(2))
}
