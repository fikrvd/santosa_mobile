package com.example.bagian_c_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "dashboard"
                ){

                    composable("dashboard"){

                        SantosaDashboard(
                            navController
                        )

                    }


                    composable("patients"){

                        PatientsScreen()

                    }
                    composable("visits"){
                        VisitsScreen()
                    }

                }

            }

        }
    }
}


data class SummaryItem(
    val title: String,
    val value: String
)


@Composable
fun SantosaDashboard(
    navController: NavController
) {

    var items by remember {
        mutableStateOf<List<SummaryItem>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }


    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {

        scope.launch {


            try {


                val result = withContext(Dispatchers.IO) {


                    val url = URL(
                        "http://192.168.163.166:9060/api/dashboard/summary"
                    )


                    val connection =
                        url.openConnection() as HttpURLConnection


                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000


                    val response =
                        connection.inputStream
                            .bufferedReader()
                            .use {
                                it.readText()
                            }


                    connection.disconnect()


                    response

                }



                val json = JSONObject(result)

                val data =
                    json.getJSONObject("data")


                items = listOf(

                    SummaryItem(
                        "Total Pasien",
                        data.optString("total_patient")
                    ),

                    SummaryItem(
                        "Total Kunjungan",
                        data.optString("total_visit")
                    ),

                    SummaryItem(
                        "Selesai",
                        data.optString("completed_visit")
                    ),

                    SummaryItem(
                        "Batal",
                        data.optString("cancelled_visit")
                    ),

                    SummaryItem(
                        "Terdaftar",
                        data.optString("registered_visit")
                    ),

                    SummaryItem(
                        "Berlangsung",
                        data.optString("in_progress_visit")
                    ),

                    SummaryItem(
                        "Total Dokter",
                        data.optString("total_doctor")
                    )

                )


                loading = false



            } catch(e: Exception){


                error =
                    e.message ?: "Gagal mengambil data"

                loading = false

            }


        }

    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F6F9)
    ) {


        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ){


            Text(
                text = "Dashboard Santosa",
                fontSize = 28.sp
            )


            Text(
                text = "Ringkasan Rumah Sakit",
                color = Color.Gray
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            Button(
                onClick = {
                    navController.navigate("patients")
                }
            ){
                Text("Data Pasien")
            }

            Button(
                onClick = {
                    navController.navigate("visits")
                }
            ){
                Text("Data Kunjungan")
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )



            if(loading){

                CircularProgressIndicator()

                Text(
                    "Memuat data..."
                )

            }
            else if(error.isNotEmpty()){

                Text(
                    text = error,
                    color = Color.Red
                )

            }
            else{


                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)

                ){


                    items(items){ item ->


                        Card(
                            shape =
                                RoundedCornerShape(12.dp),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        Color.White
                                )

                        ){

                            Column(
                                modifier =
                                    Modifier.padding(16.dp)

                            ){

                                Text(
                                    text=item.title,
                                    fontSize=14.sp,
                                    color=Color.Gray
                                )


                                Spacer(
                                    Modifier.height(10.dp)
                                )


                                Text(
                                    text=item.value,
                                    fontSize=26.sp
                                )

                            }

                        }


                    }

                }

            }

        }

    }

}