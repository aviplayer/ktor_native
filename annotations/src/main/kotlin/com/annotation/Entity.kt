package com.annotation

@Target(AnnotationTarget.CLASS)
annotation class Entity(val table: String)

