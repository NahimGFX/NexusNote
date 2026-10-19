
package com.example.nexusnote.data.repository

import com.example.nexusnote.data.local.dao.ProductDao
import com.example.nexusnote.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ProductRepository {
    fun getAllProducts(): Flow<List<ProductEntity>>
    suspend fun getProductById(productId: Int): ProductEntity?
    suspend fun insertProduct(product: ProductEntity): Int
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
}

class OfflineProductRepository @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<ProductEntity>> =
        productDao.getAllProducts()

    override suspend fun getProductById(productId: Int): ProductEntity? =
        productDao.getProductById(productId)

    override suspend fun insertProduct(product: ProductEntity): Int =
        productDao.insertProduct(product).toInt()

    override suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }
}
