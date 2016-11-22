package com.chrisprime.primestationonecontrol

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class ExampleTest {

    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @Test
    fun testAssertions() {
        Assert.assertEquals(1, (2 - 1).toLong())
    }

    @Test
    fun testSecondAssertions() {
        Assert.assertTrue(true)
    }
}