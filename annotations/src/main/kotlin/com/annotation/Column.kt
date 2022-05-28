package com.annotation

@Target(AnnotationTarget.FIELD)
annotation class Column(val name: String)