package com.example.playlistmaker.ui.settings.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val hostViewModel by activityViewModel<HostViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    @SuppressLint("IntentReset")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShareApp.setOnClickListener {
            Intent().apply {
                val message = getString(R.string.share_link)
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }

        binding.btnSendToSupport.setOnClickListener {
            Intent().apply {
                val message = getString(R.string.email_message)
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_name)))
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }

        binding.btnLicenseAgreement.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.offer_link))
                startActivity(this)
            }
        }

        binding.switchNightMode.setOnCheckedChangeListener { _, checked ->
             hostViewModel.saveTheme(checked)
        }

        hostViewModel.darkTheme.observe(viewLifecycleOwner) {checked ->
            binding.switchNightMode.isChecked = checked
        }

    }
}