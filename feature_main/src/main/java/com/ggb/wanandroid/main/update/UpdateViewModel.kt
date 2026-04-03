package com.ggb.wanandroid.main.update

import androidx.lifecycle.viewModelScope
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.wanandroid.main.data.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdateViewModel(
    private val repository: UpdateRepository = UpdateRepository()
) : BaseViewModel() {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun checkUpdate() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            collectResult(
                flow = repository.checkUpdate(),
                onSuccess = { updateInfo ->
                    _updateState.value = UpdateState.HasUpdate(updateInfo)
                },
                onError = { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "检查更新失败")
                }
            )
        }
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class HasUpdate(val updateInfo: UpdateInfo) : UpdateState()
    data class Error(val message: String) : UpdateState()
}