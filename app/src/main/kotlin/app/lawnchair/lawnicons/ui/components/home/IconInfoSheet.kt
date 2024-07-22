package app.lawnchair.lawnicons.ui.components.home

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.components.IconLink
import app.lawnchair.lawnicons.ui.components.core.Card
import app.lawnchair.lawnicons.ui.components.core.ListRow
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconInfoSheet(
    iconInfo: IconInfo,
    modifier: Modifier = Modifier,
    isPopupShown: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val groupedComponents = remember {
        iconInfo.componentNames
            .groupBy { it.label }
            .map { (label, components) ->
                label to components.map { it.componentName }
            }
    }

    val githubName = iconInfo.drawableName.replace(
        oldValue = "_foreground",
        newValue = "",
    )

    val shareContents = remember { getShareContents(githubName, groupedComponents) }

    ModalBottomSheet(
        onDismissRequest = {
            isPopupShown(false)
        },
        sheetState = sheetState,
        contentWindowInsets = {
            WindowInsets(0.dp)
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (LocalInspectionMode.current) {
                        val icon = when (iconInfo.id) {
                            1 -> Icons.Rounded.Email
                            2 -> Icons.Rounded.Search
                            3 -> Icons.Rounded.Call
                            else -> Icons.Rounded.Warning
                        }
                        Icon(
                            icon,
                            contentDescription = iconInfo.drawableName,
                            modifier = Modifier.size(250.dp),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    } else {
                        Icon(
                            painterResource(id = iconInfo.id),
                            contentDescription = iconInfo.drawableName,
                            modifier = Modifier.size(250.dp),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconLink(
                        iconResId = R.drawable.github_foreground,
                        label = stringResource(id = R.string.view_on_github),
                        url = "${Constants.GITHUB}/blob/develop/svgs/$githubName.svg",
                    )
                    Spacer(Modifier.width(16.dp))
                    IconLink(
                        iconResId = R.drawable.share_icon,
                        label = stringResource(id = R.string.share),
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareContents)
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(intent, null)
                            context.startActivity(shareIntent)
                        },
                    )
                }
            }
            item {
                Card(
                    label = stringResource(id = R.string.drawable),
                ) {
                    SimpleListRow(
                        label = githubName,
                        description = stringResource(R.string.icon_info_outdated_warning),
                        divider = false,
                    )
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Text(
                    text = stringResource(id = R.string.mapped_components),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 32.dp, bottom = 6.dp),
                )
            }
            itemsIndexed(groupedComponents) { index, (label, componentName) ->
                IconInfoListRow(label, componentName, index, groupedComponents.lastIndex)
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

private fun getShareContents(
    githubName: String,
    groupedComponents: List<Pair<String, List<String>>>,
): String {
    val formattedComponents = groupedComponents.joinToString(separator = "\n") { (group, components) ->
        val componentList = components.joinToString(separator = "\n") { it }
        "$group:\n$componentList"
    }
    return "Drawable: $githubName\n\nMapped components: \n$formattedComponents"
}

@Composable
private fun IconInfoListRow(
    label: String,
    componentNames: List<String>,
    currentIndex: Int,
    lastIndex: Int,
) {
    ListRow(
        label = {
            SelectionContainer {
                Text(
                    text = label,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        description = {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                ) {
                    componentNames.firstOrNull()?.let {
                        Text(
                            text = it,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 48.dp),
                        )
                    }
                    Column {
                        componentNames.forEach {
                            Text(
                                text = it,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        divider = currentIndex < lastIndex,
        first = currentIndex == 0,
        last = currentIndex == lastIndex,
        background = true,
        enforceHeight = false,
    )
}

@PreviewLawnicons
@Composable
private fun IconInfoPopupPreview() {
    val showPopup = remember { mutableStateOf(true) }
    LawniconsTheme {
        IconInfoSheet(
            iconInfo = SampleData.iconInfoSample,
        ) {
            showPopup.value = it
        }
    }
}
