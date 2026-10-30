package com.example.menugo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.menugo.data.memory.RepositoryProvider
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.ui.activity.AdminProductActivity
import com.example.menugo.ui.activity.BranchActivity
import com.example.menugo.ui.activity.CartActivity
import com.example.menugo.ui.activity.CatalogActivity
import com.example.menugo.ui.activity.ProductDetailActivity
import com.example.menugo.util.MoneyFormatter
import com.example.menugo.util.NavigationKeys

class MainActivity : AppCompatActivity() {

    private val repository = RepositoryProvider.repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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

        configureNavigation()
    }

    override fun onResume() {
        super.onResume()
        showPromotion()
    }

    private fun configureNavigation() {
        findViewById<Button>(R.id.buttonAdmin).setOnClickListener {
            startActivity(Intent(this, AdminProductActivity::class.java))
        }

        findViewById<Button>(R.id.buttonSearch).setOnClickListener {
            openCatalog()
        }

        findViewById<Button>(R.id.buttonHamburgers).setOnClickListener {
            openCatalog(ProductCategory.HAMBURGERS)
        }

        findViewById<Button>(R.id.buttonDrinks).setOnClickListener {
            openCatalog(ProductCategory.DRINKS)
        }

        findViewById<Button>(R.id.buttonDesserts).setOnClickListener {
            openCatalog(ProductCategory.DESSERTS)
        }

        findViewById<Button>(R.id.buttonNavMenu).setOnClickListener {
            openCatalog()
        }

        findViewById<Button>(R.id.buttonNavCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<Button>(R.id.buttonNavBranches).setOnClickListener {
            openBranches()
        }

        findViewById<Button>(R.id.buttonChangeBranch).setOnClickListener {
            openBranches()
        }
    }

    private fun openCatalog(category: ProductCategory? = null) {
        val intent = Intent(this, CatalogActivity::class.java)

        category?.let {
            intent.putExtra(NavigationKeys.CATEGORY, it.name)
        }

        startActivity(intent)
    }

    private fun openBranches() {
        startActivity(Intent(this, BranchActivity::class.java))
    }

    private fun showPromotion() {
        val promotion = repository.getProducts().firstOrNull {
            it.isAvailable && it.isOnPromotion
        }

        val promotionContainer = findViewById<View>(
            R.id.promotionContainer
        )
        val emptyMessage = findViewById<TextView>(
            R.id.textNoPromotion
        )

        if (promotion == null) {
            promotionContainer.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE
            return
        }

        promotionContainer.visibility = View.VISIBLE
        emptyMessage.visibility = View.GONE

        findViewById<TextView>(R.id.textPromotionName).text =
            promotion.name

        findViewById<TextView>(R.id.textPromotionDescription).text =
            promotion.description

        findViewById<TextView>(R.id.textPromotionPrice).text =
            MoneyFormatter.format(promotion.priceInCents)

        findViewById<Button>(R.id.buttonPromotion).setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(NavigationKeys.PRODUCT_ID, promotion.id)
            startActivity(intent)
        }
    }
}