package com.ixibot

import java.io.InputStream
import java.net.URL

/**
 * Singletons are bad and I hate this.
 */
val resourceLoader: ResourceLoader = ResourceLoader()

/**
 * Resource loader.
 */
class ResourceLoader {
    /**
     * Get resource as a stream.
     */
    fun getResourceAsStream(asset: String): InputStream? {
        return javaClass.classLoader.getResourceAsStream(asset)
    }
}
