package com.example.menugo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.menugo.R
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.util.NavigationKeys
import com.example.menugo.util.ProductValidationUtils
import java.math.BigDecimal

class FilterActivity : AppCompatActivity() {

    private lateinit var categoryGroup: RadioGroup
    private lateinit var minPriceInput: EditText
    private lateinit var maxPriceInput: EditText
    private lateinit var promotionsCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter)

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

        categoryGroup = findViewById(R.id.groupCategory)
        minPriceInput = findViewById(R.id.editMinPrice)
        maxPriceInput = findViewById(R.id.editMaxPrice)
        promotionsCheckBox = findViewById(R.id.checkOnlyPromotions)

        if (savedInstanceState == null) {
            showCurrentFilters()
        }

        findViewById<Button>(
            R.id.buttonApplyFilters
        ).setOnClickListener {
            applyFilters()
        }

        findViewById<Button>(
            R.id.buttonClearFilters
        ).setOnClickListener {
            clearForm()
        }

        findViewById<Button>(
            R.id.buttonCancelFilters
        ).setOnClickListener {
            finish()
        }
    }

    private fun showCurrentFilters() {
        val categoryName = intent.getStringExtra(
            NavigationKeys.CATEGORY
        )

        val selectedRadioId = when (categoryName) {
            ProductCategory.HAMBURGERS.name -> R.id.radioHamburgers
            ProductCategory.DRINKS.name -> R.id.radioDrinks
            ProductCategory.DESSERTS.name -> R.id.radioDesserts
            ProductCategory.SIDES.name -> R.id.radioSides
            else -> R.id.radioAll
        }

        categoryGroup.check(selectedRadioId)

        showPrice(minPriceInput, NavigationKeys.MIN_PRICE)
        showPrice(maxPriceInput, NavigationKeys.MAX_PRICE)

        promotionsCheckBox.isChecked = intent.getBooleanExtra(
            NavigationKeys.ONLY_PROMOTIONS,
            false
        )
    }

    private fun showPrice(input: EditText, key: String) {
        if (intent.hasExtra(key)) {
            val cents = intent.getLongExtra(key, 0L)
            input.setText(
                BigDecimal.valueOf(cents, 2).toPlainString()
            )
        }
    }

    private fun selectedCategory(): ProductCategory? {
        return when (categoryGroup.checkedRadioButtonId) {
            R.id.radioHamburgers -> ProductCategory.HAMBURGERS
            R.id.radioDrinks -> ProductCategory.DRINKS
            R.id.radioDesserts -> ProductCategory.DESSERTS
            R.id.radioSides -> ProductCategory.SIDES
            else -> null
        }
    }

    private fun applyFilters() {
        minPriceInput.error = null
        maxPriceInput.error = null

        val minText = minPriceInput.text.toString().trim()
        val maxText = maxPriceInput.text.toString().trim()

        val minPrice = ProductValidationUtils.priceToCents(minText)
        val maxPrice = ProductValidationUtils.priceToCents(maxText)

        if (minText.isNotEmpty() && minPrice == null) {
            minPriceInput.error = getString(
                R.string.error_invalid_price
            )
            minPriceInput.requestFocus()
            return
        }

        if (maxText.isNotEmpty() && maxPrice == null) {
            maxPriceInput.error = getString(
                R.string.error_invalid_price
            )
            maxPriceInput.requestFocus()
            return
        }

        if (!ProductValidationUtils.validatePriceRange(
                minPrice,
                maxPrice
            )
        ) {
            maxPriceInput.error = getString(
                R.string.error_price_range
            )
            maxPriceInput.requestFocus()
            return
        }

        val result = Intent()

        selectedCategory()?.let {
            result.putExtra(NavigationKeys.CATEGORY, it.name)
        }


        minPrice?.let {
            result.putExtra(NavigationKeys.MIN_PRICE, it)
        }

        maxPrice?.let {
            result.putExtra(NavigationKeys.MAX_PRICE, it)
        }

        result.putExtra(
            NavigationKeys.ONLY_PROMOTIONS,
            promotionsCheckBox.isChecked
        )

        setResult(RESULT_OK, result)
        finish()
    }

    private fun clearForm() {
        categoryGroup.check(R.id.radioAll)
        minPriceInput.text.clear()
        maxPriceInput.text.clear()
        minPriceInput.error = null
        maxPriceInput.error = null
        promotionsCheckBox.isChecked = false
    }
}