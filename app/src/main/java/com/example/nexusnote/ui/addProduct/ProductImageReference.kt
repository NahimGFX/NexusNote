package com.example.nexusnote.ui.addProduct

import com.example.nexusnote.R

/** Stable Room representation for bundled product-image resources. */
object ProductImageReference {
    const val RESOURCE_PREFIX = "nexusnote-resource://drawable/"

    data class DefaultImage(
        val resourceName: String,
        val drawableRes: Int
    ) {
        val reference: String
            get() = "$RESOURCE_PREFIX$resourceName"
    }

    private val imagesByCategory = mapOf(
        "Frutas y verduras" to DefaultImage("frutas_verduras", R.drawable.frutas_verduras),
        "Lácteos" to DefaultImage("lacteos", R.drawable.lacteos),
        "Panadería" to DefaultImage("panaderia", R.drawable.panaderia),
        "Carnes" to DefaultImage("carnes", R.drawable.carnes),
        "Bebidas" to DefaultImage("bebidas", R.drawable.bebidas)
    )

    fun forCategory(category: String): DefaultImage? = imagesByCategory[category]

    fun referenceForCategory(category: String): String? =
        forCategory(category)?.reference

    fun drawableResourceId(reference: String?): Int? {
        if (reference == null || !reference.startsWith(RESOURCE_PREFIX)) return null

        return when (reference.removePrefix(RESOURCE_PREFIX)) {
            "frutas_verduras" -> R.drawable.frutas_verduras
            "lacteos" -> R.drawable.lacteos
            "panaderia" -> R.drawable.panaderia
            "carnes" -> R.drawable.carnes
            "bebidas" -> R.drawable.bebidas
            else -> null
        }
    }

    fun isResourceReference(reference: String?): Boolean =
        reference?.startsWith(RESOURCE_PREFIX) == true
}
