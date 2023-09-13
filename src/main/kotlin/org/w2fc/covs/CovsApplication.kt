package org.w2fc.covs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CovsApplication

fun main(args: Array<String>) {
	runApplication<CovsApplication>(*args)
}
