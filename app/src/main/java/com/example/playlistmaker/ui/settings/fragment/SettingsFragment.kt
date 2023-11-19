package com.example.playlistmaker.ui.settings.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModel()

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

        binding.switchNightMode.setOnCheckedChangeListener { switcher, checked ->
            viewModel.saveTheme(checked)
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

    }

    private fun render(state: SettingsState) {
        if (binding.switchNightMode.isChecked != state.darkTheme)
            binding.switchNightMode.isChecked = state.darkTheme
        val app = (requireActivity().applicationContext as App)
        app.switchTheme(state.darkTheme)
    }

}