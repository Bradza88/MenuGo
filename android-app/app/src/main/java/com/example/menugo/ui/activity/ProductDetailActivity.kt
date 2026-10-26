package com.example.menugo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.menugo.MainActivity
import com.example.menugo.R
import com.example.menugo.util.MoneyFormatter
import com.example.menugo.util.NavigationKeys
import com.example.menugo.util.UiTextMapper
import com.example.menugo.viewmodel.MenuGoViewModelFactory
import com.example.menugo.viewmodel.ProductDetailError
import com.example.menugo.viewmodel.ProductDetailUiState
import com.example.menugo.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var detailContainer: View
    private lateinit var detailMessage: TextView
    private lateinit var decreaseButton: Button
    private lateinit var increaseButton: Button
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)

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
        )[ProductDetailViewModel::class.java]

        detailContainer = findViewById(R.id.containerProductDetail)
        detailMessage = findViewById(R.id.textDetailMessage)
        decreaseButton = findViewById(R.id.buttonDecreaseQuantity)
        increaseButton = findViewById(R.id.buttonIncreaseQuantity)
        addButton = findViewById(R.id.buttonAddToCart)

        configureQuantity()
        configureNavigation()
        observeProduct()
    }

    override fun onResume() {
        super.onResume()

        val productId = intent.getStringExtra(
            NavigationKeys.PRODUCT_ID
        ).orEmpty()

        viewModel.loadProduct(productId)
    }

    private fun configureQuantity() {
        decreaseButton.setOnClickListener {
            val quantity = viewModel.uiState.value.quantity

            if (quantity > 1) {
                viewModel.setQuantity(quantity - 1)
            }
        }

        increaseButton.setOnClickListener {
            val quantity = viewModel.uiState.value.quantity

            if (quantity < Int.MAX_VALUE) {
                viewModel.setQuantity(quantity + 1)
            }
        }

        addButton.setOnClickListener {
            viewModel.addToCart()

            if (viewModel.uiState.value.addedToCart) {
                Toast.makeText(
                    this,
                    R.string.product_added_to_cart,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun configureNavigation() {
        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            finish()
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

        findViewById<Button>(R.id.buttonNavMenu).setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
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

    private fun observeProduct() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderProduct(state)
                }
            }
        }
    }

    private fun renderProduct(state: ProductDetailUiState) {
        val product = state.product
        val error = state.error

        detailMessage.visibility = if (error != null) {
            detailMessage.setText(UiTextMapper.errorMessage(error))
            View.VISIBLE
        } else {
            View.GONE
        }

        if (product == null) {
            detailContainer.visibility = View.GONE
            return
        }

        detailContainer.visibility = View.VISIBLE

        findViewById<TextView>(R.id.textDetailName).text =
            product.name

        findViewById<TextView>(R.id.textDetailDescription).text =
            product.description

        findViewById<TextView>(R.id.textDetailCategory).setText(
            UiTextMapper.categoryName(product.category)
        )

        findViewById<TextView>(R.id.textDetailPrice).text =
            MoneyFormatter.format(product.priceInCents)

        findViewById<TextView>(R.id.textDetailQuantity).text =
            state.quantity.toString()

        val subtotal = runCatching {
            Math.multiplyExact(
                product.priceInCents,
                state.quantity.toLong()
            )
        }.getOrNull()

        val canBuy = product.isAvailable &&
                state.quantity > 0 &&
                subtotal != null &&
                error != ProductDetailError.PRODUCT_NOT_FOUND

        decreaseButton.isEnabled = state.quantity > 1

        val nextSubtotalFits = state.quantity < Int.MAX_VALUE &&
                product.priceInCents <=
                Long.MAX_VALUE / (state.quantity.toLong() + 1L)

        increaseButton.isEnabled = canBuy && nextSubtotalFits
        addButton.isEnabled = canBuy

        if (subtotal != null && state.quantity > 0) {
            val formattedSubtotal = MoneyFormatter.format(subtotal)

            findViewById<TextView>(R.id.textDetailSubtotal).text =
                getString(
                    R.string.subtotal_amount,
                    formattedSubtotal
                )

            addButton.text = getString(
                R.string.add_to_cart_amount,
                formattedSubtotal
            )
        } else {
            findViewById<TextView>(R.id.textDetailSubtotal).text = ""
            addButton.setText(R.string.add_to_cart)

            detailMessage.setText(R.string.error_invalid_quantity)
            detailMessage.visibility = View.VISIBLE
        }
    }
}