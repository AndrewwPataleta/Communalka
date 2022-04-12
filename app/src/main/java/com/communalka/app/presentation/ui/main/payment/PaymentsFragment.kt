package com.communalka.app.presentation.ui.main.payment

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.huxq17.download.Pump
import com.huxq17.download.core.DownloadInfo
import com.huxq17.download.core.DownloadListener
import com.communalka.app.R
import com.communalka.app.data.model.PaymentHistoryModel
import com.communalka.app.databinding.FragmentPaymentsBinding
import com.communalka.app.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream
import android.webkit.WebView
import android.webkit.WebViewClient
import com.communalka.app.common.utils.*


class PaymentsFragment : Fragment() {

    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<PaymentsViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private lateinit var paymentsAdapter: PaymentHistoryAdapter
    private lateinit var paymentHistoryModel: PaymentHistoryModel
    private lateinit var receiptAction: BottomSheetDialog
    private lateinit var receiptFullAction: BottomSheetDialog
    private  var receiptEmailDialog: BottomSheetDialog? = null

    private val REQUEST_READ_EXTERNAL = 111

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPaymentsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_EXTERNAL -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     Pump.newRequest("https://receipts.ru/Home/Download"+paymentHistoryModel.receipt.url.split("receipts.ru")[1])
                .listener(object : DownloadListener() {
                    override fun onSuccess() {
                        receiptAction.dismiss()
                        val downloadFile = File(downloadInfo.filePath)
                        writeToFile(requireContext(), downloadFile.name, downloadFile)
                        startActivity( Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    }
                    override fun onFailed() {}
                    override fun onProgress(progress: Int) {
                        val downloadInfo: DownloadInfo = downloadInfo
                    }
                }).submit()
                } else {
                    receiptAction.dismiss()
                    Toast.makeText(requireContext(),"Требуется разрешение для доступа к файлам.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun writeToFile(applicationContext: Context, filename: String, data: File) {
        try {
            val resolver = applicationContext.contentResolver
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
            val os: OutputStream? = uri?.let { resolver.openOutputStream(it,"wt") }
            if (os != null) {
                os.write(data.readBytes())
                os.flush()
                os.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showSendToReceipt() {
        binding.sendReceiptToEmail.visibility = View.VISIBLE
        binding.paymentButton.visibility = View.GONE
    }

    private fun initObservers() {
        viewModel.payments.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    paymentsAdapter = PaymentHistoryAdapter(it, viewModel)
                    binding.premisesList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.premisesList.adapter = paymentsAdapter
                }
            }
        }
          viewModel.actionReceipt.observe(viewLifecycleOwner) {
              if (!it.hasBeenHandled.get()) {
                  it.getContentIfNotHandled {
                      paymentHistoryModel = it
//                    val root = layoutInflater.inflate(R.layout.layout_action_receipt, null)
//                    receiptAction = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
//                    receiptAction.setContentView(root)
//                    receiptAction.findViewById<View>(R.id.downloadReceipt)?.setOnClickListener {
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            requestPermissions(
//                                arrayOf(
//                                    Manifest.permission.ACCESS_MEDIA_LOCATION,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                                ), REQUEST_READ_EXTERNAL
//                            )
//                        } else {
//                            requestPermissions(
//                                arrayOf(
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                                ), REQUEST_READ_EXTERNAL
//                            )
//                        }
//                    }
//                    receiptAction.findViewById<View>(R.id.sendEmail)?.setOnClickListener {
//                        viewModel.selectEmail()
//                    }
//                    receiptAction.findViewById<View>(R.id.openReceipt)?.setOnClickListener {
//                        receiptAction.dismiss()
//                        mainViewModel.showReceipt(true)
//                        binding.container.visibility = View.GONE
//                        binding.receiptContainer.visibility = View.VISIBLE
//                        binding.web.settings.javaScriptEnabled = true
//
//                        binding.web.webViewClient = object : WebViewClient() {
//                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                                view?.loadUrl(url!!)
//                                return true
//                            }
//                        }
//                        binding.web.loadUrl(paymentHistoryModel.receipt.url)
//
//                    }
//                    receiptAction.show()
//
//                }

                      mainViewModel.showReceipt(true)
                      binding.container.visibility = View.GONE
                      binding.receiptContainer.visibility = View.VISIBLE
                      binding.web.settings.javaScriptEnabled = true

                      binding.web.webViewClient = object : WebViewClient() {
                          override fun shouldOverrideUrlLoading(
                              view: WebView?,
                              url: String?
                          ): Boolean {
                              view?.loadUrl(url!!)
                              return true
                          }
                      }
                      binding.web.loadUrl(paymentHistoryModel.receipt.url)
                  }
              }
          }
       viewModel.totalPaymentAmount.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    mainViewModel._toolbarWithTitle.postValue(Event(Pair("Оплаты", "Общая сумма: ${roundOffTo2DecPlacesSecond(
                        it
                    )} ₽")))
                }
            }
        }
        viewModel.progressSend.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  if (it) {
                      binding.progressSend.visibility = View.VISIBLE
                      binding.sendReceiptToEmail.visibility = View.GONE
                  } else {
                      binding.progressSend.visibility = View.GONE
                      binding.sendReceiptToEmail.visibility = View.VISIBLE
                  }
                }
            }
        }

        viewModel.openFilter.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        findNavController().navigate(com.communalka.app.R.id.toFilter)
                    }
                }
            }
        }
        viewModel.confirmFilterModel.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    var disableEmail = true
                    binding.chipGroup.removeAllViews()
                    if (it != null) {
                        it.date?.let {
                            val chip= Chip(requireContext())
                            chip.text = convertLongToTime(it.first).plus(" - ")+convertLongToTime(it.second)
                            chip.isCloseIconVisible = true
                            chip.chipStrokeWidth = 1f
                            chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                            chip.setOnCloseIconClickListener {
                                viewModel.removeDateFromFilter()
                            }
                            binding.chipGroup.addView(chip)
                            showSendToReceipt()
                            disableEmail = false
                        }
                        it.placement.second.map { placement ->
                            if (placement.selected) {
                                val chip= Chip(requireContext())
                                chip.text = placement.name
                                chip.chipStrokeWidth = 1f
                                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                                chip.isCloseIconVisible = true
                                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                chip.setOnCloseIconClickListener {
                                    viewModel.removePlacementFromFilter(placement)
                                }
                                binding.chipGroup.addView(chip)
                                showSendToReceipt()
                                disableEmail = false
                            }
                        }
                        it.suppliers.second.map { supplier ->
                            if (supplier.selected) {
                                val chip= Chip(requireContext())
                                chip.text = supplier.name
                                chip.isCloseIconVisible = true
                                chip.chipStrokeWidth = 1f
                                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                chip.setOnCloseIconClickListener {
                                    viewModel.removeSupplierFromFilter(supplier)
                                }
                                binding.chipGroup.addView(chip)
                                showSendToReceipt()
                                disableEmail = false
                            }
                        }
                        it.services.second.map { service ->
                            if (service.selected) {
                                val chip= Chip(requireContext())
                                chip.text = service.name
                                chip.isCloseIconVisible = true
                                chip.chipStrokeWidth = 1f
                                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                chip.setOnCloseIconClickListener {
                                    viewModel.removeServiceFromFilter(service)
                                }
                                binding.chipGroup.addView(chip)
                                showSendToReceipt()
                                disableEmail = false
                            }
                        }
                        if (disableEmail) {
                            binding.sendReceiptToEmail.visibility = View.GONE
                            binding.paymentButton.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        viewModel.showProgress.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   if (it) {
                       binding.premisesList.gone(false)
                       binding.progress.visible(false)
                   } else {
                        binding.premisesList.visible(false)
                        binding.progress.gone(false)
                   }
                }
            }
        }

        viewModel.dialogEmailSend.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    receiptAction?.let {
                        it.dismiss()
                    }
                    val root = layoutInflater.inflate(R.layout.layout_email_receipt, null)
                    receiptEmailDialog =
                        BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                    receiptEmailDialog!!.setContentView(root)

                    receiptEmailDialog!!.findViewById<View>(R.id.send)?.setOnClickListener {
                        viewModel.sendReceiptTo(receiptEmailDialog!!.findViewById<EditText>(R.id.email_value)!!.text.toString())
                    }
                    receiptEmailDialog!!.findViewById<EditText>(R.id.email_value)!!.setText(it)
                    receiptEmailDialog!!.show()
                }
            }
        }

        viewModel.dialogEmailSendError.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    receiptEmailDialog!!.findViewById<EditText>(R.id.email_value)?.let {
                        it.error = "Вы неправильно указали почту!"
                    }
                }
            }
        }

        viewModel.emailForFilter.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val root = layoutInflater.inflate(R.layout.layout_email_receipt, null)
                    receiptEmailDialog =
                        BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                    receiptEmailDialog!!.setContentView(root)

                    receiptEmailDialog!!.findViewById<View>(R.id.send)?.setOnClickListener {
                        viewModel.sendReceiptTo(receiptEmailDialog!!.findViewById<EditText>(R.id.email_value)!!.text.toString())
                    }
                    receiptEmailDialog!!.findViewById<EditText>(R.id.email_value)!!.setText(it)
                    receiptEmailDialog!!.show()
                }
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (receiptEmailDialog != null)
                        receiptEmailDialog!!.dismiss()
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.selectShare.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, it)
                    startActivity(Intent.createChooser(sharingIntent, "Кому отправить"))
                }
            }
        }
    }

    private fun initListeners() {
        binding.paymentButton.setOnClickListener {
            findNavController().navigate(R.id.toPlacementPayment)
        }
        binding.share.setOnClickListener {
            viewModel.selectShare()
        }
        binding.sendReceiptToEmail.setOnClickListener {
            viewModel.sendReceiptToEmail()
        }
        binding.close.setOnClickListener {
            mainViewModel.showReceipt(false)
            binding.container.visibility = View.VISIBLE
            binding.receiptContainer.visibility = View.GONE

        }

//        binding.menu.setOnClickListener {
//            val root = layoutInflater.inflate(R.layout.layout_action_full_receipt, null)
//            receiptFullAction = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
//            receiptFullAction.setContentView(root)
//            receiptFullAction.findViewById<View>(R.id.downloadReceipt)?.setOnClickListener {
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    requestPermissions(
//                        arrayOf(
//                            Manifest.permission.ACCESS_MEDIA_LOCATION,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                        ), REQUEST_READ_EXTERNAL
//                    )
//                } else {
//                    requestPermissions(
//                        arrayOf(
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                        ), REQUEST_READ_EXTERNAL
//                    )
//                }
//            }
//            receiptFullAction.findViewById<View>(R.id.sendEmail)?.setOnClickListener {
//                viewModel.selectEmail()
//            }
//
//            receiptFullAction.show()
//        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.updateFilters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}