package com.artemchep.keyguard.ui.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.Material3RichText
import com.halilibo.richtext.ui.resolveDefaults
import com.halilibo.richtext.ui.string.RichTextStringStyle

@Composable
fun Md(
    modifier: Modifier = Modifier,
    markdown: String,
) {
    val richTextStyle = RichTextStyle().resolveDefaults().copy(
        stringStyle = RichTextStringStyle().copy(
            linkStyle = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.primary,
            ),
        ),
    )
    Material3RichText(
        modifier = modifier,
        style = richTextStyle,
    ) {
        Markdown(markdown)
    }
}
