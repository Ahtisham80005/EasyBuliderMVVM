package com.tradesk.activity.proposalModule

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.AddItemDataProposal
import com.tradesk.Model.AddItemDataUpdateProposal
import com.tradesk.Model.ItemsData
import com.tradesk.R
import com.tradesk.activity.proposalModule.AddProposalActivity.Companion.mAddItemDataUpdate
import com.tradesk.activity.proposalModule.AddProposalActivity.Companion.sub_total_amount
import com.tradesk.activity.proposalModule.AddProposalActivity.Companion.tax_amount
import com.tradesk.activity.proposalModule.AddProposalActivity.Companion.total_amount
import com.tradesk.activity.proposalModule.adapter.DefaultItemsAdapter
import com.tradesk.databinding.ActivityProposalsBinding
import com.tradesk.databinding.ActivityProposaltemsBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.customFullDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProposalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProposaltemsActivity : AppCompatActivity() , SingleItemCLickListener {
    var select_old = ""
    var select_old_title = ""
    var select_old_description = ""
    var taxRate=10;
    val mList = arrayListOf<ItemsData>()
    val mDefaultItemsAdapter by lazy { DefaultItemsAdapter(this, this, mList) }
    lateinit var binding:ActivityProposaltemsBinding
    lateinit var viewModel: ProposalsViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_proposaltems)
        viewModel = ViewModelProvider(this).get(ProposalsViewModel::class.java)
        binding.rvDefaultItems.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvDefaultItems.adapter = mDefaultItemsAdapter

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getProposalItemslist()
            }
        }

        binding.mIvAddNew.setOnClickListener {
            select_old = ""
            select_old_title = ""
            select_old_description = ""
            createEditItem {
                addItem(it)
            }
        }

        binding.mIvBack.setOnClickListener {
            finish()
        }
        if (intent.hasExtra("taxRate")){
            taxRate=intent.getStringExtra("taxRate")!!.toInt()
        }
        initObserve()
    }
    fun initObserve()
    {
        viewModel.responseDefaultItemsModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.items_data)
                    mDefaultItemsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
    }
    override fun onSingleItemClick(item: Any, position: Int) {
        select_old_title = mList[position].title
        select_old_description = mList[position].description
        select_old = "1"
        createEditItem {
            addItem(it)
        }
    }

    fun addItem(addItemData: AddItemDataProposal) {

    }

    fun createEditItem(data: AddItemDataProposal? = null, onSuccess: (AddItemDataProposal) -> Unit) {
        customFullDialog(R.layout.add_estimate_item) { v, d ->
            val etName = v.findViewById<TextInputEditText>(R.id.etName)
            val etDesc = v.findViewById<TextInputEditText>(R.id.etDesc)
            val etQty = v.findViewById<TextInputEditText>(R.id.etQty)
            val etCost = v.findViewById<TextInputEditText>(R.id.etCost)
            val subTotal = v.findViewById<TextView>(R.id.subTotal)
            val itemTax = v.findViewById<TextView>(R.id.itemTax)
            val total = v.findViewById<TextView>(R.id.total)
            val isTaxable = v.findViewById<Switch>(R.id.isTaxable)

            if (select_old.isNotEmpty()) {
                etName.setText(select_old_title)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    etDesc.setText(Html.fromHtml(select_old_description.replace("\\n","\n"), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    etDesc.setText(Html.fromHtml(select_old_description));
                }
            }

            isTaxable.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if(!etCost.text.isNullOrEmpty()&&!etQty.text.isNullOrEmpty()) {
                    if (isChecked) {

                        var sub_total =
                            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                                .toInt()

                        var tax = 0
                        if (isTaxable.isChecked) {
                            tax = sub_total * taxRate / 100
                        } else {
                            tax = 0
                        }
                        var totalamount = sub_total + tax
                        val formatter = DecimalFormat("#,###,###")

                        subTotal.setText("$ " + formatter.format(sub_total))
                        itemTax.setText("$ " + formatter.format(tax))
                        total.setText("$ " + formatter.format(totalamount))

                        sub_total_amount = sub_total.toString()
                        total_amount = totalamount.toString()
                        tax_amount = tax.toString()
                    } else {

                        var sub_total =
                            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                                .toInt()

                        var tax = 0
                        if (isTaxable.isChecked) {
                            tax = sub_total * taxRate / 100
                        } else {
                            tax = 0
                        }
                        tax = 0;

                        var totalamount = sub_total


                        val formatter = DecimalFormat("#,###,###")


                        subTotal.setText("$ " + formatter.format(sub_total))
                        itemTax.setText("$ " + formatter.format(tax))
                        total.setText("$ " + formatter.format(totalamount))


                        sub_total_amount = sub_total.toString()
                        total_amount = totalamount.toString()
                        tax_amount = tax.toString()
                    }
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            })
            etQty.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    etQty.removeTextChangedListener(this)

                    try {
                        var originalString = s.toString()
                        val longval: Int
                        if (originalString.contains(",")) {
                            originalString = originalString.replace(",".toRegex(), "")
                        }
                        longval = originalString.toInt()
                        if (etCost.text.toString().isNotEmpty()){

                            var sub_total =
                                etCost.text.toString().replace(",", "")
                                    .toInt() * longval
                                    .toInt()

                            var tax = 0
                            if (isTaxable.isChecked) {
                                tax = sub_total * taxRate / 100
                            } else {
                                tax = 0
                            }

                            var totalamount = sub_total + tax


                            val formatter = DecimalFormat("#,###,###")


                            subTotal.setText("$ " + formatter.format(sub_total))
                            itemTax.setText("$ " + formatter.format(tax))
                            total.setText("$ " + formatter.format(totalamount))

                            sub_total_amount = sub_total.toString()
                            total_amount = totalamount.toString()
                            tax_amount = tax.toString()
                        }
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }

                    etQty.addTextChangedListener(this)
                }
            })
            etCost.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (etQty.text.toString().isNotEmpty())
                    {
                        if (etCost.text.toString().isNotEmpty()) {
                            if(etQty.text.toString().isNotEmpty())
                            {
                                var sub_total = etCost.text.toString().replace(",", "")
                                    .toInt() * etQty.text.toString()
                                    .toInt()
                                var tax = 0
                                if (isTaxable.isChecked) {
                                    tax = sub_total * taxRate / 100
                                } else {
                                    tax = 0
                                }
                                var totalamount = sub_total + tax
                                val formatter = DecimalFormat("#,###,###")
                                subTotal.setText("$ " + formatter.format(sub_total))
                                itemTax.setText("$ " + formatter.format(tax))
                                total.setText("$ " + formatter.format(totalamount))
                                sub_total_amount = sub_total.toString()
                                total_amount = totalamount.toString()
                                tax_amount = tax.toString()
                            }
                        }
                    }else toast("Please enter quantity to continue")

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    etCost.removeTextChangedListener(this)

                    try {
                        var originalString = s.toString()
                        val longval: Long
                        if (originalString.contains(",")) {
                            originalString = originalString.replace(",".toRegex(), "")
                        }
                        longval = originalString.toLong()
                        val formatter: DecimalFormat =
                            NumberFormat.getInstance(Locale.US) as DecimalFormat
                        formatter.applyPattern("#,###,###,###")
                        val formattedString: String = formatter.format(longval)

                        //setting text after format to EditText
                        etCost.setText(formattedString)
                        etCost.setSelection(etCost.getText()!!.length)
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }

                    etCost.addTextChangedListener(this)
                }
            })

            v.findViewById<ImageView>(R.id.mIvBack).setOnClickListener {
                d.dismiss()
            }
            v.findViewById<TextView>(R.id.btnDone).setOnClickListener {
                if (etName.text!!.isNotEmpty() && etDesc.text!!.isNotEmpty() && etQty.text!!.isNotEmpty() && etCost.text!!.isNotEmpty()) {

                    mAddItemDataUpdate.add(
                        AddItemDataUpdateProposal(
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etQty.text.toString(),
                            etCost.text.toString(),
                            if (isTaxable.isChecked) "1" else "0"
                        )
                    )
                    select_old = ""
                    finish()
                    val intent = Intent("itemupdated")
                    intent.putExtra("itemupdated", "itemupdated")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    d.dismiss()
                } else toast("All fields are required!")
            }
            data?.let {
                etName.setText(it.name)
                etDesc.setText(it.desc)
                etQty.setText(it.qty)
                etCost.setText(it.cost.toString())
            }
        }
    }
}