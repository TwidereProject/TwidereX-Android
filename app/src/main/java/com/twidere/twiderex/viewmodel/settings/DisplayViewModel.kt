package com.twidere.twiderex.viewmodel.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.settings.AvatarStyleSettings
import com.twidere.twiderex.settings.FontScaleSettings
import com.twidere.twiderex.settings.MediaPreviewSettings
import com.twidere.twiderex.settings.UseSystemFontSizeSettings

class DisplayViewModel @ViewModelInject constructor(
    val avatarStyleSettings: AvatarStyleSettings,
    val mediaPreviewSettings: MediaPreviewSettings,
    val useSystemFontSizeSettings: UseSystemFontSizeSettings,
    val fontScaleSettings: FontScaleSettings,
) : ViewModel()