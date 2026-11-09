package com.example.menugo.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.menugo.R
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.util.NavigationKeys
import com.example.menugo.viewmodel.MenuGoViewModelFactory
import com.example.menugo.viewmodel.ProductFormError
import com.example.menugo.viewmodel.ProductFormViewModel

class ProductFormActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductFormViewModel

    private lateinit var nameField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var priceField: EditText
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_form)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        viewModel = ViewModelProvider(this, MenuGoViewModelFactory())
            .get(ProductFormViewModel::class.java)

        nameField = findViewById(R.id.editProductName)
        descriptionField = findViewById(R.id.editProductDescription)
        priceField = findViewById(R.id.editProductPrice)
        categorySpinner = findViewById(R.id.spinnerProductCategory)

        val productId = intent.getStringExtra(NavigationKeys.PRODUCT_ID)
        if (productId.isNullOrBlank()) {
            viewModel.startNewProduct()
        } else {
            viewModel.loadProduct(productId)
        }

        showCurrentProduct()
        connectFieldChanges()

        findViewById<Button>(R.id.buttonSaveProduct).setOnClickListener {
            saveProduct()
        }

        findViewById<Button>(R.id.buttonCancelProduct).setOnClickListener {
            finish()
        }
    }

    private fun showCurrentProduct() {
        val state = viewModel.uiState.value

        nameField.setText(state.name)
        descriptionField.setText(state.description)
        priceField.setText(state.price)

        val categoryIndex = ProductCategory.values().indexOf(state.category)
        if (categoryIndex >= 0) {
            categorySpinner.setSelection(categoryIndex)
        }

        findViewById<Button>(R.id.buttonSaveProduct).isEnabled = state.canSave
    }

    private fun connectFieldChanges() {
        nameField.addTextChangedListener(createTextWatcher { value ->
            viewModel.updateName(value)
        })

        descriptionField.addTextChangedListener(createTextWatcher { value ->
            viewModel.updateDescription(value)
        })

        priceField.addTextChangedListener(createTextWatcher { value ->
            viewModel.updatePrice(value)
        })

        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val categories = ProductCategory.values()
                    if (position in categories.indices) {
                        viewModel.updateCategory(categories[position])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun createTextWatcher(
        onTextChanged: (String) -> Unit
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                onTextChanged(text?.toString().orEmpty())
            }

            override fun afterTextChanged(editable: Editable?) = Unit
        }
    }

    private fun saveProduct() {
        viewModel.saveProduct()
        val state = viewModel.uiState.value

        if (state.isSaved) {
            setResult(RESULT_OK)
            finish()
            return
        }

        state.error?.let { error ->
            val messageId = when (error) {
                ProductFormError.NAME_REQUIRED -> R.string.error_name_required
                ProductFormError.DESCRIPTION_REQUIRED -> R.string.error_description_required
                ProductFormError.INVALID_PRICE -> R.string.error_invalid_price
                ProductFormError.PRODUCT_NOT_FOUND -> R.string.error_product_not_found
                ProductFormError.DUPLICATE_ID -> R.string.error_duplicate_product
                ProductFormError.SAVE_FAILED -> R.string.error_save_product
            }

            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }
}
