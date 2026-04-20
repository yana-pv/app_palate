package com.example.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.auth.R
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.example.design.theme.CondimentFont
import com.example.design.theme.PalateColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import com.example.navigation.Destination

@Composable
fun StartupScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = dimensionResource(R.dimen.startup_bg_radius), bottomEnd = dimensionResource(R.dimen.startup_bg_radius)))
        ) {
            Image(
                painter = painterResource(id = R.drawable.startup_background),
                contentDescription = stringResource(com.example.design.R.string.app_name),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.startup_bottom_sheet_height))
                .align(Alignment.BottomCenter)
                .background(
                    color = PalateColors.GreenPrimary,
                    shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.startup_bottom_sheet_radius), topEnd = dimensionResource(R.dimen.startup_bottom_sheet_radius))
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_extra_large))
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_vertical_padding)))

                Text(
                    text = stringResource(com.example.design.R.string.app_name),
                    fontFamily = CondimentFont,
                    fontSize = dimensionResource(R.dimen.startup_logo_text_size).value.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

                Text(
                    text = stringResource(R.string.app_description),
                    fontSize = dimensionResource(R.dimen.startup_description_text_size).value.sp,
                    color = Color.White,
                    lineHeight = dimensionResource(R.dimen.startup_description_line_height).value.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_vertical_padding)))

                Button(
                    onClick = { navController.navigate(Destination.Register.route) },
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.startup_button_width))
                        .height(dimensionResource(R.dimen.startup_button_height))
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius))),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PalateColors.PurpleButton
                    ),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius))
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))

                Button(
                    onClick = { navController.navigate(Destination.Login.route) },
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.startup_button_width))
                        .height(dimensionResource(R.dimen.startup_button_height))
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius))),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        color = PalateColors.PurpleButton,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
