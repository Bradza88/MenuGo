package com.example.menugo.util

import androidx.annotation.StringRes
import com.example.menugo.R
import com.example.menugo.domain.model.OrderStatus
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.viewmodel.AdminProductError
import com.example.menugo.viewmodel.BranchError
import com.example.menugo.viewmodel.CartError
import com.example.menugo.viewmodel.CatalogError
import com.example.menugo.viewmodel.ProductDetailError
import com.example.menugo.viewmodel.ProductFormError

object UiTextMapper {

    @StringRes
    fun categoryName(category: ProductCategory?): Int {
        return when (category) {
            null -> R.string.category_all
            ProductCategory.HAMBURGERS -> R.string.category_hamburgers
            ProductCategory.DRINKS -> R.string.category_drinks
            ProductCategory.DESSERTS -> R.string.category_desserts
            ProductCategory.SIDES -> R.string.category_sides
        }
    }

    @StringRes
    fun orderStatusName(status: OrderStatus): Int {
        return when (status) {
            OrderStatus.PENDING -> R.string.order_status_pending
            OrderStatus.PREPARING -> R.string.order_status_preparing
            OrderStatus.READY -> R.string.order_status_ready
            OrderStatus.COMPLETED -> R.string.order_status_completed
            OrderStatus.CANCELLED -> R.string.order_status_cancelled
        }
    }

    @StringRes
    fun errorMessage(error: CatalogError): Int {
        return when (error) {
            CatalogError.LOAD_FAILED -> R.string.error_load_products
        }
    }

    @StringRes
    fun errorMessage(error: AdminProductError): Int {
        return when (error) {
            AdminProductError.LOAD_FAILED -> R.string.error_load_products
            AdminProductError.DELETE_FAILED -> R.string.error_delete_product
        }
    }

    @StringRes
    fun errorMessage(error: BranchError): Int {
        return when (error) {
            BranchError.LOAD_FAILED -> R.string.error_load_branches
            BranchError.BRANCH_NOT_FOUND -> R.string.error_invalid_branch
        }
    }

    @StringRes
    fun errorMessage(error: ProductDetailError): Int {
        return when (error) {
            ProductDetailError.PRODUCT_NOT_FOUND ->
                R.string.error_product_not_found

            ProductDetailError.PRODUCT_UNAVAILABLE ->
                R.string.error_product_unavailable

            ProductDetailError.INVALID_QUANTITY ->
                R.string.error_invalid_quantity

            ProductDetailError.ADD_TO_CART_FAILED ->
                R.string.error_add_to_cart
        }
    }

    @StringRes
    fun errorMessage(error: ProductFormError): Int {
        return when (error) {
            ProductFormError.NAME_REQUIRED ->
                R.string.error_name_required

            ProductFormError.DESCRIPTION_REQUIRED ->
                R.string.error_description_required

            ProductFormError.INVALID_PRICE ->
                R.string.error_invalid_price

            ProductFormError.PRODUCT_NOT_FOUND ->
                R.string.error_product_not_found

            ProductFormError.DUPLICATE_ID ->
                R.string.error_duplicate_product

            ProductFormError.SAVE_FAILED ->
                R.string.error_save_product
        }
    }

    @StringRes
    fun errorMessage(error: CartError): Int {
        return when (error) {
            CartError.LOAD_FAILED -> R.string.error_load_cart
            CartError.ADD_FAILED -> R.string.error_add_to_cart
            CartError.UPDATE_FAILED -> R.string.error_update_cart
            CartError.REMOVE_FAILED -> R.string.error_remove_from_cart
            CartError.CLEAR_FAILED -> R.string.error_clear_cart
            CartError.BRANCHES_LOAD_FAILED -> R.string.error_load_branches
            CartError.BRANCH_REQUIRED -> R.string.error_branch_required
            CartError.INVALID_BRANCH -> R.string.error_invalid_branch
            CartError.EMPTY_CART -> R.string.error_cart_empty
            CartError.ORDER_FAILED -> R.string.error_create_order
        }
    }
}