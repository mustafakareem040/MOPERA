package com.example.mopera.ui.screens.homeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.mopera.R
import com.example.mopera.ui.theme.LogoBlue

internal const val SearchKey = "tab:SEARCH"

internal fun tabKey(tab: HomeTab) = "tab:${tab.name}"

/**
 * Logo and sections. [contentEntry] is where focus goes when you press down.
 */
@Composable
fun TopBar(
    selected: HomeTab,
    focus: FocusMemory,
    onTabSelected: (HomeTab) -> Unit,
    onSearch: () -> Unit,
    contentEntry: () -> FocusRequester,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = SidePadding, end = SidePadding, top = 24.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "MOPERA",
            modifier = Modifier.height(15.dp)
        )
        Spacer(Modifier.width(36.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.entries.forEach { tab ->
                TopBarItem(
                    label = tab.label,
                    selected = tab == selected,
                    focusKey = tabKey(tab),
                    focus = focus,
                    onClick = { onTabSelected(tab) },
                    contentEntry = contentEntry
                )
            }
            TopBarItem(
                label = "Search",
                icon = Icons.Default.Search,
                selected = false,
                focusKey = SearchKey,
                focus = focus,
                onClick = onSearch,
                contentEntry = contentEntry
            )
        }
    }
}

@Composable
private fun TopBarItem(
    label: String,
    selected: Boolean,
    focusKey: String,
    focus: FocusMemory,
    onClick: () -> Unit,
    contentEntry: () -> FocusRequester,
    icon: ImageVector? = null,
) {
    val colors = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.small
    Surface(
        onClick = onClick,
        modifier = Modifier
            .focusProperties { down = contentEntry() }
            .focusRequester(rememberRestorableFocusRequester(focusKey, focus))
            .onFocusChanged { if (it.isFocused) focus.focused(focusKey) },
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = if (selected) colors.onSurface else colors.onSurface.copy(alpha = 0.6f),
            focusedContainerColor = colors.primaryContainer,
            focusedContentColor = colors.onSurface,
            pressedContainerColor = colors.primaryContainer,
            pressedContentColor = colors.onSurface
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, colors.border), shape = shape)
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f)
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(Modifier.height(4.dp))
            // The open tab is underlined in the logo's blue.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (selected) LogoBlue else Color.Transparent)
            )
        }
    }
}
