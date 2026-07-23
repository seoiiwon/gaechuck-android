package com.gaechuck_package.gaechuck.ui.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.repository.SettingRepository

class SettingViewModel(private val repository: SettingRepository) : ViewModel() {

    fun isEnabled(key: String): Boolean = repository.isEnabled(key)

    /**
     * Attempts to toggle a quick-info key. Returns false (and leaves the
     * stored value untouched) if enabling it would exceed the max-2 limit.
     */
    fun toggleQuickInfo(key: String, checked: Boolean): Boolean {
        if (checked && repository.enabledQuickInfoCount() >= SettingRepository.QUICK_INFO_MAX) {
            return false
        }
        repository.setEnabled(key, checked)
        return true
    }

    fun setEnabled(key: String, checked: Boolean) {
        repository.setEnabled(key, checked)
    }

    class SettingViewModelFactory(private val repository: SettingRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
