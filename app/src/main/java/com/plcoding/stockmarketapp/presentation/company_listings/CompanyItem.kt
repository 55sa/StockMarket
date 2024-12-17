package com.plcoding.stockmarketapp.presentation.company_listings

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.ui.theme.DarkThemeColors
import com.plcoding.stockmarketapp.ui.theme.LightThemeColors

@Composable
fun CompanyItem(
    company: CompanyListing,
    modifier: Modifier = Modifier
) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            val companyName = company.name.substringBefore(" ").lowercase()
            val logoUrl = "https://logo.clearbit.com/${companyName}.com"
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = logoUrl),
                    contentDescription = "${company.name} logo",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )


                Text(
                    text = company.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colorTheme.primaryText,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = company.exchange,
                    fontWeight = FontWeight.Light,
                    color = colorTheme.primaryText
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "(${company.symbol})",
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
            )
        }
    }
}


