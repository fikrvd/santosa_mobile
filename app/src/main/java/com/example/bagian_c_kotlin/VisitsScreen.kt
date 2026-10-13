package com.example.bagian_c_kotlin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


data class Visit(
    val id: String,
    val rm: String,
    val patient: String,
    val status: String
)


@Composable
fun VisitsScreen(){

    var visits by remember {
        mutableStateOf<List<Visit>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }


    LaunchedEffect(Unit){

        try {

            val result = withContext(Dispatchers.IO){

                URL(
                    "http://192.168.163.166:9060/api/visits"
                ).readText()

            }


            val json = JSONObject(result)

            val array = json.getJSONArray("data")

            val list = mutableListOf<Visit>()


            for(i in 0 until array.length()){

                val item = array.getJSONObject(i)

                val patient =
                    item.getJSONObject("patient")


                list.add(
                    Visit(
                        id = item.optString("visit_no"),
                        rm = patient.optString("medical_record_no"),
                        patient = patient.optString("name"),
                        status = item.optString("status")
                    )
                )
            }


            visits = list
            loading = false


        } catch(e: Exception){

            loading = false

        }

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ){

        Text(
            "Data Kunjungan",
            style = MaterialTheme.typography.headlineMedium
        )


        Spacer(
            Modifier.height(20.dp)
        )


        if(loading){

            CircularProgressIndicator()

        } else {


            LazyColumn{


                items(visits){ visit ->


                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)

                    ){

                        Column(

                            modifier = Modifier
                                .padding(15.dp)

                        ){

                            Text(
                                "Visit : ${visit.id}"
                            )

                            Text(
                                "RM : ${visit.rm}"
                            )

                            Text(
                                "Pasien : ${visit.patient}"
                            )

                            Text(
                                "Status : ${visit.status}"
                            )

                        }

                    }

                }

            }

        }

    }

}