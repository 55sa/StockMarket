package com.plcoding.stockmarketapp.presentation.company_listings

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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


/**
 * Displays a single company listing with logo, name, and exchange information.
 *
 * @param company The [CompanyListing] data object to display.
 * @param modifier Optional [Modifier] for layout customization.
 */
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
            // Extract company name to fetch logo URL dynamically
            val companyName = company.name.substringBefore(" ").lowercase()
            val logoUrl = "https://logo.clearbit.com/${companyName}.com"

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display company logo
                Image(
                    painter = rememberAsyncImagePainter(model = logoUrl),
                    contentDescription = "${company.name} logo",
                    modifier = Modifier
                        .size(40.dp) // Set size for the logo image
                        .padding(end = 8.dp) // Padding to the right of the logo
                )

                // Display company name
                Text(
                    text = company.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colorTheme.primaryText,
                    overflow = TextOverflow.Ellipsis, // Ellipsis for overflowed text
                    maxLines = 1, // Limit to one line
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(4.dp)) // Small spacer between name and exchange

                // Display exchange information
                Text(
                    text = company.exchange,
                    fontWeight = FontWeight.Light,
                    color = colorTheme.primaryText
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Space between rows

            // Display company symbol
            Text(
                text = "(${company.symbol})",
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
            )
        }
    }
}

