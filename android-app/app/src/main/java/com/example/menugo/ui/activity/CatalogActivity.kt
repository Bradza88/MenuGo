package com.example.menugo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.menugo.MainActivity
import com.example.menugo.R
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.util.MoneyFormatter
import com.example.menugo.util.NavigationKeys
import com.example.menugo.util.UiTextMapper
import com.example.menugo.viewmodel.CatalogUiState
import com.example.menugo.viewmodel.CatalogViewModel
import com.example.menugo.viewmodel.MenuGoViewModelFactory
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts

class CatalogActivity : AppCompatActivity() {

    private lateinit var viewModel: CatalogViewModel
    private lateinit var searchInput: EditText
    private lateinit var productContainer: LinearLayout
    private lateinit var productCount: TextView
    private lateinit var catalogMessage: TextView

    private val filterLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data

        if (result.resultCode == RESULT_OK && data != null) {
            val categoryName = data.getStringExtra(
                NavigationKeys.CATEGORY
            )

            val category = ProductCategory.entries.firstOrNull {
                it.name == categoryName
            }

            val minPrice = if (
                data.hasExtra(NavigationKeys.MIN_PRICE)
            ) {
                data.getLongExtra(NavigationKeys.MIN_PRICE, 0L)
            } else {
                null
            }

            val maxPrice = if (
                data.hasExtra(NavigationKeys.MAX_PRICE)
            ) {
                data.getLongExtra(NavigationKeys.MAX_PRICE, 0L)
            } else {
                null
            }

            viewModel.selectCategory(category)
            viewModel.setMinPrice(minPrice)
            viewModel.setMaxPrice(maxPrice)
            viewModel.setOnlyPromotions(
                data.getBooleanExtra(
                    NavigationKeys.ONLY_PROMOTIONS,
                    false
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalog)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        viewModel = ViewModelProvider(
            this,
            MenuGoViewModelFactory()
        )[CatalogViewModel::class.java]

        searchInput = findViewById(R.id.editSearch)
        productContainer = findViewById(R.id.containerProducts)
        productCount = findViewById(R.id.textProductCount)
        catalogMessage = findViewById(R.id.textCatalogMessage)

        if (savedInstanceState == null) {
            readInitialCategory()
        }

        configureSearch()
        configureNavigation()
        observeCatalog()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun readInitialCategory() {
        val categoryName = intent.getStringExtra(
            NavigationKeys.CATEGORY
        )

        val category = ProductCategory.entries.firstOrNull {
            it.name == categoryName
        }

        viewModel.selectCategory(category)
    }

    private fun configureSearch() {
        searchInput.isSaveEnabled = false
        searchInput.setText(viewModel.uiState.value.filter.query)

        searchInput.doAfterTextChanged { text ->
            val query = text?.toString().orEmpty()

            if (query != viewModel.uiState.value.filter.query) {
                viewModel.search(query)
            }
        }
    }

    private fun configureNavigation() {
        findViewById<Button>(R.id.buttonFilters).setOnClickListener {
            val filter = viewModel.uiState.value.filter
            val intent = Intent(this, FilterActivity::class.java)

            filter.category?.let {
                intent.putExtra(NavigationKeys.CATEGORY, it.name)
            }

            filter.minPriceInCents?.let {
                intent.putExtra(NavigationKeys.MIN_PRICE, it)
            }

            filter.maxPriceInCents?.let {
                intent.putExtra(NavigationKeys.MAX_PRICE, it)
            }

            intent.putExtra(
                NavigationKeys.ONLY_PROMOTIONS,
                filter.onlyPromotions
            )

            filterLauncher.launch(intent)
        }

        findViewById<Button>(R.id.buttonNavHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.buttonNavCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<Button>(R.id.buttonNavBranches).setOnClickListener {
            startActivity(Intent(this, BranchActivity::class.java))
        }
    }

    private fun observeCatalog() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderCatalog(state)
                }
            }
        }
    }

    private fun renderCatalog(state: CatalogUiState) {
        productCount.text = resources.getQuantityString(
            R.plurals.catalog_product_count,
            state.products.size,
            state.products.size
        )

        val error = state.error

        when {
            error != null -> {
                catalogMessage.setText(
                    UiTextMapper.errorMessage(error)
                )
                catalogMessage.visibility = View.VISIBLE
            }

            state.isEmpty -> {
                catalogMessage.setText(R.string.products_empty)
                catalogMessage.visibility = View.VISIBLE
            }

            else -> {
                catalogMessage.visibility = View.GONE
            }
        }

        productContainer.removeAllViews()

        state.products.forEach { product ->
            addProductCard(product)
        }
    }

    private fun addProductCard(product: Product) {
        val card = layoutInflater.inflate(
            R.layout.item_product,
            productContainer,
            false
        )

        card.findViewById<TextView>(R.id.textProductName).text =
            product.name

        card.findViewById<TextView>(R.id.textProductDescription).text =
            product.description

        card.findViewById<TextView>(R.id.textProductPrice).text =
            MoneyFormatter.format(product.priceInCents)

        card.findViewById<Button>(
            R.id.buttonViewProduct
        ).setOnClickListener {
            val intent = Intent(
                this,
                ProductDetailActivity::class.java
            )

            intent.putExtra(
                NavigationKeys.PRODUCT_ID,
                product.id
            )

            startActivity(intent)
        }

        productContainer.addView(card)
    }
}