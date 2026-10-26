package com.example.menugo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.menugo.domain.model.CartItem
import com.example.menugo.util.MoneyFormatter
import com.example.menugo.util.UiTextMapper
import com.example.menugo.viewmodel.CartUiState
import com.example.menugo.viewmodel.CartViewModel
import com.example.menugo.viewmodel.MenuGoViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var viewModel: CartViewModel
    private lateinit var itemContainer: LinearLayout
    private lateinit var checkoutContainer: View
    private lateinit var emptyMessage: TextView
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

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
        )[CartViewModel::class.java]

        itemContainer = findViewById(R.id.containerCartItems)
        checkoutContainer = findViewById(R.id.containerCheckout)
        emptyMessage = findViewById(R.id.textCartEmpty)
        errorMessage = findViewById(R.id.textCartError)

        configureNavigation()

        findViewById<Button>(
            R.id.buttonChooseBranch
        ).setOnClickListener {
            showBranchSelection()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderCart(state)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun configureNavigation() {
        findViewById<Button>(R.id.buttonNavCart).apply {
            isEnabled = false
            setTypeface(typeface, android.graphics.Typeface.BOLD)
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

        findViewById<Button>(R.id.buttonNavBranches).setOnClickListener {
            startActivity(Intent(this, BranchActivity::class.java))
        }
    }

    private fun renderCart(state: CartUiState) {
        val quantity = state.items.sumOf { it.quantity }

        findViewById<TextView>(R.id.textCartCount).text =
            resources.getQuantityString(
                R.plurals.cart_item_count,
                quantity,
                quantity
            )

        emptyMessage.visibility = if (state.isEmpty) {
            View.VISIBLE
        } else {
            View.GONE
        }

        checkoutContainer.visibility = if (state.isEmpty) {
            View.GONE
        } else {
            View.VISIBLE
        }

        val error = state.error

        if (error != null) {
            errorMessage.setText(UiTextMapper.errorMessage(error))
            errorMessage.visibility = View.VISIBLE
        } else {
            errorMessage.visibility = View.GONE
        }

        itemContainer.removeAllViews()

        state.items.forEach { item ->
            addCartItem(item)
        }

        val formattedTotal = MoneyFormatter.format(
            state.totalInCents
        )

        findViewById<TextView>(R.id.textCartSubtotal).text =
            getString(R.string.subtotal_amount, formattedTotal)

        findViewById<TextView>(R.id.textCartTotal).text =
            formattedTotal

        val branch = state.branches.firstOrNull {
            it.id == state.selectedBranchId
        }

        findViewById<TextView>(R.id.textCartBranchName).text =
            branch?.name ?: getString(R.string.select_branch)

        findViewById<TextView>(R.id.textCartBranchAddress).text =
            branch?.address.orEmpty()

        findViewById<Button>(R.id.buttonChooseBranch).isEnabled =
            state.branches.isNotEmpty()
    }

    private fun addCartItem(item: CartItem) {
        val card = layoutInflater.inflate(
            R.layout.item_cart,
            itemContainer,
            false
        )

        card.findViewById<TextView>(R.id.textCartProductName).text =
            item.product.name

        card.findViewById<TextView>(R.id.textCartUnitPrice).text =
            getString(
                R.string.unit_price_amount,
                MoneyFormatter.format(item.product.priceInCents)
            )

        card.findViewById<TextView>(R.id.textCartQuantity).text =
            item.quantity.toString()

        card.findViewById<TextView>(R.id.textCartItemSubtotal).text =
            getString(
                R.string.subtotal_amount,
                MoneyFormatter.format(item.subtotalInCents)
            )

        card.findViewById<Button>(
            R.id.buttonCartDecrease
        ).apply {
            isEnabled = item.quantity > 1

            setOnClickListener {
                if (item.quantity > 1) {
                    viewModel.updateQuantity(
                        item.product.id,
                        item.quantity - 1
                    )
                }
            }
        }

        card.findViewById<Button>(
            R.id.buttonCartIncrease
        ).apply {
            isEnabled = item.quantity < Int.MAX_VALUE

            setOnClickListener {
                if (item.quantity < Int.MAX_VALUE) {
                    viewModel.updateQuantity(
                        item.product.id,
                        item.quantity + 1
                    )
                }
            }
        }

        card.findViewById<Button>(
            R.id.buttonRemoveCartItem
        ).setOnClickListener {
            viewModel.removeProduct(item.product.id)
        }

        itemContainer.addView(card)
    }

    private fun showBranchSelection() {
        val state = viewModel.uiState.value
        val branches = state.branches

        if (branches.isEmpty()) {
            return
        }

        val labels = branches.map { branch ->
            "${branch.name}\n${branch.address}"
        }.toTypedArray()

        val selectedIndex = branches.indexOfFirst {
            it.id == state.selectedBranchId
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.select_branch)
            .setSingleChoiceItems(
                labels,
                selectedIndex
            ) { dialog, position ->
                viewModel.selectBranch(branches[position].id)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}