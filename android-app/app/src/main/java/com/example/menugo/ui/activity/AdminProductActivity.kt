package com.example.menugo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.menugo.R
import com.example.menugo.domain.model.Product
import com.example.menugo.util.MoneyFormatter
import com.example.menugo.util.NavigationKeys
import com.example.menugo.viewmodel.AdminProductError
import com.example.menugo.viewmodel.AdminProductViewModel
import com.example.menugo.viewmodel.MenuGoViewModelFactory

class AdminProductActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminProductViewModel
    private lateinit var productList: LinearLayout
    private lateinit var emptyMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_product)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, MenuGoViewModelFactory())
            .get(AdminProductViewModel::class.java)

        productList = findViewById(R.id.adminProductList)
        emptyMessage = TextView(this).apply {
            text = getString(R.string.products_empty)
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 32)
        }

        findViewById<Button>(R.id.buttonAdminAddProduct).setOnClickListener {
            openProductForm()
        }

        findViewById<TextView>(R.id.textAdminBackToCustomer).setOnClickListener {
            finish()
        }

        setupSearch()
        renderProducts()
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) {
            viewModel.refresh()
            renderProducts()
        }
    }

    private fun setupSearch() {
        val searchField = findViewById<EditText>(R.id.editAdminSearch)
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString().orEmpty())
                renderProducts()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun renderProducts() {
        val state = viewModel.uiState.value
        productList.removeAllViews()

        if (state.products.isEmpty()) {
            emptyMessage.text = getString(R.string.products_empty)
            productList.addView(emptyMessage)
            return
        }

        state.products.forEach { product ->
            productList.addView(createProductRow(product))
        }
    }

    private fun createProductRow(product: Product): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 12, 8, 12)
        }

        val details = TextView(this).apply {
            text = "${product.name}\n${MoneyFormatter.format(product.priceInCents)}"
            textSize = 15f
            setTextColor(getColor(android.R.color.black))
        }
        row.addView(details, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val editButton = Button(this).apply {
            text = getString(R.string.admin_edit)
            isAllCaps = false
            setOnClickListener { openProductForm(product.id) }
        }
        row.addView(editButton)

        val deleteButton = Button(this).apply {
            text = getString(R.string.admin_delete)
            isAllCaps = false
            setOnClickListener {
                viewModel.deleteProduct(product.id)
                renderProducts()
                showViewModelError()
            }
        }
        row.addView(deleteButton)

        return row
    }

    private fun openProductForm(productId: String? = null) {
        val intent = Intent(this, ProductFormActivity::class.java)
        productId?.let { intent.putExtra(NavigationKeys.PRODUCT_ID, it) }
        startActivity(intent)
    }

    private fun showViewModelError() {
        when (viewModel.uiState.value.error) {
            AdminProductError.LOAD_FAILED ->
                Toast.makeText(this, R.string.error_load_products, Toast.LENGTH_SHORT).show()

            AdminProductError.DELETE_FAILED ->
                Toast.makeText(this, R.string.error_delete_product, Toast.LENGTH_SHORT).show()

            null -> Unit
        }
        viewModel.clearError()
    }
}